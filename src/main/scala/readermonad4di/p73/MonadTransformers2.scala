package readermonad4di.p73

import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

import scalaz._
import Scalaz._
import scalaz.concurrent.{Future => ZFuture}

object MonadTransformers2 {
  def main(args: Array[String]) {

    def idToAddress(id: Int) =
      for {
        email <- UserService.getEmail(id)
        address <- UserService.findAddress(email)
      } yield address
    
    println(idToAddress(100)(productionEnv).run)
    
  }

  type Query[A] = ReaderT[ZFuture, Env, A]
  object Query {
    def apply[A](run: Env => ZFuture[A]): Query[A] = Kleisli[ZFuture, Env, A](run)
    def lift[A](reader: Reader[Env, ZFuture[A]]) = Query(reader.run)
  }

  case class User(id: Int, supervisorId: Int, email: String)

  trait UserRepo {
    def get(userId: Int): ZFuture[User]
    def find(email: String): ZFuture[User]
    def update(user: User): ZFuture[User]
  }

  trait AddressRepo {
    def get(id: Int): ZFuture[String]
  }

  object AddressRepo {
    import Repositories.addressRepo
    def getAddress(id: Int)(implicit ec: ExecutionContext) = Query.lift(addressRepo map (_.get(id)))
  }

  case class MyRepositories() extends Repositories {
    def userRepo: UserRepo = MyUserRepo()
    def addressRepo: AddressRepo = MyAdressRepo()
  }

  case class MyUserRepo() extends UserRepo {
    def get(userId: Int) = ZFuture { User(userId, userId * 100, s"$userId@a.com") }
    def find(email: String) = ZFuture { User(2, 200, email) }
    def update(user: User) = ZFuture { User(3, 300, s"c@a.com") }
  }

  case class MyAdressRepo() extends AddressRepo {
    def get(id: Int) = ZFuture { s"$id@a.com" }
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
    def getUser(userId: Int)(implicit ec: ExecutionContext) = Query.lift(userRepo map (_.get(userId)))
    def findUser(email: String)(implicit ec: ExecutionContext) = Query.lift(userRepo map (_.find(email)))
    def updateUser(user: User)(implicit ec: ExecutionContext) = Query.lift(userRepo map (_.update(user)))
  }

  object UserService {
    import scalaz.concurrent.Future._

    def getEmail(userId: Int)(implicit ec: ExecutionContext) =
      for {
        user <- UserRepo.getUser(userId)
      } yield user.email

    def findAddress(email: String)(implicit ec: ExecutionContext) =
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

}