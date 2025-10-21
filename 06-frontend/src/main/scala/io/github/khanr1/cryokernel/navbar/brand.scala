package io.github.khanr1.cryocore
package components
package navbar

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.defs.attrs.AriaAttrs
import com.raquo.laminar.keys.AriaAttr
import com.raquo.laminar.nodes.ReactiveElement

def brand(brandName: String) = a(
  cls := "navbar-brand",
  href := "#",
  brandName
)
