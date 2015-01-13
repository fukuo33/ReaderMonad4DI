package readermonad4di.p56

import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

import scalaz._
import Scalaz._
import readermonad4di.common._
import readermonad4di.p46._
import readermonad4di.p52._


object OtherMonads {
  def main(args: Array[String]) {
    val email: Reader[Env, Future[String]] = UserService.getEmail(100)
    val result = Await.result(email(productionEnv), Duration.Inf)
    println(result)
  }
}

trait UserRepoAsync {
  def get(userId: Int): Future[User]
  def find(email: String): Future[User]
  def update(user: User): Future[User]
}

trait AddressRepoAsync {
  def get(id: Int): Future[String]
}

object UserRepoAsync {
  import RepositoriesAsync.userRepo

  def getUser(userId: Int) = userRepo map (_.get(userId))
  def findUser(email: String) = userRepo map (_.find(email))
  def updateUser(user: User) = userRepo map (_.update(user))
}

object AddressRepoAsync {
  import RepositoriesAsync.addressRepo

  def getAddress(id: Int) = addressRepo map (_.get(id))
}

case class MyUserRepoAsync() extends UserRepoAsync {
  def get(userId: Int) = Future { User(userId, userId * 100, s"$userId@a.com") }
  def find(email: String) = Future { User(2, 200, email) }
  def update(user: User) = Future { User(3, 300, s"c@a.com") }
}

case class MyAddressRepoAsync() extends AddressRepoAsync {
  def get(id: Int) = Future { s"$id@a.com" }
}

object UserService {
  def getEmail(userId: Int)(implicit ec: ExecutionContext): Reader[Env, Future[String]] =
    for (userFuture <- UserRepoAsync.getUser(userId))
    yield userFuture map (_.email)

  def findAddress(email: String)(implicit ec: ExecutionContext): Reader[Env, Future[String]] =
    Env.env map { env =>
      for {
        user <- UserRepoAsync.findUser(email)(env)
        address <- AddressRepoAsync.getAddress(user.id)(env)
      } yield address
    }
}



trait RepositoriesAsync {
  def userRepo: UserRepoAsync
  def addressRepo: AddressRepoAsync
}

object RepositoriesAsync {
  import Env.repositories

  val userRepo = repositories map (_.userRepo)
  val addressRepo = repositories map (_.addressRepo)
}

trait Env {
  def config: Configuration
  def emailService: EmailService
  def repositories: RepositoriesAsync
}

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


trait MongoRepositoriesComponent extends RepositoriesComponentAsync {
  lazy val _repositories = MyRepositoriesAsync()
  def repositories: RepositoriesAsync = _repositories
}

case class MyRepositoriesAsync() extends RepositoriesAsync {
  def userRepo: UserRepoAsync = MyUserRepoAsync()
  def addressRepo: AddressRepoAsync = MyAddressRepoAsync()
}

trait RepositoriesComponentAsync {
  def repositories: RepositoriesAsync
}
