package com.thoughtworks.designpattern.benchmark.asyncio

import com.thoughtworks.designpattern._, continuation._, covariant._, either._

import scala.concurrent.ExecutionContext
import scala.util.Try

object designpattern {

  object AsyncIO extends EitherMonadErrorFactoryDecorator with EitherIOFactoryDecorator with BoxCompanion {
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
