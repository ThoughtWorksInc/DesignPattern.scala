package com.thoughtworks.plainoldscalafactorydesignpattern
import language.higherKinds

/**
  * @author 杨博 (Yang Bo)
  */
trait LiftIOs {

  type Facade[A]

  def liftIO[A](io: () => A): Facade[A]

}
