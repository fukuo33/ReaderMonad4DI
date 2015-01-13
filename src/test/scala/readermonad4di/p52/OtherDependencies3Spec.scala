package readermonad4di.p52

import org.specs2.mock._
import org.specs2.mutable.SpecificationWithJUnit
import readermonad4di.common._
import readermonad4di.p46._

/**
 * Created by fukuo33 on 12/21/14.
 */
class OtherDependencies3Spec extends SpecificationWithJUnit with Mockito {

  object testEnv extends Env
  with MockConfigComponent
  with MockEmailServiceComponent
  with MockRepositoriesComponent {
    override lazy val repositories = mock[Repositories]
  }

  trait MockConfigComponent extends Configuration {
    def config: Configuration = ProductionConfiguration()
  }

  trait MockEmailServiceComponent extends EmailServiceComponent {
    def emailService: EmailService = ProductionEmailService()
  }

  trait MockRepositoriesComponent extends RepositoriesComponent {
    def repositories: Repositories = MyRepositories()
  }

  "UserService#getEmail" should {
    "return mock email" in {
      val mockUserRepo = mock[UserRepo]
      mockUserRepo.get(100) returns User(999, 9999, "mock@mock.com")
      testEnv.repositories.userRepo returns mockUserRepo

      UserService.getEmail(100)(testEnv) must_== "mock@mock.com"
    }
  }

}
