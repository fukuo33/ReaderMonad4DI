package readermonad4

import scalaz._
import Scalaz._

/**
  * p35
  */
object DependencyInjection {

   def main(args: Array[String]) {
     println("Hello Reader Monad!")

     println(getUser(5)(MysqlUserRepo()))
     println(UserRepo.getUser(5)(MysqlUserRepo()))
     println(UserRepo2.getUser(5)(MysqlUserRepo()))
   }

   def getUser(userId: Int) =
     Reader[UserRepo, User](_.get(userId))

   def getEmail(userId: Int) =
     for (user <- getUser(userId))
     yield user.email

   def getSupervisor(userId: Int) =
     for {
       user <- getUser(userId)
       supervisor <- getUser(user.supervisorId)
     } yield supervisor

 }

case class User(id: Int, supervisorId: Int, email: String)

trait UserRepo {
  def get(userId: Int): User
  def find(email: String): User
  def update(user: User): User
}

// p40
object UserRepo {
  def getUser(userId: Int) = Reader[UserRepo, User](_.get(userId))
  def findUser(email: String) = Reader[UserRepo, User](_.find(email))
  def updateUser(user: User) = Reader[UserRepo, User](_.update(user))
}

// p41
object UserRepo2 {
  val userRepo = Reader[UserRepo, UserRepo](identity)
  def getUser(userId: Int) = userRepo map (_.get(userId))
  def findUser(email: String) = userRepo map (_.find(email))
  def updateUser(user: User) = userRepo map (_.update(user))
}

case class MysqlUserRepo() extends UserRepo {
  def get(userId: Int): User = User(userId, userId * 100, "a@a.com")
  def find(email: String): User = User(2, 200, "b@a.com")
  def update(user: User): User = User(3, 300, "c@a.com")
}