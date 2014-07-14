package readermonad6

import scalaz._
import Scalaz._

/**
 * p46
 */
object OtherDependencies2 {

  def main(args: Array[String]) {
    println(UserService.getEmail(100)(ProductionEnv()))
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

trait EmailService {
}

trait Configuration {
}

trait Env {
  def config: Configuration
  def emailService: EmailService
  def repositories: Repositories
}

object Env {
  val env = Reader[Env, Env](identity)
  val config = env map (_.config)
  val emailService = env map (_.emailService)
  val repositories = env map (_.repositories)
}

trait Repositories {
  def userRepo: UserRepo
  def addressRepo: AddressRepo
}

object Repositories {
  import Env.repositories

  val userRepo = repositories map (_.userRepo)
  val addressRepo = repositories map (_.addressRepo)
}

object UserRepo {
  import Repositories.userRepo

  def getUser(userId: Int) = userRepo map (_.get(userId))
  def findUser(email: String) = userRepo map (_.find(email))
  def updateUser(user: User) = userRepo map (_.update(user))
}

object UserService {

  def getEmail(userId: Int) =
    for {
      user <- UserRepo.getUser(userId)
    } yield user.email

  def findAddress(email: String) =
    for {
      user <- UserRepo.findUser(email)
      address <- AddressRepo.getAddress(user.id)
    } yield address
}


case class ProductionEnv() extends Env {
  lazy val _config = ProductionConfiguration()
  lazy val _emailService = ProductionEmailService()
  lazy val _repositories = MyRepositories()

  def config: Configuration = _config
  def emailService: EmailService = _emailService
  def repositories: Repositories = _repositories
}

case class ProductionEmailService() extends EmailService {
}

case class ProductionConfiguration() extends Configuration {
}
