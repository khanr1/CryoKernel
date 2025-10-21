package io.github.khanr1.cryokernel
package materials

import org.scalajs.dom.HTMLElement
import com.raquo.laminar.api.L.{*, given}

object MaterialCardElements {
  def titles: Material => HtmlElement = m => h1(m.name)
}
