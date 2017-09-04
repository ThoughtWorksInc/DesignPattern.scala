package com.thoughtworks.plainoldscalafactorydesignpattern
import com.thoughtworks.plainoldscalafactorydesignpattern.continuation.UnitContinuation
import com.thoughtworks.plainoldscalafactorydesignpattern.covariant._

import language.higherKinds

/**
  * @author 杨博 (Yang Bo)
  */
object either {

  trait BoxFactoryDecorator extends BoxFactory {
    type UnderlyingFactory <: BoxFactory
    val underlyingFactory: UnderlyingFactory

    type Error
    type Value[+A] = underlyingFactory.Value[Either[Error, A]]
  }

  trait MonadErrorFactoryDecorator extends MonadErrorFactory with BoxFactoryDecorator {
    type UnderlyingFactory <: MonadFactory with BoxFactory {
      type Facade[+A] <: Monad[A] with Box[A]
    }

    type Facade[+A] <: MonadErrorDecorator[A]

    trait MonadErrorDecorator[+A] extends Any with MonadError[A] with Box[A] {
      def handleError[B >: A](catcher: Error => Facade[B]): Facade[B] = Facade {
        // Assign underlyingFactory to local in case of this MonadErrorDecorator being captured by closures
        val underlyingFactory: MonadErrorFactoryDecorator.this.underlyingFactory.type =
          MonadErrorFactoryDecorator.this.underlyingFactory
        underlyingFactory
          .Facade(value)
          .flatMap {
            case Left(e) =>
              underlyingFactory.Facade(catcher(e).value)
            case right: Right[Error, B] =>
              underlyingFactory.pure(right)
          }
          .value
      }

      def flatMap[B](mapper: (A) => Facade[B]): Facade[B] = Facade {
        // Assign underlyingFactory to local in case of this MonadErrorDecorator being captured by closures
        val underlyingFactory: MonadErrorFactoryDecorator.this.underlyingFactory.type =
          MonadErrorFactoryDecorator.this.underlyingFactory
        underlyingFactory
          .Facade(value)
          .flatMap {
            case Right(a) =>
              underlyingFactory.Facade(mapper(a).value)
            case Left(e) =>
              underlyingFactory.pure(Left(e))
          }
          .value
      }
    }

    def raiseError[A](e: Error): Facade[A] = Facade(underlyingFactory.pure(Left(e)).value)

    def pure[A](a: A): Facade[A] = Facade(underlyingFactory.pure(Right(a)).value)

  }

  trait IOFactoryDecorator extends IOFactory with BoxFactoryDecorator {

    type UnderlyingFactory <: IOFactory with BoxFactory

    def liftIO[A](io: () => A): Facade[A] = Facade(underlyingFactory.liftIO(() => Right(io())).value)

  }

  object Task extends MonadErrorFactoryDecorator with IOFactoryDecorator with BoxCompanion {
    type Error = Throwable
    type UnderlyingFactory = continuation.UnitContinuation.type
    val underlyingFactory: UnderlyingFactory = UnitContinuation
    implicit final class Facade[+A](val value: Value[A]) extends AnyVal with MonadErrorDecorator[A]
  }

  /** @template */
  type Task[+A] = Task.Facade[A]
}
