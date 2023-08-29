package zio.uuid.internals

import zio.{Ref, UIO, ZIO}

import java.util.concurrent.TimeUnit

private[zio] final case class GeneratorState(lastUsedEpochMillis: Long, sequence: Long)

private[zio] object GeneratorState {
  val initial: GeneratorState = GeneratorState(lastUsedEpochMillis = 0L, sequence = 0L)
}

private[zio] object UUIDGeneratorBuilder {
  type UUIDBuilder[UUIDvX] = (Long, Long, Long) => UUIDvX

  def generate[UUIDvX](
    state: Ref.Synchronized[GeneratorState],
    builder: UUIDBuilder[UUIDvX],
  ): UIO[UUIDvX] =
    for {
      random <- ZIO.random.flatMap(_.nextLong)
      uuid   <-
        for {
          modifiedState <- state.modifyZIO { currentState =>
                             ZIO.clockWith(_.currentTime(TimeUnit.MILLISECONDS)).map { timestamp =>
                               // realTime clock may run backward
                               val actualTimestamp = Math.max(currentState.lastUsedEpochMillis, timestamp)
                               val sequence        =
                                 if (currentState.lastUsedEpochMillis == actualTimestamp) {
                                   currentState.sequence + 1
                                 } else 0L

                               val newState = GeneratorState(lastUsedEpochMillis = actualTimestamp, sequence = sequence)
                               (newState, newState)
                             }
                           }
        } yield builder(
          modifiedState.lastUsedEpochMillis,
          modifiedState.sequence,
          random,
        )
    } yield uuid

  // noinspection YieldingZIOEffectInspection
  def buildGenerator[UUIDvX](builder: UUIDBuilder[UUIDvX]): UIO[UIO[UUIDvX]] =
    for {
      state <- Ref.Synchronized.make(GeneratorState.initial)
    } yield generate(state, builder)
}
