package com.thoughtworks.plainoldscalafactorydesignpattern

/**
  * @author 杨博 (Yang Bo)
  */
final class Continuations[R] extends Monads with Boxes with LiftIOs {
  type UnderlyingData[+A] = (A => R) => R

  implicit final class Facade[+A](val unbox: UnderlyingData[A]) extends Monad[A] with Box[A] {
    override def flatMap[B](mapper: (A) => Facade[B]): Facade[B] = Facade { (continue: B => R) =>
      unbox { a: A =>
        mapper(a).unbox(continue)
      }
    }
  }

  def box[A](unboxed: ((A) => R) => R): Facade[A] = Facade(unboxed)

  def apply[A](a: A): Facade[A] = Facade(_(a))

  def liftIO[A](io: () => A): Facade[A] = Facade(_(io()))
}

object Continuations {

  val UnitContinuation = new Continuations[Unit]
  type UnitContinuation[+A] = UnitContinuation.Facade[A]

}