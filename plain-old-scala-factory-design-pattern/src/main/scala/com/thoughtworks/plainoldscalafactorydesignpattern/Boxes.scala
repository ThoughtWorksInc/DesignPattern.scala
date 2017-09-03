package com.thoughtworks.plainoldscalafactorydesignpattern

import language.higherKinds

/**
  * @author 杨博 (Yang Bo)
  */
trait Boxes {

  type Facade[+A] <: Box[A]

  type UnderlyingData[+A]

  def box[A](unboxed: UnderlyingData[A]): Facade[A]

  trait Box[+A] {

    def unbox: UnderlyingData[A]

  }

}
