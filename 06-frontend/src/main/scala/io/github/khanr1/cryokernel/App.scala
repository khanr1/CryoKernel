package io.github.khanr1
package cryokernel

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom
import io.github.khanr1.cryokernel.api.MessageAPI

@main
def main(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    div(
      "If you see text below the code is working",
      div(
        child.text <-- MessageAPI.getMessage
          .toSignal("loading")
      )
    )
  )
