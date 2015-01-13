package readermonad4di.example

import scalaz.Scalaz._
import scalaz._

/**
 * Created by fukuo33 on 2014/07/06.
 */
object ReaderMonadFromLearningScalaz {

  def main(args: Array[String]) {
    println("Hello Reader Transformer Monad!")

    // 関数合成
    val f = (_: Int) * 5
    val g = (_: Int) + 3
    println((g map f)(8))

    // アプリカティブスタイル
    val f2 = ({(_: Int) * 2} |@| {(_: Int) + 10}) {_ + _}
    println(f2(3))

    // forスタイル
    val addStuff: Int => Int = for {
      a <- (_: Int) * 2
      b <- (_: Int) + 10
    } yield a + b

    println(addStuff(3))

    println(myName("hoge")("foo"))

    println(localExample("Fred"))

    val goodConfig = Map(
      "host" -> "eed3si9n.com",
      "user" -> "sa",
      "password" -> "****"
    )
    println(setupConnection(goodConfig))

    val badConfig = Map(
      "host" -> "example.com",
      "user" -> "sa"
    )
    println(setupConnection(badConfig))

    def aaa =
    for {
      a <- (x => Some(x + 2)) : Int => Option[Int]
      b <- (x => Some(x * 3)) : Int => Option[Int]
    } yield {
      for {
        aa <- a
        bb <- b
      } yield aa + bb
    }
    println(aaa(10))
    
    
    def aaaa = 
    for {
      a <- ReaderTOption[Int, Int] {x => Some(x + 2)}
      b <- ReaderTOption[Int, Int] {x => Some(x * 3)}
    } yield a + b
    
    println(aaaa(10))

    val loi: List[Option[Int]] = List(Some(1), None, Some(3))

    val loResult =
    for {
      oi <- loi
    } yield {
      for {
        i <- oi
      } yield i + 100
    }
    
    println(s"loResult: $loResult")

    val otli: OptionTList[Int] = OptionT(List(Some(1), None, Some(3)))
    val otliResult =
    for {
      i <- otli
    } yield i + 100
    println(s"otliResult: ${otliResult.run}")

    
    val lto: ListTOption[Int] = ListT(Option(List(1,2,3)))
    val otl: OptionTList[Int] = OptionT(List(Some(1), Some(2), Some(3)))

    def bbb() =
    for {
      o1 <- otl
      o2 <- otl
    } yield o1 + o2
    
    println(bbb().run)


  }

  def myName(step: String): Reader[String, String] = Reader {step + ", I am " + _}

  def localExample: Reader[String, (String, String, String)] = for {
    a <- myName("First")
    b <- myName("Second") >=> Reader { _ + "dy"}
    c <- myName("Third")
  } yield (a, b, c)

  type ReaderTOption[A, B] = ReaderT[Option, A, B]
  object ReaderTOption extends KleisliFunctions with KleisliInstances {
    def apply[A, B](f: A => Option[B]): ReaderTOption[A, B] = kleisli(f)
  }

  def configure(key: String) = ReaderTOption[Map[String, String], String] {_.get(key)}

  def setupConnection: ReaderTOption[Map[String, String], (String, String, String)] = for {
    host <- configure("host")
    user <- configure("user")
    password <- configure("password")
  } yield (host, user, password)

  type ListTOption[A] = ListT[Option, A]
  type OptionTList[A] = OptionT[List, A]


}
