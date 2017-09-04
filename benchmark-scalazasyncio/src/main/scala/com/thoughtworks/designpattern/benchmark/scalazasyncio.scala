package com.thoughtworks.designpattern.benchmark

import scala.concurrent.{ExecutionContext, SyncVar}
import scalaz._
import scalaz.effect._

object scalazasyncio {

  object AsyncIO {
    implicit object asyncIOInstances
        extends IsomorphismMonadError[AsyncIO, EitherT[Cont[Unit, ?], Throwable, ?], Throwable]
        with MonadIO[AsyncIO] {
      val G = EitherT.eitherTMonadError[Cont[Unit, ?], Throwable](Cont.ContsTMonad[scalaz.Id.Id, scalaz.Id.Id, Unit])
      object iso extends Isomorphism.IsoFunctorTemplate[AsyncIO, EitherT[Cont[Unit, ?], Throwable, ?]] {
        def to[A](fa: AsyncIO[A]) = fa.eitherTUnitCont
        def from[A](ga: EitherT[Cont[Unit, ?], Throwable, A]): AsyncIO[A] = new AsyncIO(ga)
      }

      def liftIO[A](ioa: IO[A]): AsyncIO[A] = {
        AsyncIO(EitherT[Cont[Unit, ?], Throwable, A](Cont[Unit, Throwable \/ A](_(ioa.catchLeft.unsafePerformIO()))))
      }
    }

    def execute[A](io: IO[A])(implicit executionContext: ExecutionContext): AsyncIO[A] = {
      val cont = Cont { (continue: Throwable \/ A => Unit) =>
        executionContext.execute { () =>
          continue(io.catchLeft.unsafePerformIO())
        }
      }
      AsyncIO(EitherT[Cont[Unit, ?], Throwable, A](cont))
    }
  }

  final case class AsyncIO[A](eitherTUnitCont: EitherT[Cont[Unit, ?], Throwable, A]) extends AnyVal {
    def blockingAwait(): A = {
      val syncVar: SyncVar[Throwable \/ A] = new SyncVar
      eitherTUnitCont.run(syncVar.put)
      syncVar.take.valueOr(throw _)
    }
  }

}
