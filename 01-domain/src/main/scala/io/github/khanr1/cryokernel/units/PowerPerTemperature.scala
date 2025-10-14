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

final class PowerPerTemperature private (
    val value: Double,
    val unit: PowerPerTemperatureUnit
) extends Quantity[PowerPerTemperature]:

  /** Returns the dimension associated with this quantity.
    *
    * @return
    *   The [[Dimension]] of PowerPerTemperature.
    */

  def dimension: Dimension[PowerPerTemperature] = PowerPerTemperature

object PowerPerTemperature extends Dimension[PowerPerTemperature]:
  /** Create a ThermalConductivity quantity from a numeric value and a specified
    * unit.
    *
    * @param n
    *   The numeric value to be converted into a ThermalConductivity.
    * @param unit
    *   The [[PowerPerTemperatureUnit]] that this quantity is measured in.
    * @param num
    *   Implicit numeric typeclass for the value.
    * @tparam A
    *   The numeric type of the input value.
    * @return
    *   A new [[PowerPerTemperature]] instance.
    */
  def apply[A](n: A, unit: PowerPerTemperatureUnit)(using num: Numeric[A]) =
    new PowerPerTemperature(num.toDouble(n), unit)

  /** @return
    *   The name of this dimension.
    */
  override def name: String = "Power per  Temperature"

  /** @return
    *   The primary (and also SI) unit for PowerPerTemperature.
    */
  override def primaryUnit: UnitOfMeasure[PowerPerTemperature] & PrimaryUnit =
    WattsPerKelvin

  /** @return
    *   The SI unit for PowerPerTemperature.
    */
  override def siUnit: UnitOfMeasure[PowerPerTemperature] & SiUnit =
    WattsPerKelvin

  /** @return
    *   The set of all units defined for PowerPerTemperature.
    */
  override def units: Set[UnitOfMeasure[PowerPerTemperature]] = Set(
    WattsPerKelvin
  )

/** Base trait for units of [[PowerPerTemperature]].
  *
  * Implementations of this trait define how to convert from a numeric value
  * into a PowerPerTemperature quantity.
  */
trait PowerPerTemperatureUnit
    extends UnitOfMeasure[PowerPerTemperature]
    with UnitConverter:

  /** Create a PowerPerTemperature quantity in this unit from a numeric value.
    *
    * @param n
    *   The numeric value.
    * @param num
    *   Implicit numeric typeclass for the value.
    * @tparam N
    *   The numeric type of the input.
    * @return
    *   A [[PowerPerTemperature]] instance in this unit.
    */
  def apply[N](n: N)(implicit num: Numeric[N]): PowerPerTemperature =
    PowerPerTemperature(n, this)

object WattsPerKelvin
    extends PowerPerTemperatureUnit
    with PrimaryUnit
    with SiUnit:

  /** @return
    *   The symbol for the Watts per Kelvin unit.
    */
  override def symbol: String = "W/K"
