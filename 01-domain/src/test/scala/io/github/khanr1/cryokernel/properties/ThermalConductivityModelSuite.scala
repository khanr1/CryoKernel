package io.github.khanr1.cryokernel
package properties

import cats.effect.IO
import cats.syntax.all.*
import io.circe.syntax.*
import squants.QuantityRange
import squants.thermal.Kelvin
import weaver.SimpleIOSuite

import io.github.khanr1.cryokernel.properties.ThermalConductivityModel.given

object ThermalConductivityModelSuite extends SimpleIOSuite:

  private val defaultRange =
    QuantityRange(Kelvin(10.0), Kelvin(20.0))

  private def approxEq(actual: Double, expected: Double, tolerance: Double = 1e-6): Boolean =
    math.abs(actual - expected) <= tolerance

  test("constant model keeps the same conductivity across temperatures"):
    IO {
      val constant = ThermalConductivityModel.constant(42.0, defaultRange)
      val warmConductivity = constant.kappa(Kelvin(15.0))
      val coldConductivity = constant.kappa(Kelvin(10.1))

      expect.all(
        warmConductivity.toWattsPerMeterKelvin == 42.0,
        coldConductivity.toWattsPerMeterKelvin == 42.0,
        constant.coef == List(42.0),
        constant.model == "constant",
        constant.validityRange == defaultRange
      )
    }

  test("linear model applies slope and intercept to temperature input"):
    IO {
      val linear = ThermalConductivityModel.linear(2.0, -4.0, defaultRange)
      val midPoint = linear.kappa(Kelvin(12.5))
      val upperPoint = linear.kappa(Kelvin(20.0))

      expect.all(
        midPoint.toWattsPerMeterKelvin == 21.0,
        upperPoint.toWattsPerMeterKelvin == 36.0,
        linear.coef == List(2.0, -4.0),
        linear.model == "linear"
      )
    }

  test("NIST model clamps low temperatures and rescales conductivity"):
    IO {
      val coefficients =
        List(0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
      val range = QuantityRange(Kelvin(10.0), Kelvin(50.0))
      val nist = ThermalConductivityModel.NIST(coefficients, range)

      val aboveLower = nist.kappa(Kelvin(20.0))
      val belowLower = nist.kappa(Kelvin(5.0))

      expect.all(
        approxEq(aboveLower.toWattsPerMeterKelvin, 20.0),
        approxEq(belowLower.toWattsPerMeterKelvin, 5.0),
        nist.coef == coefficients,
        nist.model == "NIST"
      )
    }

  test("JSON codec preserves ThermalConductivityModel definitions"):
    IO {
      val models = List(
        ThermalConductivityModel.constant(12.0, defaultRange),
        ThermalConductivityModel.linear(0.5, 1.0, defaultRange),
        ThermalConductivityModel.NIST(
          List(0.5, -0.2, 0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
          defaultRange
        )
      )

      val roundTripped = models.traverse { model =>
        model.asJson
          .as[ThermalConductivityModel]
          .map { decoded =>
            (
              decoded.model,
              decoded.coef,
              decoded.validityRange.lower.toKelvinDegrees,
              decoded.validityRange.upper.toKelvinDegrees,
              decoded.kappa(Kelvin(15.0)).toWattsPerMeterKelvin
            )
          }
      }

      val expected = models.map { model =>
        (
          model.model,
          model.coef,
          model.validityRange.lower.toKelvinDegrees,
          model.validityRange.upper.toKelvinDegrees,
          model.kappa(Kelvin(15.0)).toWattsPerMeterKelvin
        )
      }

      expect(roundTripped == Right(expected))
    }
