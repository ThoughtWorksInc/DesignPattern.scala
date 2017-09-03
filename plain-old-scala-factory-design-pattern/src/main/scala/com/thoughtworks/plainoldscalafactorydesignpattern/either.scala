package com.thoughtworks.plainoldscalafactorydesignpattern
import com.thoughtworks.plainoldscalafactorydesignpattern.continuation.UnitContinuation

import language.higherKinds
import com.thoughtworks.plainoldscalafactorydesignpattern.{IOFactory => Base, MonadErrorFactory => BaseMonadErrors}

/**
  * @author 杨博 (Yang Bo)
  */
object either {

  trait BoxDecoratorFactory extends BoxFactory {
    type Underlying <: BoxFactory
    val underlying: Underlying

    type Error
    type Unboxed[+A] = underlying.Unboxed[Either[Error, A]]
  }

  trait MonadErrorDecoratorFactory extends BaseMonadErrors with BoxDecoratorFactory {
    type Underlying <: MonadFactory with BoxFactory {
      type Facade[+A] <: Monad[A] with Box[A]
    }

    type Facade[+A] <: MonadErrorDecorator[A]

    trait MonadErrorDecorator[+A] extends Any with MonadError[A] with Box[A] {
      def handleError[B >: A](catcher: Error => Facade[B]): Facade[B] = Facade {
        underlying
          .Facade(unbox)
          .flatMap {
            case Left(e) =>
              underlying.Facade(catcher(e).unbox)
            case right: Right[Error, B] =>
              underlying.pure(right)
          }
          .unbox
      }

      def flatMap[B](mapper: (A) => Facade[B]): Facade[B] = Facade {
        underlying
          .Facade(unbox)
          .flatMap {
            case Right(a) =>
              underlying.Facade(mapper(a).unbox)
            case Left(e) =>
              underlying.pure(Left(e))
          }
          .unbox
      }
    }

    def raiseError[A](e: Error): Facade[A] = Facade(underlying.pure(Left(e)).unbox)

    def pure[A](a: A): Facade[A] = Facade(underlying.pure(Right(a)).unbox)

  }

  trait IODecoratorFactory extends IOFactory with BoxDecoratorFactory {

    type Underlying <: IOFactory with BoxFactory

    def liftIO[A](io: () => A): Facade[A] = Facade(underlying.liftIO(() => Right(io())).unbox)

  }

  object Task extends MonadErrorDecoratorFactory with IODecoratorFactory with BoxCompanion {
    type Error = Throwable
    type Underlying = continuation.UnitContinuation.type
    val underlying: Underlying = UnitContinuation
    implicit final class Facade[+A](val unbox: Unboxed[A]) extends AnyVal with MonadErrorDecorator[A]
  }
  type Task[+A] = Task.Facade[A]
}
