import korolev.state.{StateDeserializer, StateSerializer}
import korolev.{Async, Component, FormData}
import Async._

import scala.language.higherKinds

class FormComponent[F[+_]: Async, S: StateSerializer: StateDeserializer](is: S)(implicit data: FormComponentData[S])
  extends Component[F, S, Unit, S](is) {

  import context._
  import symbolDsl._

  private val (labels, construct) = data match {
    case FormComponentData.Obj(labels, construct) =>
      (labels, construct)
    case _ => ???
  }

  def render(parameters: Unit, state: S): Node = {

    val formBinding = elementId()

    'form(
      formBinding,
      labels.map { label =>
        'input('name /= label, 'placeholder /= label, 'type /= "text")
      },
      'button("Submit"),
      event('submit) { access =>
        access
          .downloadFormData(formBinding)
          .start()
          .flatMap { fromData =>
            access.publish(construct(fromData))
          }
      }
    )
  }
}

import language.experimental.macros, magnolia._

sealed trait FormComponentData[T]

object FormComponentData {
  case class Obj[T](labels: Seq[String], construct: FormData => T) extends FormComponentData[T]
  case class Primitive[T](fromString: String => T, show: T => String) extends FormComponentData[T]
}

object FormComponentDataDerivation {

  type Typeclass[T] = FormComponentData[T]

  def combine[T](ctx: CaseClass[FormComponentData, T]): FormComponentData[T] = {
    FormComponentData.Obj(
      labels = ctx.parameters.map(_.label),
      construct = { formData =>
        ctx.construct { param =>
          param.typeclass match {
            case FormComponentData.Primitive(fromString, _) =>
              fromString(formData.text(param.label))
//            case FormComponentData.Obj(_, _) =>
//              ???
          }
        }
      }
    )
  }

  def dispatch[T](ctx: SealedTrait[FormComponentData, T]): FormComponentData[T] =
    ???

  implicit val stringPrimitive: FormComponentData[String] = FormComponentData.Primitive[String](identity,
    identity)
  implicit val intPrimitive: FormComponentData[Int] = FormComponentData.Primitive[Int](_.toInt, _.toString)

  implicit def gen[T]: FormComponentData[T] = macro Magnolia.gen[T]
}
