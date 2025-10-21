package io.github.khanr1.cryocore
package components
package navbar

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.defs.attrs.AriaAttrs
import com.raquo.laminar.keys.AriaAttr
import com.raquo.laminar.nodes.ReactiveElement

def navBarTogglerButton(target: String) = button(
  cls := "navbar-toggler",
  tpe := "button",
  dataAttr("bs-toggle") := "collapse",
  dataAttr("bs-target") := target,
  aria.controls := "navbarSupportedContent",
  aria.expanded := false,
  aria.label := "Toggle navigation",
  span(cls := "navbar-toggler-icon")
)
