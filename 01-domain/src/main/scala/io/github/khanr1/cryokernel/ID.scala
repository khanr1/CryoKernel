package io.github.khanr1.cryokernel

import monocle.Iso

/** Lightweight typed identifier wrapper.
  *
  * The `ID[A]` opaque type lets the codebase distinguish identifiers belonging
  * to different aggregates purely at the type level (e.g. `ID[User]` versus
  * `ID[Order]`) while still compiling down to a raw `Int`. Instances are created
  * with [[ID.apply]] and the underlying integer can be retrieved with the
  * extension method [[ID.value]] or the provided `Iso`.
  *
  * JSON codecs and key encoders/decoders are supplied so the identifier can be
  * serialised transparently when talking to external systems.
  */
opaque type ID[A] = Int
object ID:
  def apply[A](i: Int): ID[A] = i
  extension [A](m: ID[A]) def value: Int = m
  given [A]: Encoder[ID[A]] = Encoder.encodeInt.contramap(_.value)
  given [A]: Decoder[ID[A]] = Decoder.decodeInt.map(apply[A](_))
  given [A]: KeyEncoder[ID[A]] = KeyEncoder.encodeKeyInt.contramap(a => a.value)
  given [A]: KeyDecoder[ID[A]] = KeyDecoder.decodeKeyInt.map(a => ID.apply(a))
  given [A]: Iso[Int, ID[A]] = Iso[Int, ID[A]](i => ID[A](i))(id => id.value)
