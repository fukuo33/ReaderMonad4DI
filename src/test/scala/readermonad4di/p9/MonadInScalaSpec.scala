package readermonad4di.example

import org.specs2.mutable.SpecificationWithJUnit

import scalaz._
import Scalaz._

/**
 * Created by fukuo33 on 2014/07/06.
 */
class MonadInScalaSpec extends SpecificationWithJUnit {

  "p11" should {
    "return" in {
      val xs = List(1, 2, 5)
      xs map (x => x * x) must_== List(1, 4, 25)
    }
  }

  "p14" should {
    val xs = List(1, 2, 5)
    val f = (i: Int) => i * 5
    val g = (i: Int) => i + 3

    "xs.map(identity) == xs" in {
      xs.map(identity) must_== xs
    }

    "xs.map(f).map(g) == xs.map(x => g(f(x)))" in {
      xs.map(f).map(g) must_== xs.map(x => g(f(x)))
    }
  }

  "p19" should {
    val xs = List(1, 2, 5)
    val f = (i: Int) => i * 5
    val g = (i: Int) => i + 3

    "xs.flatMap(x => List(f(x))) == xs.map(f)" in {
      xs.flatMap(x => List(f(x))) must_== xs.map(f)
    }
  }

  "p20" should {
    val xs = List(1, 2, 3)
    val f = (i: Int) => List(i * 5)
    val g = (i: Int) => List(i + 3)

    "List(x).flatMap(f) == f(x)" in {
      List(10).flatMap(f) must_== f(10)
    }
    "xs.flatMap(x => List(x)) == xs" in {
      xs.flatMap(x => List(x)) must_== xs
    }
    "xs.flatMap(f).flatMap(g) == xs.flatMap(f(_).flatMap(g))" in {
      println(xs.flatMap(f).flatMap(g))
      xs.flatMap(f).flatMap(g) must_== xs.flatMap(f(_).flatMap(g))
    }
  }

  "p33" should {
    "return" in {
      val f = Reader[Int, Int](_ + 2)
      val g = f map (_ * 3)

      g(3) must_== 15
    }
  }
}
