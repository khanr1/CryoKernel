package io.github.khanr1.cryokernel
package materials

import cats.Show
import io.circe.{Decoder, DecodingFailure, Encoder}
import io.github.khanr1.cryokernel.properties.ThermalConductivityModel
import squants.QuantityRange
import squants.thermal.Kelvin

/** Enumerates the materials supported by the domain model.
  *
  * Each entry captures a stable [[ID]] for persistence, a displayable name, and
  * an optional [[ThermalConductivityModel]] describing how the material
  * conducts heat across a temperature range. When the conductivity model is
  * absent the material lacks tabulated data and downstream computations must
  * handle that case explicitly.
  *
  * @param id
  *   Stable identifier used when serialising or exporting the material.
  * @param name
  *   Human-readable name or shorthand for the material.
  * @param thermalConductivityModel
  *   Optional thermal conductivity correlation for the material.
  */
enum Material(
    val id: ID[Material],
    val name: String,
    val thermalConductivityModel: Option[ThermalConductivityModel] = None
):
  case SCuNi
      extends Material(
        ID[Material](1),
        "SCuNi",
        Some(
          ThermalConductivityModel.NIST(
            List(-2.750, 25.845, -74.184, 113.586, -96.844, 46.383, -11.825,
              1.322, -0.025),
            QuantityRange(Kelvin(2.3), Kelvin(292.6))
          )
        )
      )
  case CuNi
      extends Material(
        ID[Material](2),
        "CuNi",
        Some(
          ThermalConductivityModel.NIST(
            List(-3.198, 20.499, -66.111, 117.684, -121.471, 76.210, -28.747,
              5.984, -0.527),
            QuantityRange(Kelvin(2.0), Kelvin(297.6))
          )
        )
      )
  case PTFE
      extends Material(
        ID[Material](3),
        "PTFE",
        Some(
          ThermalConductivityModel.NIST(
            List(2.7380, -30.677, 89.430, -136.99, 124.69, -69.556, 23.320,
              -4.3135, 0.33829),
            QuantityRange(Kelvin(4.0), Kelvin(300.0))
          )
        )
      )
  case Brass
      extends Material(
        ID[Material](4),
        "Brass",
        Some(
          ThermalConductivityModel.NIST(
            List(0.021035, -1.01835, 4.54083, -5.03374, 3.20536, -1.12933,
              0.174057, -0.0038151, 0),
            QuantityRange(Kelvin(5), Kelvin(116.0))
          )
        )
      )

/** Typeclass instances for serialising and formatting [[Material]] values. */
object Material:
  given show[MaterialID]: Show[Material] = Show.fromToString
  given encoder[MaterialID: Encoder]: Encoder[Material] =
    Encoder.forProduct3("id", "name", "thermalConductivity")(m =>
      (m.id, m.name, m.thermalConductivityModel)
    )
  given decoder[MaterialID: Decoder]: Decoder[Material] =
    Decoder.instance { cursor =>
      for
        id <- cursor.get[ID[Material]]("id")
        name <- cursor.get[String]("name")
        // Decode the conductivity payload to ensure it matches the schema,
        // even though we re-use the canonical instances defined on the enum.
        _ <- cursor.get[Option[ThermalConductivityModel]]("thermalConductivity")
        material <- Material.values
          .find(_.id == id)
          .toRight(
            DecodingFailure(
              s"Unknown material identifier ${id.value}",
              cursor.history
            )
          )
        _ <-
          if material.name == name then Right(())
          else
            Left(
              DecodingFailure(
                s"Material name mismatch for id ${id.value}: expected ${material.name}, found $name",
                cursor.history
              )
            )
      yield material
    }
