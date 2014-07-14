package readermonad2

import org.specs2.mutable.SpecificationWithJUnit

/**
 * Created by fukuo33 on 2014/07/07.
 */
class FunctionSpec extends SpecificationWithJUnit {

  "p27" should {
    val f = (i: Int) => i * 5
    val g = (i: Int) => i + 3
    val h = (i: Int) => i - 10

    "f.andThen(identity) == f" in {
      f.andThen(identity)(1) must_== f(1)
    }

    "f.andThen(g).andThen(h) == f.andThen(x => h(g(x)))" in {
      f.andThen(g).andThen(h)(1) == f.andThen(x => h(g(x)))(1)
    }
  }

}
