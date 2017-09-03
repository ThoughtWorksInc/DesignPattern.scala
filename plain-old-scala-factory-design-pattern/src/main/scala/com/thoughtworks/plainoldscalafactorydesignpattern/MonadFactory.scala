package com.thoughtworks.plainoldscalafactorydesignpattern
import language.higherKinds

/**
  * @author 杨博 (Yang Bo)
  */
trait MonadFactory extends FunctorFactory {

  type Facade[+A] <: Monad[A]

  trait Monad[+A] extends Any with Functor[A] {

    def map[B](mapper: (A) => B): Facade[B] = {
      flatMap { a =>
        pure(mapper(a))
      }
    }

    def flatMap[B](mapper: A => Facade[B]): Facade[B]

  }

  def pure[A](a: A): Facade[A]

}
