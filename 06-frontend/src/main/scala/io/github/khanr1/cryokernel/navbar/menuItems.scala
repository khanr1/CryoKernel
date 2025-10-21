package io.github.khanr1.cryocore
package components
package navbar

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.defs.attrs.AriaAttrs
import com.raquo.laminar.keys.AriaAttr
import com.raquo.laminar.nodes.ReactiveElement

def menuItems(items: List[String]) =
  ul(
    cls := ("navbar-nav", "me-auto mb-2", "mb-lg-0"),
    for item <- items
    yield li(
      cls := "nav-item",
      a(
        cls := "nav-link",
        href := "#",
        item
      )
    )
  )
