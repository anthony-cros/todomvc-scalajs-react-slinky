package todomvc

import org.scalajs.dom
import slinky.core.annotations.react
import slinky.core.facade.React
import slinky.core.Component
import slinky.web.html._

// ===========================================================================
@react class TodoItem extends Component {

  // ---------------------------------------------------------------------------
  case class Props(
      key             : String,  // = todo.id
      todo            : Todo,
      currentlyEditing: Boolean,

      // ---------------------------------------------------------------------------
      onToggle :  TodoId             => Unit,
      onDestroy:  TodoId             => Unit,
      onEdit   :  TodoId             => Unit,
      onSave   : (TodoId, TodoTitle) => Unit,
      onCancel : ()                  => Unit)    {

    def className: String =
           if (todo.completed)   "completed"
      else if (currentlyEditing) "editing"
      else                       ""
  }

  // ---------------------------------------------------------------------------
  case class State(editText: String = "")

  // ---------------------------------------------------------------------------
  def initialState = State()

  // ---------------------------------------------------------------------------
  private val inputRef = React.createRef[dom.html.Input] // see https://slinky.dev/docs/refs/

  // ===========================================================================
  override def render() =
    li(className := props.className)(
      div(className := "view")(

        input(
          className:= "toggle",
         `type`    := "checkbox",
          checked  :=        props.todo.completed,
          onChange := { _ => props.onToggle(props.todo.id) } ),

        label(onDoubleClick := { _ => handleEdit() })(props.todo.title),

        button(
            className := "destroy",
            onClick   := { _ => props.onDestroy(props.todo.id) }) ),

      input(
        ref       := inputRef,

        className := "edit",
        value     := state.editText,

        onBlur    := { _     => handleSubmit (props.todo.id)      },
        onChange  := { event => handleChange (event.target.value) },
        onKeyDown := { event => handleKeyDown(event.which)        }) )

  // ===========================================================================
  override def shouldComponentUpdate(nextProps: Props, nextState: State) = // optional optimization
        nextProps.todo             != props.todo             ||
        nextProps.currentlyEditing != props.currentlyEditing ||
        nextState.editText         != state.editText

  // ---------------------------------------------------------------------------
  override def componentDidUpdate(prevProps: Props, ignored: State) {
    if (!prevProps.currentlyEditing && props.currentlyEditing) {
      val node = inputRef.current
      node.focus()
      node.setSelectionRange(node.value.length, node.value.length)
    }
  }

  // ===========================================================================
  private def handleEdit() {
    props.onEdit(props.todo.id)
    setState(_.copy(editText = props.todo.title))
  }

  // ---------------------------------------------------------------------------
  private def handleSubmit(id: TodoId)  {
    val trimmedValue = state.editText.trim

    if (trimmedValue.isEmpty) {
      props.onDestroy(id)
    } else {
      props.onSave(id, trimmedValue)
      setState(_.copy(editText = trimmedValue))
    }
  }

  // ---------------------------------------------------------------------------
  private def handleChange(value: String) {
    if (props.currentlyEditing) {
      setState(_.copy(editText = value))
    }
  }

  // ---------------------------------------------------------------------------
  private def handleKeyDown(whichKey: Int) {
    whichKey match {
      case dom.ext.KeyCode.Escape =>
        setState(_.copy(editText = props.todo.title))
        props.onCancel()

      case dom.ext.KeyCode.Enter  => handleSubmit(props.todo.id)
      case _                      => ()
    }
  }

}

// ===========================================================================
