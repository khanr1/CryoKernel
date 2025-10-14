package io.github.khanr1.cryokernel
package units

import squants.{
  Dimension,
  Length,
  PrimaryUnit,
  Quantity,
  SiUnit,
  UnitConverter,
  UnitOfMeasure
}
import squants.energy.{Power, Watts}
import squants.thermal.{Temperature, ThermalCapacityUnit}

/** Represents a quantity of Thermal Conductivity.
  *
  * Thermal conductivity is a physical property that indicates how well a
  * material can conduct heat. It is commonly measured in Watts per Meter per
  * Kelvin (W/(m·K)).
  *
  * @constructor
  *   Create a new ThermalConductivity quantity.
  * @param value
  *   The numeric value of the quantity.
  * @param unit
  *   The unit in which this quantity is measured.
  */
final class ThermalConductivity private (
    val value: Double,
    val unit: ThermalConductivityUnit
) extends Quantity[ThermalConductivity]:

  /** Returns the dimension associated with this quantity.
    *
    * @return
    *   The [[Dimension]] of ThermalConductivity.
    */
  def dimension: Dimension[ThermalConductivity] = ThermalConductivity

  /** Converts this ThermalConductivity to Watts per Meter per Kelvin.
    *
    * @return
    *   The value in W/(m·K).
    */
  def toWattsPerMeterKelvin: Double = value

  /** Multiplies this ThermalConductivity by a length, resulting in a
    * [[PowerPerTemperature]].
    *
    * The resulting quantity can be interpreted as how much power per degree
    * would pass through a segment of the given length under a unit temperature
    * gradient.
    *
    * @param length
    *   The length over which thermal conduction occurs.
    * @return
    *   A [[PowerPerTemperature]] instance.
    */
  def *(length: squants.space.Length): PowerPerTemperature =
    PowerPerTemperature(
      toWattsPerMeterKelvin * length.toMeters,
      WattsPerKelvin
    )

  /** Multiplies this ThermalConductivity by a temperature, resulting in a
    * [[PowerPerLength]].
    *
    * By applying a temperature first, you effectively get a quantity measured
    * in W/m, indicating the power transfer rate per unit length given a certain
    * temperature difference.
    *
    * @param temp
    *   The temperature difference.
    * @return
    *   A [[PowerPerLength]] instance.
    */
  def *(temp: squants.thermal.Temperature): PowerPerLength =
    new PowerPerLength(toWattsPerMeterKelvin * temp.toKelvinDegrees)

/** Companion object to [[ThermalConductivity]] defining its dimension and
  * units.
  *
  * This object provides a factory for creating ThermalConductivity instances
  * and defines the primary and SI unit, as well as the set of supported units.
  */
object ThermalConductivity extends Dimension[ThermalConductivity]:

  /** Create a ThermalConductivity quantity from a numeric value and a specified
    * unit.
    *
    * @param n
    *   The numeric value to be converted into a ThermalConductivity.
    * @param unit
    *   The [[ThermalConductivityUnit]] that this quantity is measured in.
    * @param num
    *   Implicit numeric typeclass for the value.
    * @tparam A
    *   The numeric type of the input value.
    * @return
    *   A new [[ThermalConductivity]] instance.
    */
  def apply[A](n: A, unit: ThermalConductivityUnit)(using num: Numeric[A]) =
    new ThermalConductivity(num.toDouble(n), unit)

  /** @return
    *   The name of this dimension.
    */
  override def name: String = "Thermal conductivity"

  /** @return
    *   The primary (and also SI) unit for ThermalConductivity.
    */
  override def primaryUnit: UnitOfMeasure[ThermalConductivity] & PrimaryUnit =
    WattsPerMeterKelvin

  /** @return
    *   The SI unit for ThermalConductivity.
    */
  override def siUnit: UnitOfMeasure[ThermalConductivity] & SiUnit =
    WattsPerMeterKelvin

  /** @return
    *   The set of all units defined for ThermalConductivity.
    */
  override def units: Set[UnitOfMeasure[ThermalConductivity]] = Set(
    WattsPerMeterKelvin
  )

/** Base trait for units of [[ThermalConductivity]].
  *
  * Implementations of this trait define how to convert from a numeric value
  * into a ThermalConductivity quantity.
  */
trait ThermalConductivityUnit
    extends UnitOfMeasure[ThermalConductivity]
    with UnitConverter:

  /** Create a ThermalConductivity quantity in this unit from a numeric value.
    *
    * @param n
    *   The numeric value.
    * @param num
    *   Implicit numeric typeclass for the value.
    * @tparam N
    *   The numeric type of the input.
    * @return
    *   A [[ThermalConductivity]] instance in this unit.
    */
  def apply[N](n: N)(implicit num: Numeric[N]): ThermalConductivity =
    ThermalConductivity(n, this)

/** Represents a unit of ThermalConductivity measured in Watts per Meter per
  * Kelvin.
  *
  * Symbol: W/(m·K)
  */
object WattsPerMeterKelvin
    extends ThermalConductivityUnit
    with PrimaryUnit
    with SiUnit:

  /** @return
    *   The symbol for the Watts per Meter per Kelvin unit.
    */
  override def symbol: String = "W/(m·K)"

/** Represents a derived quantity equivalent to Power/Temperature (e.g., W/K)
  * per a given length.
  *
  * This class is used as an intermediate calculation result when multiplying a
  * ThermalConductivity by a length.
  *
  * @param value
  *   The numeric value for power per temperature.
  */

////////////////////////:
////////////////////////
// final class PowerPerTemperature(val value: Double):

//   /** Multiply this quantity by a temperature to yield a
//     * [[squants.energy.Power]].
//     *
//     * @param temp
//     *   The temperature in Kelvin.
//     * @return
//     *   A power quantity in Watts.
//     */
//   def *(temp: squants.thermal.Temperature): squants.energy.Power =
//     squants.energy.Watts(value * temp.toKelvinDegrees)
////////////////////
/** Represents a derived quantity equivalent to Power/Length (e.g., W/m) for a
  * given temperature gradient.
  *
  * This class is used as an intermediate calculation result when multiplying a
  * ThermalConductivity by a temperature.
  *
  * @param value
  *   The numeric value for power per length.
  */
final class PowerPerLength(val value: Double):

  /** Multiply this quantity by a length to yield a [[squants.energy.Power]].
    *
    * @param length
    *   The length in meters.
    * @return
    *   A power quantity in Watts.
    */
  def *(length: squants.space.Length): squants.energy.Power =
    squants.energy.Watts(value * length.toMeters)

  def +(powerPerLength: PowerPerLength): PowerPerLength =
    PowerPerLength(value + powerPerLength.value)
