package com.thoughtworks.plainoldscalafactorydesignpattern

/**
  * @author 杨博 (Yang Bo)
  */
trait BoxCompanion extends BoxFactory {
  def apply[A](unboxed: Unboxed[A]): Facade[A] = Facade(unboxed)
  def unapply[A](facade: Facade[A]): Some[Unboxed[A]] = Some(facade.unbox)
}
