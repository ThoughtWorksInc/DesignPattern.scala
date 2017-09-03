package com.thoughtworks.plainoldscalafactorydesignpattern
import language.higherKinds
import language.implicitConversions

/**
  * @author 杨博 (Yang Bo)
  */
trait Functors {

  type Facade[+A]  <: Functor[A]

  trait Functor[+A] {

    def map[B](mapper: A => B): Facade[B]

  }

}
