package io.github.khanr1.cryokernel
package materials

import cats.effect.IO
import cats.syntax.all.*
import io.circe.syntax.*
import squants.thermal.Kelvin
import weaver.SimpleIOSuite

// Bring the Circe codecs and identifier instances into scope.
import io.github.khanr1.cryokernel.ID.given
import io.github.khanr1.cryokernel.materials.Material.given

object MaterialSuite extends SimpleIOSuite:

  test("Material enumeration exposes stable identifiers and names"):
    IO {
      expect.all(
        Material.SCuNi.id.value == 1,
        Material.SCuNi.name == "SCuNi",
        Material.CuNi.id.value == 2,
        Material.CuNi.name == "CuNi",
        Material.PTFE.id.value == 3,
        Material.PTFE.name == "PTFE"
      )
    }

  test("Material thermal conductivity models retain coefficients and ranges"):
    IO {
      val scuNiModel = Material.SCuNi.thermalConductivityModel.get
      val cuNiModel = Material.CuNi.thermalConductivityModel.get
      val ptfeModel = Material.PTFE.thermalConductivityModel.get

      expect.all(
        scuNiModel.model == "NIST",
        scuNiModel.coef == List(-2.750, 25.845, -74.184, 113.586, -96.844,
          46.383, -11.825, 1.322, -0.025),
        scuNiModel.validityRange.lower == Kelvin(2.3),
        scuNiModel.validityRange.upper == Kelvin(292.6),
        cuNiModel.coef == List(-3.198, 20.499, -66.111, 117.684, -121.471,
          76.210, -28.747, 5.984, -0.527),
        cuNiModel.validityRange.lower == Kelvin(2.0),
        cuNiModel.validityRange.upper == Kelvin(297.6),
        ptfeModel.coef == List(2.7380, -30.677, 89.430, -136.99, 124.69,
          -69.556, 23.320, -4.3135, 0.33829),
        ptfeModel.validityRange.lower == Kelvin(4.0),
        ptfeModel.validityRange.upper == Kelvin(300.0)
      )
    }

  test("Material JSON codec preserves identifier, name and conductivity model"):
    IO {
      val materials = List(Material.SCuNi, Material.CuNi, Material.PTFE)

      val roundTripped = materials.traverse { material =>
        material.asJson.as[Material].map { decoded =>
          val modelInfo = decoded.thermalConductivityModel.map { model =>
            (
              model.model,
              model.coef,
              model.validityRange.lower.toKelvinDegrees,
              model.validityRange.upper.toKelvinDegrees,
              model.kappa(Kelvin(10)).toWattsPerMeterKelvin
            )
          }

          (decoded.id.value, decoded.name, modelInfo)
        }
      }

      val expected = materials.map { material =>
        val modelInfo = material.thermalConductivityModel.map { model =>
          (
            model.model,
            model.coef,
            model.validityRange.lower.toKelvinDegrees,
            model.validityRange.upper.toKelvinDegrees,
            model.kappa(Kelvin(10)).toWattsPerMeterKelvin
          )
        }

        (material.id.value, material.name, modelInfo)
      }

      expect(roundTripped == Right(expected))
    }
