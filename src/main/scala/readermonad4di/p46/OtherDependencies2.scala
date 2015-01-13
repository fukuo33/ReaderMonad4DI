package readermonad4di.p46

import scalaz._
import Scalaz._
import readermonad4di.common._


object OtherDependencies2 {

  def main(args: Array[String]) {
    println(UserService.getEmail(100)(ProductionEnv()))
  }
}

object AddressRepo {
  import Repositories.addressRepo

  def getAddress(id: Int) = addressRepo map (_.get(id))
}

trait EmailService {
}

trait Configuration {
}

// p46
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
