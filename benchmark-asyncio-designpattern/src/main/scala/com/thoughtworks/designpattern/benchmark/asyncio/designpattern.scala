package com.thoughtworks.designpattern.benchmark.asyncio

import com.thoughtworks.designpattern._
import continuation._
import covariant._
import either._

import scala.concurrent.ExecutionContext
import scala.util.Try
import scala.util.control.NonFatal

object designpattern {

  object AsyncIO extends EitherMonadErrorFactoryDecorator with EitherIOFactoryDecorator with BoxCompanion {
    type UnderlyingFactory = UnitContinuation.type
    val underlyingFactory: UnderlyingFactory = UnitContinuation
    implicit final class Facade[+A](val value: Value[A]) extends AnyVal with MonadErrorDecorator[A] {
      def blockingAwait(): A = underlyingFactory.Facade(value).blockingAwait().left.map(throw _).merge
    }

    def execute[A](io: () => A)(implicit executionContext: ExecutionContext): Facade[A] = Facade { continue =>
      executionContext.execute { () =>
        continue(try {
          Right(io())
        } catch {
          case NonFatal(e) => Left(e)
        })
      }
    }
  }

  /** @template */
  type AsyncIO[+A] = AsyncIO.Facade[A]
}
