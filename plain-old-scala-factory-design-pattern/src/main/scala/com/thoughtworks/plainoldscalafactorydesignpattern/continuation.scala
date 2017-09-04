package com.thoughtworks.plainoldscalafactorydesignpattern
import com.thoughtworks.plainoldscalafactorydesignpattern.covariant._

import language.higherKinds
import language.implicitConversions
import scala.concurrent.SyncVar

/**
  * @author 杨博 (Yang Bo)
  */
object continuation {

  trait ContinuationFactory extends MonadFactory with BoxFactory with IOFactory {
    type Result
    type Value[+A] = (A => Result) => Result

    type Facade[+A] <: Continuation[A]

    trait Continuation[+A] extends Any with Monad[A] with Box[A] {
      override def flatMap[B](mapper: (A) => Facade[B]): Facade[B] = {
        // Assign value to local in case of this Continuation being captured by closures
        val value = unbox
        Facade { (continue: B => Result) =>
          value { a: A =>
            mapper(a).unbox(continue)
          }
        }
      }

      override def map[B](mapper: (A) => B): Facade[B] = {
        // Assign value to local in case of this Continuation being captured by closures
        val value = unbox
        Facade { (continue: B => Result) =>
          value(mapper.andThen(continue))
        }
      }
    }

    def pure[A](a: A): Facade[A] = Facade(_(a))

    def liftIO[A](io: () => A): Facade[A] = Facade(_(io()))
  }

  object UnitContinuation extends ContinuationFactory with BoxCompanion {
    type Result = Unit
    implicit final class Facade[+A](val unbox: Value[A]) extends AnyVal with Continuation[A] {
      def blockingAwait: A = {
        val syncVar: SyncVar[A] = new SyncVar
        unbox(syncVar.put)
        syncVar.take
      }
    }
  }

  /** @template */
  type UnitContinuation[+A] = UnitContinuation.Facade[A]

}
