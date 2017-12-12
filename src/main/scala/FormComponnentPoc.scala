import korolev.state.StateStorage
import korolev.server._
import korolev.blazeServer._
import korolev.execution._
import korolev.state.javaSerialization._
import scala.concurrent.Future
import FormComponentDataDerivation.stringPrimitive
import FormComponentDataDerivation.intPrimitive

object SimpleExample extends KorolevBlazeServer {

  import MyState.globalContext._
  import MyState.globalContext.symbolDsl._

  case class UserProfile(firstName: String, lastName: String, email: String)

  private implicit val userProfileFormData =
    FormComponentDataDerivation.gen[UserProfile]

  private def userProfileFormComponent(default: UserProfile) =
    new FormComponent[Future, UserProfile](default)

  private val emptyUserProfileFormComponent =
    userProfileFormComponent(UserProfile("", "", ""))

  val service = blazeService[Future, MyState, Any] from KorolevServiceConfig[Future, MyState, Any] (
    stateStorage = StateStorage.default(MyState()),
    render = {
      case state =>
        'body(
          state.text,
          emptyUserProfileFormComponent(()) { (access, profile) =>
            access.transition(_.copy(text = profile.toString))
          }
        )
    },
    serverRouter = ServerRouter.empty[Future, MyState]
  )
}


