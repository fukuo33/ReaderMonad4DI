package readermonad8

import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

import scalaz._
import Scalaz._

/**
 * p56
 */
object OtherMonads {
  def main(args: Array[String]) {
    println("Hello OtherMonads!")

    val email = UserService.getEmail(100)
    val result = Await.result(email(productionEnv), Duration.Inf)
    println(result)
  }

}

case class User(id: Int, supervisorId: Int, email: String)

trait UserRepo {
  def get(userId: Int): Future[User]
  def find(email: String): Future[User]
  def update(user: User): Future[User]
}

trait AddressRepo {
  def get(id: Int): Future[String]
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
  def get(userId: Int): Future[User] = Future { User(userId, userId * 100, s"$userId@a.com") }
  def find(email: String): Future[User] = Future { User(2, 200, email) }
  def update(user: User): Future[User] = Future { User(3, 300, s"c@a.com") }
}

case class MyAdressRepo() extends AddressRepo {
  def get(id: Int): Future[String] = Future { s"$id@a.com" }
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

  def getEmail(userId: Int)(implicit ec: ExecutionContext) =
    for (userFuture <- UserRepo.getUser(userId))
    yield userFuture map (_.email)

  def findAddress(email: String)(implicit ec: ExecutionContext) =
    Env.env map { env =>
      for {
        user <- UserRepo.findUser(email).run(env)
        address <- AddressRepo.getAddress(user.id).run(env)
      } yield address
    }
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

object Env {
  val env = Reader[Env, Env](identity)
  val config = env map (_.config)
  val emailService = env map (_.emailService)
  val repositories = env map (_.repositories)
}

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

case class ProductionEmailService() extends EmailService {
}

case class ProductionConfiguration() extends Configuration {
}
