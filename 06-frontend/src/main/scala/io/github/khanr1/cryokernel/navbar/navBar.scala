package io.github.khanr1.cryocore
package components
package navbar

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.defs.attrs.AriaAttrs
import com.raquo.laminar.keys.AriaAttr
import com.raquo.laminar.nodes.ReactiveElement

def navBar(brandName: String, items: List[String]) = div(
  cls := ("navbar", "navbar-expand-md", "navbar-dark", "bg-dark"),
  aria.label("main navigation bar"),
  div(
    cls := "container-fluid",
    brand(brandName),
    navBarTogglerButton("#navbarSupportedContent"),
    div(
      cls := ("collapse", "navbar-collapse"),
      idAttr := "navbarSupportedContent",
      menuItems(items)
    )
  )
)
