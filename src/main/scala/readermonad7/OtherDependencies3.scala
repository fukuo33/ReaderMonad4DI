package readermonad7

import scalaz._
import Scalaz._

/**
 * p52
 */
object OtherDependencies3 {

  def main(args: Array[String]) {
    println(UserService.getEmail(100)(productionEnv))
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

trait ConfigurationComponent {
  def config: Configuration
}

trait EmailServiceComponent {
  def emailService: EmailService
}

trait RepositoriesComponent {
  def repositories: Repositories
}

trait UserRepoComponent {
  def userRepo: UserRepo
}

trait AddressRepoComponent {
  def addressRepo: AddressRepo
}

trait Env extends
ConfigurationComponent with
EmailServiceComponent with
RepositoriesComponent

//trait RepositoriesComponent extends
//UserRepoComponent with
//AddressRepoComponent

object productionEnv extends Env
with PlayConfigComponent
with PlayEmailServiceComponent
with MongoRepositoriesComponent


trait PlayConfigComponent extends Configuration {
  lazy val _config = ProductionConfiguration()
  def config: Configuration = _config
}

trait PlayEmailServiceComponent extends EmailServiceComponent {
  lazy val _emailService = ProductionEmailService()
  def emailService: EmailService = _emailService
}

trait MongoRepositoriesComponent extends RepositoriesComponent {
  lazy val _repositories = MyRepositories()
  def repositories: Repositories = _repositories

}

object Env {
  val env = Reader[Env, Env](identity)
  val config = env map (_.config)
  val emailService = env map (_.emailService)
  val repositories = env map (_.repositories)
}

//object testEnv extends Env
//with MockConfigComponent
//with MockEmailServiceComponent
//with MockRepositoriesComponent


case class ProductionEmailService() extends EmailService {
}

case class ProductionConfiguration() extends Configuration {
}
