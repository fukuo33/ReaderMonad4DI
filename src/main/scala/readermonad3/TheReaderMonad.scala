package readermonad3

import scalaz._
import Scalaz._

/**
 * Created by fukuo33 on 2014/07/06.
 */
object TheReaderMonad {

  def main(args: Array[String]) {
    println("Hello Reader Monad!")

    def p30 = {
      val f = Reader[Int, Int](_ + 2)
      val g = f map (_ * 3)
      g(3)
    }
    println(p30)
  }

}
