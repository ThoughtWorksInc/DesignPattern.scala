package com.thoughtworks.plainoldscalafactorydesignpattern

import language.higherKinds

/**
  * @author 杨博 (Yang Bo)
  */
trait EitherTMonadErrors[Error] extends MonadErrors[Error] with Boxes with LiftIOs {
  type UnderlyingFactory <: Monads with Boxes with LiftIOs {
    type Facade[+A] <: Monad[A] with Box[A]
  }

  val underlying: UnderlyingFactory

  type UnderlyingData[+A] = underlying.UnderlyingData[Either[Error, A]]

  type Facade[+A] <: EitherTMonadError[A]

  trait EitherTMonadError[+A] extends MonadError[A] with Box[A] {
    def handleError[B >: A](catcher: Error => Facade[B]): Facade[B] = box {
      underlying
        .box(unbox)
        .flatMap {
          case Left(e) =>
            underlying.box(catcher(e).unbox)
          case right: Right[Error, B] =>
            underlying(right)
        }
        .unbox

    }

    def flatMap[B](mapper: (A) => Facade[B]): Facade[B] = box {
      underlying
        .box(unbox)
        .flatMap {
          case Right(a) =>
            underlying.box(mapper(a).unbox)
          case Left(e) =>
            underlying(Left(e))
        }
        .unbox
    }
  }

  def raiseError[A](e: Error) = box(underlying(Left(e)).unbox)

  def apply[A](a: A) = box(underlying(Right(a)).unbox)

  def liftIO[A](io: () => A): Facade[A] = box(underlying.liftIO(() => Right(io())).unbox)
}
