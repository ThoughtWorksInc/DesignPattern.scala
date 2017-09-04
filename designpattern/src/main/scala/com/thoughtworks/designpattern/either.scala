package com.thoughtworks.designpattern
import com.thoughtworks.designpattern.covariant._

import language.higherKinds
import scala.util.Try

/**
  * @author 杨博 (Yang Bo)
  */
object either {

  trait EitherBoxFactoryDecorator extends BoxFactory {
    type UnderlyingFactory <: BoxFactory
    val underlyingFactory: UnderlyingFactory

    type LeftHandSide
    type Value[+A] = underlyingFactory.Value[Either[LeftHandSide, A]]
  }

  trait EitherMonadErrorFactoryDecorator extends MonadErrorFactory with EitherBoxFactoryDecorator {
    type UnderlyingFactory <: MonadFactory with BoxFactory {
      type Facade[+A] <: Monad[A] with Box[A]
    }

    type Error = LeftHandSide

    type Facade[+A] <: MonadErrorDecorator[A]

    trait MonadErrorDecorator[+A] extends Any with MonadError[A] with Box[A] with FlattenIsDerived[A] {
      this: Facade[A] =>

      def map[B](mapper: A => B): Facade[B] = {
        underlyingFactory.Facade(value).map(_.map(mapper)).value
      }

      def handleError[B >: A](catcher: Error => Facade[B]): Facade[B] = Facade {
        // Assign underlyingFactory to local in case of this MonadErrorDecorator being captured by closures
        val underlyingFactory: EitherMonadErrorFactoryDecorator.this.underlyingFactory.type =
          EitherMonadErrorFactoryDecorator.this.underlyingFactory
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

      def flatMap[B](mapper: A => Facade[B]): Facade[B] = Facade {
        // Assign underlyingFactory to local in case of this MonadErrorDecorator being captured by closures
        val underlyingFactory: EitherMonadErrorFactoryDecorator.this.underlyingFactory.type =
          EitherMonadErrorFactoryDecorator.this.underlyingFactory
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

  trait EitherIOFactoryDecorator extends IOFactory with EitherBoxFactoryDecorator {

    type UnderlyingFactory <: IOFactory with BoxFactory

    type LeftHandSide = Throwable

    def liftIO[A](io: () => A): Facade[A] = Facade(underlyingFactory.liftIO(() => Try(io()).toEither).value)

  }

}
