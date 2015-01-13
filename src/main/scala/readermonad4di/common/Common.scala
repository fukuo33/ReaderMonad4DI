package readermonad4di.common

case class User(id: Int, supervisorId: Int, email: String)

// p39
trait UserRepo {
  def get(userId: Int): User
  def find(email: String): User
  def update(user: User): User
}

trait AddressRepo {
  def get(id: Int): String
}

// p43
trait Repositories {
  def userRepo: UserRepo
  def addressRepo: AddressRepo
}

case class MyRepositories() extends Repositories {
  def userRepo: UserRepo = MyUserRepo()
  def addressRepo: AddressRepo = MyAddressRepo()
}

case class MyUserRepo() extends UserRepo {
  def get(userId: Int): User = User(userId, userId * 100, s"$userId@a.com")
  def find(email: String): User = User(2, 200, email)
  def update(user: User): User = User(3, 300, s"c@a.com")
}

case class MyAddressRepo() extends AddressRepo {
  def get(id: Int): String = s"$id@a.com"
}
