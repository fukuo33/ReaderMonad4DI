package readermonad9

import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

import scalaz._
import Scalaz._
import scalaz.concurrent.{Future => ZFuture}

/**
 * p63
 */
object MonadTransformers {
  def main(args: Array[String]) {
    println("Hello MonadTransformers!")

    println(getUser(5)(MyUserRepo()).run)
    println(UserRepo.getUser(5)(MyUserRepo()).run)
  }

  def getUser(userId: Int) =
    ReaderTFuture[UserRepo, User](_.get(userId))

  def getEmail(userId: Int) =
    for (user <- getUser(userId))
    yield user.email

  def getSupervisor(userId: Int) =
    for {
      user <- getUser(userId)
      supervisor <- getUser(user.supervisorId)
    } yield supervisor

  case class User(id: Int, supervisorId: Int, email: String)

  trait UserRepo {
    def get(userId: Int): ZFuture[User]
    def find(email: String): ZFuture[User]
    def update(user: User): ZFuture[User]
  }

  object UserRepo {
    def getUser(userId: Int) = ReaderTFuture[UserRepo, User](_.get(userId))
    def findUser(email: String) = ReaderTFuture[UserRepo, User](_.find(email))
    def updateUser(user: User) = ReaderTFuture[UserRepo, User](_.update(user))
  }

  case class MyUserRepo() extends UserRepo {
    def get(userId: Int): ZFuture[User] = ZFuture { User(userId, userId * 100, s"$userId@a.com") }
    def find(email: String): ZFuture[User] = ZFuture { User(2, 200, email) }
    def update(user: User): ZFuture[User] = ZFuture { User(3, 300, s"c@a.com") }
  }

  type ReaderTFuture[A, B] = ReaderT[ZFuture, A, B]
  object ReaderTFuture extends KleisliFunctions with KleisliInstances {
    def apply[A, B](f: A => ZFuture[B]): ReaderTFuture[A, B] = kleisli(f)
  }

}
