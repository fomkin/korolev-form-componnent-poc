import korolev.Context
import korolev.execution._
import scala.concurrent.Future
import korolev.state.javaSerialization._

case class MyState(text: String = "Hello world")

object MyState {
  val globalContext = Context[Future, MyState, Any]
}
