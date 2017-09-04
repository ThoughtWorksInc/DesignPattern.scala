package com.thoughtworks.designpattern.benchmark

import com.thoughtworks.designpattern.continuation.UnitContinuation
import com.thoughtworks.designpattern.covariant.BoxCompanion
import com.thoughtworks.designpattern.either._

import scala.concurrent.ExecutionContext
import scala.util.Try

object designpatternasyncio {

  object AsyncIO extends MonadErrorFactoryDecorator with IOFactoryDecorator with BoxCompanion {
    type UnderlyingFactory = UnitContinuation.type
    val underlyingFactory: UnderlyingFactory = UnitContinuation
    implicit final class Facade[+A](val value: Value[A]) extends AnyVal with MonadErrorDecorator[A] {
      def blockingAwait(): A = underlyingFactory.Facade(value).blockingAwait().toTry.get
    }

    def execute[A](io: () => A)(implicit executionContext: ExecutionContext): Facade[A] = Facade { continue =>
      executionContext.execute { () =>
        continue(Try(io()).toEither)
      }
    }
  }

  /** @template */
  type AsyncIO[+A] = AsyncIO.Facade[A]
}
