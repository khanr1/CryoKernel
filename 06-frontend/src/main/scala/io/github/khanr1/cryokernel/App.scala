package io.github.khanr1
package cryokernel

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom
import io.github.khanr1.cryokernel.api.MessageAPI
import io.github.khanr1.cryocore.components.navbar.navBar

@main
def main(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    div(
      navBar("Cryokernel", List("Materials", "Heatload"))
    )
  )
