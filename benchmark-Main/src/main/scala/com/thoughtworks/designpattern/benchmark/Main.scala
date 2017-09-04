package com.thoughtworks.designpattern.benchmark

import org.openjdk.jmh.annotations._

import scala.collection.mutable.ListBuffer
import scala.language.{higherKinds, implicitConversions}
import scalaz.effect.IO

/**
  * @author 杨博 (Yang Bo)
  */
@State(Scope.Benchmark)
class Main {

  @Benchmark
  def sequenceScalazAsyncIOS(): Long = {
    import com.thoughtworks.designpattern.benchmark.scalazasyncio.AsyncIO

    import scalaz.syntax.all._

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

  @Benchmark
  def sequenceScalazAsyncIOA(): Long = {
    import com.thoughtworks.designpattern.benchmark.scalazasyncio.AsyncIO

    import scala.concurrent.ExecutionContext.Implicits._
    import scalaz.syntax.all._

    val tasks = (0 until 100).map(_ => AsyncIO.execute(IO(1))).toList
    val init = AsyncIO.execute(IO(ListBuffer.empty[Int]))
    tasks
      .foldLeft(init)((acc, elem: AsyncIO[Int]) => acc.flatMap(lb => elem.map(e => lb += e)))
      .map(_.toList.sum.toLong)
      .blockingAwait()
  }

  @Benchmark
  def sequenceDesignPatternAsyncIOA(): Long = {
    import com.thoughtworks.designpattern.benchmark.designpatternasyncio.AsyncIO

    import scala.concurrent.ExecutionContext.Implicits._

    val tasks = (0 until 100).map(_ => AsyncIO.execute(() => 1)).toList
    val init = AsyncIO.execute(() => ListBuffer.empty[Int])
    tasks
      .foldLeft(init)((acc, elem: AsyncIO[Int]) => acc.flatMap(lb => elem.map(e => lb += e)))
      .map(_.toList.sum.toLong)
      .blockingAwait()
  }

}
