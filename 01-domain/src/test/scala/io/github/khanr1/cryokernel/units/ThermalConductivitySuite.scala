package io.github.khanr1.cryokernel
package units

import cats.effect.IO
import squants.energy.Watts
import squants.space.Meters
import squants.thermal.Kelvin
import weaver.SimpleIOSuite

object ThermalConductivitySuite extends SimpleIOSuite:

  test("ThermalConductivity stores value in watts per meter kelvin"):
    IO {
      val conductivity = ThermalConductivity(15.0, WattsPerMeterKelvin)
      expect.all(
        conductivity.unit == WattsPerMeterKelvin,
        conductivity.toWattsPerMeterKelvin == 15.0,
        conductivity.dimension == ThermalConductivity
      )
    }

  test("Multiplying by a length yields expected power per temperature"):
    IO {
      val conductivity = ThermalConductivity(10.0, WattsPerMeterKelvin)
      val span = Meters(2.0)

      val powerPerTemp = conductivity * span

      expect.all(
        powerPerTemp.unit == WattsPerKelvin,
        powerPerTemp.value == 20.0,
        powerPerTemp.dimension == PowerPerTemperature
      )
    }

  test("Thermal conductivity with temperature produces power per length and power"):
    IO {
      val conductivity = ThermalConductivity(5.0, WattsPerMeterKelvin)
      val deltaT = Kelvin(4.0)
      val run = Meters(3.0)

      val powerPerLength = conductivity * deltaT
      val totalPower = powerPerLength * run
      val combined = powerPerLength + new PowerPerLength(7.5)

      expect.all(
        powerPerLength.value == 20.0,
        totalPower.toWatts == 60.0,
        combined.value == 27.5
      )
    }
