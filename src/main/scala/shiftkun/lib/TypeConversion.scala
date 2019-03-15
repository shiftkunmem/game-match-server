package shiftkun.lib

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}
import cats.instances.future._
import cats.instances.list._
import cats.{Eval, Foldable}
import shiftkun.lib.{ErrorType, ProcessResult}
import shiftkun.lib.TypeAlias.ProcessResult

import scala.concurrent.ExecutionContext

object TypeConversions {

  def err[T](f: => ErrorType): NonEmptyList[ErrorType] =
    NonEmptyList.of(f)

  def flatten[E, T](v: Validated[E, Validated[E, T]]): Validated[E, T] =
    v match {
      case Invalid(e) => Invalid(e)
      case Valid(Valid(s)) => Valid(s)
      case Valid(Invalid(e)) => Invalid(e)
    }

  def sequence[A](evals: Seq[Eval[A]]): Eval[Seq[A]] = {
    Foldable[List].foldRight(evals.toList, Eval.now(List.empty[A])) { (aEval: Eval[A], accEval: Eval[List[A]]) =>
      for {
        acc <- accEval
        a <- aEval
      } yield a :: acc
    }
  }

  def foldPR[A, B](xs: Seq[A])(f: A => ProcessResult[B])(implicit ec: ExecutionContext): ProcessResult[Seq[B]] =
    xs.foldLeft(ProcessResult.wrapSuccess(Seq.empty[B])) { (acc, x) =>
      for {
        ys <- acc
        y <- f(x)
      } yield ys :+ y
    }

  def foldPR2[ID, A](ids: Seq[ID])(f: ID => ProcessResult[A])(implicit ec: ExecutionContext): ProcessResult[(Seq[A], Seq[ErrorType])] = {
    for {
      result <- ids.toStream.foldLeft(ProcessResult.wrapSuccess((Seq.empty[A], Seq.empty[ErrorType]))) { (acc, id) =>
        (for {
          x <- acc
          result <- f(id)
        } yield {
          (x._1 :+ result, x._2)
        }).recoverWith { case errors =>
          acc.map { case (as, es) => (as, es ++ errors.toList) }
        }
      }
    } yield result
  }


  /*
   * 処理の結果Aとエラーを分離します
   */
  def splitErrors[A](ps: Seq[ProcessResult[A]])(implicit ec: ExecutionContext): ProcessResult[(Seq[A],Seq[ErrorType])] =
    ps.foldLeft(ProcessResult.wrapSuccess((Seq.empty[A],Seq.empty[ErrorType]))) { (acc, p) =>
      p.flatMap( pp =>
        for {
          x <- acc
        } yield (x._1 :+ pp, x._2)
      ).recoverWith { case errors =>
        for {
          x <- acc
        } yield (x._1, x._2 ++ errors.toList)
      }
    }

  def shiftKey[A, B, C](m: Map[A, Map[B, C]]): Map[B, Map[A, C]] =
    m.flatMap { case (a, inner) =>
      inner.map { case (b, c) => (a, b, c) }
    }.groupBy(_._2)
      .mapValues { _.map { case (a, _, c) => (a, c) }.toMap }


  def mergeWithMaxValue[K, V : Ordering](src: Map[K, V], keys: Seq[K], value: V): Map[K, V] =
    src ++ keys.distinct.map { key =>
      key -> {
        src.get(key).map { v => if (Ordering[V].gt(v, value)) v else value }
          .getOrElse(value)
      }
    }


}
