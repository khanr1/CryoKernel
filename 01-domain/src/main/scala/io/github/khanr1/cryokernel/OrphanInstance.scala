package io.github.khanr1.cryokernel

import io.circe.Decoder
import io.circe.Encoder
import squants.thermal.*
import cats.syntax.all.*
import io.circe.Json
import io.circe.HCursor
import io.circe.Decoder.Result
import io.circe.DecodingFailure
import squants.QuantityParseException
import io.github.khanr1.cryokernel.units.ThermalConductivity
import io.github.khanr1.cryokernel.units.WattsPerMeterKelvin
import squants.space.Length
import scala.util.Failure
import scala.util.Success
import squants.space.Meters
import squants.space.Millimeters
import squants.space.Microns
import squants.space.Nanometers
import squants.QuantityRange
import squants.Quantity

object OrphanInstances:
  // Encoder and Decoder for Temperature
  given Encoder[Temperature] = new Encoder[Temperature] {

    override def apply(a: Temperature): Json = Encoder.encodeString(a.toString)

  }
  given Decoder[Temperature] = new Decoder[Temperature] {

    override def apply(c: HCursor): Result[Temperature] =
      val pattern = """^(-?\d+(\,\d+)?)[ ](f|F|c|C|k|K|r|R)$""".r
      c.as[String]
        .flatMap(x =>
          x match
            case pattern(value, _, unit) =>
              unit match
                case "f" | "F" =>
                  Right(Fahrenheit(value.replace(",", ".").toDouble))
                case "c" | "C" =>
                  Right(Celsius(value.replace(",", ".").toDouble))
                case "k" | "K" =>
                  Right(Kelvin(value.replace(",", ".").toDouble))
                case "r" | "R" =>
                  Right(Rankine(value.replace(",", ".").toDouble))
                case other =>
                  Left(
                    DecodingFailure(
                      s"Invalide temperature unit format. The input $other cannot be parsed.",
                      c.history
                    )
                  )
            case other =>
              Left(
                DecodingFailure(
                  s"Invalid temperature format. The input $other cannot be parsed",
                  c.history
                )
              )
        )

  }
  // Encoder and Decoder for the Range
  given encoder[A <: Quantity[A]](using Encoder[A]): Encoder[QuantityRange[A]] =
    Encoder.forProduct2("lower", "upper")(range => (range.lower, range.upper))
  given decoder[A <: Quantity[A]](using Decoder[A]): Decoder[QuantityRange[A]] =
    Decoder.forProduct2("lower", "upper")((l: A, u: A) => QuantityRange(l, u))
  // Encoder and Decoder for the Thermal Conductivity
  given Encoder[ThermalConductivity] = new Encoder[ThermalConductivity] {

    override def apply(a: ThermalConductivity): Json =
      Encoder.encodeString(a.toString)

  }
  given Decoder[ThermalConductivity] = new Decoder[ThermalConductivity] {

    override def apply(c: HCursor): Result[ThermalConductivity] =
      val pattern = """^(-?\d+(\,\d+)?)[ ](W/\(m.K\))$""".r
      c.as[String]
        .flatMap(x =>
          x match
            case pattern(value, _, unit) =>
              Right(WattsPerMeterKelvin(value.replace(",", ".").toDouble))
            case other =>
              Left(
                DecodingFailure(
                  s"Invalid temperature format. The input $other cannot be parsed",
                  c.history
                )
              )
        )

  }
  // Encoder and Decoder for the Length
  given Encoder[Length] = new Encoder[Length] {

    override def apply(a: Length): Json = Encoder.encodeString(a.toString)

  }
  given Decoder[Length] = Decoder.instance[Length] { (c: HCursor) =>
    val pattern = """^(-?\d+(\,\d+)?)[ ](m|mm|µm|nm)$""".r
    c.as[String]
      .flatMap(x =>
        x match
          case pattern(value, _, unit) =>
            unit match
              case "m" =>
                Right(Meters(value.replace(",", ".").toDouble))
              case "mm" =>
                Right(Millimeters(value.replace(",", ".").toDouble))
              case "µm" =>
                Right(Microns(value.replace(",", ".").toDouble))
              case "nm" =>
                Right(Nanometers(value.replace(",", ".").toDouble))
              case other =>
                Left(
                  DecodingFailure(
                    s"Invalide temperature unit format. The input $other cannot be parsed.",
                    c.history
                  )
                )
          case other =>
            Left(
              DecodingFailure(
                s"Invalid temperature format. The input $other cannot be parsed",
                c.history
              )
            )
      )
  }
