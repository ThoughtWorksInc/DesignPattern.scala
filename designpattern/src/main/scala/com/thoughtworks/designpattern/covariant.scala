package com.thoughtworks.designpattern
import language.higherKinds
import scala.language.implicitConversions

/**
  * @author 杨博 (Yang Bo)
  */
object covariant {
  trait BoxFactory {

    type Value[+A]

    trait Box[+A] extends Any {
      def value: Value[A]
    }

    type Facade[+A] <: Box[A]
    implicit def Facade[A](value: Value[A]): Facade[A]

  }

  trait BoxCompanion extends BoxFactory {
    def apply[A](value: Value[A]): Facade[A] = Facade(value)
    def unapply[A](facade: Facade[A]): Some[Value[A]] = Some(facade.value)
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

  trait FlatMapFactory extends FunctorFactory {

    type Facade[+A] <: FlatMap[A]

    trait FlatMap[+A] extends Any with Functor[A] {

      def flatMap[B](mapper: A => Facade[B]): Facade[B]

      def flatten[B](implicit asNested: Facade[A] <:< Facade[Facade[B]]): Facade[B]

    }

    trait FlatMapIsPrimary[+A] extends Any with FlatMap[A] {
      protected def isFlatMapDerived = false
    }

    trait FlatMapIsDerived[+A] extends Any with FlatMap[A] {

      def flatMap[B](mapper: (A) => Facade[B]): Facade[B] = {
        map(mapper).flatten
      }

      protected def isFlatMapDerived = true
    }

    trait FlattenIsDerived[+A] extends Any with FlatMapIsPrimary[A] { this: Facade[A] =>

      def flatten[B](implicit asNested: Facade[A] <:< Facade[Facade[B]]): Facade[B] = {
        asNested(this).flatMap(identity)
      }

    }

  }

  trait ApplicativeFactory extends FunctorFactory {
    def pure[A](a: A): Facade[A]
  }

  trait MonadFactory extends FlatMapFactory with ApplicativeFactory {

    type Monad[+A] = FlatMap[A]

    trait MapIsDerived[+A] extends Any with FlatMapIsPrimary[A] {

      def map[B](mapper: (A) => B): Facade[B] = {
        // Assign MonadFactory to local in case of this Monad being captured by closures
        val monadFactory: MonadFactory.this.type = MonadFactory.this
        flatMap { a: A =>
          monadFactory.pure(mapper(a))
        }
      }

    }
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
