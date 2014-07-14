package example

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

  def setupConnection = for {
    host <- configure("host")
    user <- configure("user")
    password <- configure("password")
  } yield (host, user, password)

}
