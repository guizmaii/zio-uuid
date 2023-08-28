package zio.uuid

import zio.prelude.Subtype

import java.util.UUID

object types {

  type UUIDv1 = UUIDv1.Type
  object UUIDv1 extends Subtype[UUID] {
    private[zio] def unsafe(uuid: UUID): Type = wrap(uuid)
  }

  type UUIDv6 = UUIDv6.Type
  object UUIDv6 extends Subtype[UUID] {
    private[zio] def unsafe(uuid: UUID): Type = wrap(uuid)
  }

  type UUIDv7 = UUIDv7.Type
  object UUIDv7 extends Subtype[UUID] {
    private[zio] def unsafe(uuid: UUID): Type = wrap(uuid)
  }

}
