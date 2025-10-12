package io.github.khanr1
package cryokernel
package services

import io.github.khanr1.cryokernel.Message
import io.github.khanr1.cryokernel.repositories.*

/** Public interface exposing high-level message operations. */
trait MessageServices[F[_]] {
  /** Retrieves the message that should be shared with API consumers. */
  def sayHello: F[Message]
}

object MessageServices:
  /** Creates a [[MessageServices]] backed by the provided repository. */
  def make[F[_]](repo: MessageRepository[F]): MessageServices[F] =
    new MessageServices[F] {

      override def sayHello: F[Message] = repo.getMessage

    }
