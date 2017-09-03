package com.thoughtworks.plainoldscalafactorydesignpattern
import language.higherKinds

/**
  * @author 杨博 (Yang Bo)
  */
trait MonadErrors[Error] extends Monads {

  override type Facade[+A]   <: MonadError[A]

  trait MonadError[+A] extends Monad[A] {
    def handleError[B >: A](catcher: Error => Facade[B]): Facade[B]
  }

  def raiseError[A](e: Error): Facade[A]

}
