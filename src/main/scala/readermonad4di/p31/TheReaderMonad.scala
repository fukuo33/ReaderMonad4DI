package readermonad4di.p31

import scalaz.Reader

/**
 * Created by fukuo33 on 2014/07/06.
 */
object TheReaderMonad {

  def main(args: Array[String]) {
    def p30 = {
      val f = Reader[Int, Int](_ + 2)
      val g = f map (_ * 3)
      g(3)
    }
    println(p30)
  }

}
