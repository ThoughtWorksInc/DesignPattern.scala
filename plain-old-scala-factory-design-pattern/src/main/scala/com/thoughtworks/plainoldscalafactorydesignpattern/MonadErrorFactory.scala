package com.thoughtworks.plainoldscalafactorydesignpattern
import language.higherKinds

/**
  * @author 杨博 (Yang Bo)
  */
trait MonadErrorFactory extends MonadFactory {

  type Error

  override type Facade[+A] <: MonadError[A]

  trait MonadError[+A] extends Any with Monad[A] {
    def handleError[B >: A](catcher: Error => Facade[B]): Facade[B]
  }

  def raiseError[A](e: Error): Facade[A]

}
