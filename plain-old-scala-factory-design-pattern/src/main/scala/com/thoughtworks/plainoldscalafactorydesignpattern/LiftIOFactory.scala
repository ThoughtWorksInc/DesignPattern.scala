package com.thoughtworks.plainoldscalafactorydesignpattern
import language.higherKinds

/**
  * @author 杨博 (Yang Bo)
  */
trait LiftIOFactory {

  type Facade[A]

  type IO[+A] = () => A

  def liftIO[A](io: IO[A]): Facade[A]

}
