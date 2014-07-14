package readermonad5

import scalaz._
import Scalaz._

/**
 * p42
 */
object OtherDependencies {

  def main(args: Array[String]) {
    println("Hello Reader Monad!")

    println(UserRepo.getUser(4)(MyRepositories()))
  }

}


case class User(id: Int, supervisorId: Int, email: String)

trait UserRepo {
  def get(userId: Int): User
  def find(email: String): User
  def update(user: User): User
}

trait AddressRepo {
  def get(id: Int): String
}

trait Repositories {
  def userRepo: UserRepo
  def addressRepo: AddressRepo
}

object Repositories {
  val repositories = Reader[Repositories, Repositories](identity)
  val userRepo = repositories map (_.userRepo)
  val addressRepo = repositories map (_.addressRepo)
}

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

case class MyRepositories() extends Repositories {
  def userRepo: UserRepo = MyUserRepo()
  def addressRepo: AddressRepo = MyAdressRepo()
}

case class MyUserRepo() extends UserRepo {
  def get(userId: Int): User = User(userId, userId * 100, s"$userId@a.com")
  def find(email: String): User = User(2, 200, email)
  def update(user: User): User = User(3, 300, s"c@a.com")
}

case class MyAdressRepo() extends AddressRepo {
  def get(id: Int): String = s"$id@a.com"
}
