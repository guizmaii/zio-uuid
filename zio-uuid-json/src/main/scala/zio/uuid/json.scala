package zio.uuid

import zio.json.JsonCodec
import zio.uuid.types.{UUIDv1, UUIDv6, UUIDv7}

object json {

  implicit val UUIDv1Codec: JsonCodec[UUIDv1] = UUIDv1.wrapAll(JsonCodec.uuid)
  implicit val UUIDv6Codec: JsonCodec[UUIDv6] = UUIDv6.wrapAll(JsonCodec.uuid)
  implicit val UUIDv7Codec: JsonCodec[UUIDv7] = UUIDv7.wrapAll(JsonCodec.uuid)

  implicit val typeIDCodec: JsonCodec[TypeID] =
    JsonCodec.string.transformOrFail[TypeID](TypeID.decode(_).toEitherWith(_.mkString(", ")), _.value)

}
