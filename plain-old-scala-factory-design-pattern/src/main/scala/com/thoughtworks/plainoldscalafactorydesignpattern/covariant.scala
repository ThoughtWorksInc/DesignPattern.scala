package com.thoughtworks.plainoldscalafactorydesignpattern
import language.higherKinds

/**
  * @author 杨博 (Yang Bo)
  */
object covariant {
  trait BoxFactory {

    type Unboxed[+A]

    trait Box[+A] extends Any {
      def unbox: Unboxed[A]
    }

    type Facade[+A] <: Box[A]
    implicit def Facade[A](unboxed: Unboxed[A]): Facade[A]

  }

  trait BoxCompanion extends BoxFactory {
    def apply[A](unboxed: Unboxed[A]): Facade[A] = Facade(unboxed)
    def unapply[A](facade: Facade[A]): Some[Unboxed[A]] = Some(facade.unbox)
  }

  trait IOFactory {

    type Facade[+A]

    def liftIO[A](io: () => A): Facade[A]

  }

  trait FunctorFactory {

    type Facade[+A] <: Functor[A]

    trait Functor[+A] extends Any {

      def map[B](mapper: A => B): Facade[B]

    }

  }

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

  trait MonadErrorFactory extends MonadFactory {

    type Error

    override type Facade[+A] <: MonadError[A]

    trait MonadError[+A] extends Any with Monad[A] {
      def handleError[B >: A](catcher: Error => Facade[B]): Facade[B]
    }

    def raiseError[A](e: Error): Facade[A]

  }

}
