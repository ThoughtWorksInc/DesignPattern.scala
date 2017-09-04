package com.thoughtworks.designpattern

import language.implicitConversions
import language.higherKinds
import com.thoughtworks.designpattern.continuation.UnitContinuation
import com.thoughtworks.designpattern.covariant.BoxCompanion
import com.thoughtworks.designpattern.either.{IOFactoryDecorator, MonadErrorFactoryDecorator}
import org.openjdk.jmh.annotations._

import scala.collection.mutable.ListBuffer
import scala.concurrent.SyncVar
import scalaz.{Cont, EitherT, IndexedContsT, \/-}
import scalaz.effect.{IO, LiftIO}

/**
  * @author 杨博 (Yang Bo)
  */
@State(Scope.Benchmark)
class Main {

  @Benchmark
  def sequenceScalazAsyncIOS(): Long = {
    import scalaz.syntax.all._
    import com.thoughtworks.designpattern.benchmark.scalazasyncio.AsyncIO

    val tasks = (0 until 100).map(_ => IO(1).liftIO[AsyncIO]).toList
    val init = IO(ListBuffer.empty[Int]).liftIO[AsyncIO]
    tasks
      .foldLeft(init)((acc, elem: AsyncIO[Int]) => acc.flatMap(lb => elem.map(e => lb += e)))
      .map(_.toList.sum.toLong)
      .blockingAwait()
  }

  @Benchmark
  def sequenceDesignPatternAsyncIOS(): Long = {
    import com.thoughtworks.designpattern.benchmark.designpatternasyncio.AsyncIO

    val tasks = (0 until 100).map(_ => AsyncIO.liftIO(() => 1)).toList
    val init = AsyncIO.liftIO(() => ListBuffer.empty[Int])
    tasks
      .foldLeft(init)((acc, elem: AsyncIO[Int]) => acc.flatMap(lb => elem.map(e => lb += e)))
      .map(_.toList.sum.toLong)
      .blockingAwait()
  }

}
