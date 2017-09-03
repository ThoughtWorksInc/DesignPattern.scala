package com.thoughtworks.plainoldscalafactorydesignpattern
import language.higherKinds

/**
  * @author 杨博 (Yang Bo)
  */
object continuation {

  /**
    * @author 杨博 (Yang Bo)
    */
  trait ContinuationFactory extends MonadFactory with BoxFactory with LiftIOFactory {
    type Result
    type Unboxed[+A] = (A => Result) => Result

    type Facade[+A] <: Continuation[A]

    trait Continuation[+A] extends Any with Monad[A] with Box[A] {
      override def flatMap[B](mapper: (A) => Facade[B]): Facade[B] = Facade { (continue: B => Result) =>
        unbox { a: A =>
          mapper(a).unbox(continue)
        }
      }
    }

    def apply[A](a: A): Facade[A] = Facade(_(a))

    def liftIO[A](io: IO[A]): Facade[A] = Facade(_(io()))
  }

  object UnitContinuation extends ContinuationFactory {
    type Result = Unit
    implicit final class Facade[+A](val unbox: Unboxed[A]) extends AnyVal with Continuation[A]
  }
  type UnitContinuation[+A] = UnitContinuation.Facade[A]

}
