package com.thoughtworks.plainoldscalafactorydesignpattern

import language.higherKinds
import language.implicitConversions

/**
  * @author 杨博 (Yang Bo)
  */
trait BoxFactory {

  type Unboxed[+A]

  trait Box[+A] extends Any {
    def unbox: Unboxed[A]
  }

  type Facade[+A] <: Box[A]
  implicit def Facade[A](unboxed: Unboxed[A]): Facade[A]

}
