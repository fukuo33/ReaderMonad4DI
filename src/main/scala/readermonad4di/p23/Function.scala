package readermonad4di.p23

/**
 * Created by fukuo33 on 2014/07/06.
 */
object Function {

  def main(args: Array[String]) {
    def p24 = {
      val add2: Int => Int = _ + 2
      add2(3)
    }
    println(p24)

    def p25 = {
      def add(x: Int): Int => Int = _ + x
      val add2 = add(2)
      add2(3)
    }
    println(p25)

    def p26 = {
      def add(x: Int): Int => Int = _ + x
      def multiplyBy(x: Int): Int => Int = _ * x
      val f = add(2) andThen multiplyBy(3)
      f(3) // multiplyBy(3)(add(2)(3))
    }
    println(p26)

    def p28 = {
      def add(x: Int): Int => Int = _ + x
      val f = add(2) andThen (_ * 3)
      f(3)
    }
    println(p28)

//    def p29 = {
//      def add(x: Int): Int => Int = _ + x
//      val f = add(2) map (_ * 3) //error: value map is not a member of Int => Int
//      f(3)
//    }

  }

}
