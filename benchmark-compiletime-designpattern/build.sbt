sourceGenerators in Compile += Def.task {
  val maxDepth = 4
  val operatorsPerDepth = 5
  val numberOfFiles = 40

  def comprehensionCode(depth: Int): String = {
    if (depth < maxDepth) {
      val line = raw"""_ <- ${comprehensionCode(depth + 1)}"""
      raw"""for {
${(for (i <- 0 until operatorsPerDepth) yield line).mkString("\n")}
} yield ()"""
    } else {
      "AsyncIO.liftIO { () => println() }"
    }
  }
  for (i <- 0 until numberOfFiles) yield {
    val outputFile = (sourceManaged in configuration).value / raw"""CompileTimeBenchmark$i.scala"""
    IO.write(
      outputFile,
      raw"""package com.thoughtworks.designpattern.benchmark.compiletime
import com.thoughtworks.designpattern.benchmark.asyncio.designpattern.AsyncIO
object designpattern$i extends App {
${comprehensionCode(0)}
}"""
    )
    outputFile
  }

}.taskValue
