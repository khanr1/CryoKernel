package io.github.khanr1.cryokernel
package properties

import io.circe.syntax.*
import io.github.khanr1.cryokernel.units.ThermalConductivity
import io.github.khanr1.cryokernel.units.WattsPerMeterKelvin
import squants.QuantityRange
import squants.thermal.Temperature

/** Encapsulates a parameterised model that expresses thermal conductivity as a
  * function of temperature.
  *
  * Implementations provide a human-readable name (`model`), a function
  * (`kappa`) that transforms a temperature into a [[ThermalConductivity]]
  * quantity, the coefficients backing the function, and the temperature range
  * for which the model is considered valid. Concrete models can be created
  * through helpers in the companion object (e.g. constant, linear, or empirical
  * NIST fits).
  */
trait ThermalConductivityModel:
  def model: String
  def kappa: Temperature => ThermalConductivity
  def coef: List[Double]
  def validityRange: QuantityRange[Temperature]

/** Helper constructors and Circe codecs for [[ThermalConductivityModel]]
  * instances.
  *
  * The provided encoder/decoder serialise models using a simple product
  * representation (`model`, `coefficient`, `validityRange`) so that data
  * sources can persist coefficient sets alongside their applicable temperature
  * range.
  */
object ThermalConductivityModel:
  given Encoder[ThermalConductivityModel] =
    Encoder.forProduct3("model", "coefficient", "validityRange")(tc =>
      (tc.model, tc.coef, tc.validityRange)
    )
  given Decoder[ThermalConductivityModel] =
    Decoder.forProduct3("model", "coefficient", "validityRange")(
      (model: String, coef: List[Double], range: QuantityRange[Temperature]) =>
        coef match
          case List(a)    => ThermalConductivityModel.constant(a, range)
          case List(a, b) => ThermalConductivityModel.linear(a, b, range)
          case List(a, b, c, d, e, f, g, h, i) =>
            ThermalConductivityModel.NIST(coef, range)
          case _ =>
            throw new IllegalArgumentException(
              "No Thermal conductivity models matches the given coefficient and or range"
            )
    )

  /** Creates a model with a temperature-independent conductivity. */
  def constant(
      a: Double,
      range: QuantityRange[Temperature]
  ): ThermalConductivityModel = new ThermalConductivityModel {

    override def kappa: Temperature => ThermalConductivity = _ =>
      ThermalConductivity(a, WattsPerMeterKelvin)
    override def coef: List[Double] = List(a)
    override def validityRange: QuantityRange[Temperature] = range
    override def model: String = "constant"

  }

  /** Creates a linear model of the form `k(T) = a * T + b`. */
  def linear(
      a: Double,
      b: Double,
      range: QuantityRange[Temperature]
  ): ThermalConductivityModel = new ThermalConductivityModel {

    override def kappa: Temperature => ThermalConductivity = (t: Temperature) =>
      ThermalConductivity((t.value) * a + b, WattsPerMeterKelvin)
    override def coef: List[Double] = List(a, b)
    override def validityRange: QuantityRange[Temperature] = range
    override def model: String = "linear"

  }

  /** Creates the NIST logarithmic polynomial fit used for cryogenic materials.
    *
    * The model expects nine coefficients representing the polynomial on
    * `log10(T[K])` as published by NIST. Values outside the validity range are
    * clamped to the lower bound and scaled proportionally.
    */
  def NIST(
      l: List[Double],
      range: QuantityRange[Temperature]
  ) = new ThermalConductivityModel {

    override def kappa: Temperature => ThermalConductivity =
      (temperature: Temperature) => {
        val effectiveTemperature: Temperature =
          if (temperature > range.lower) then temperature else range.lower
        // Calculate the logarithmic polynomial exponent
        val logT = math.log10(effectiveTemperature.toKelvinDegrees)
        val exponent = coef.zipWithIndex.map { case (coef, index) =>
          coef * math.pow(logT, index)
        }.sum

        // Calculate the thermal conductivity
        val baseConductivity =
          ThermalConductivity(math.pow(10, exponent), WattsPerMeterKelvin)
        // Adjust for temperatures below the validity.
        // We assume a linear relation between the Temperature and Kappa bleow the lower temperature range.
        if (temperature > range.lower) baseConductivity
        else baseConductivity * (temperature / range.lower)

      }

    override def coef: List[Double] = l
    override def validityRange: QuantityRange[Temperature] = range
    override def model: String = "NIST"

  }
