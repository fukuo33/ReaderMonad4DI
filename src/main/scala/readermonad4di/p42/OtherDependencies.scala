package readermonad4di.p42

import scalaz._
import Scalaz._
import readermonad4di.common._


object OtherDependencies {

  def main(args: Array[String]) {
    println(UserRepo.getUser(4)(MyRepositories()))
  }

}

// p44
object Repositories {
  val repositories = Reader[Repositories, Repositories](identity)
  val userRepo = repositories map (_.userRepo)
  val addressRepo = repositories map (_.addressRepo)
}

// p45
object UserRepo {
  import Repositories.userRepo

  // 戻り値を明記しないのがイマイチ
  // メソッド名かぶりを回避するために ~~User() にしてる
  def getUser(userId: Int) = userRepo map (_.get(userId))
  def findUser(email: String) = userRepo map (_.find(email))
  def updateUser(user: User) = userRepo map (_.update(user))
}

object AddressRepo {
  import Repositories.addressRepo

  def getAddress(id: Int) = addressRepo map (_.get(id))
}

