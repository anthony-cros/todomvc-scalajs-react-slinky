package todomvc

import org.scalajs.dom
import slinky.core.annotations.react
import slinky.core.Component
import slinky.web.html._

// ===========================================================================
@react class TodoApp extends Component {

  case class Props(model: Model)

  // ---------------------------------------------------------------------------
  case class State(
      nowShowing: Filter = Filter.all,

      // can't seem to use Options here, causes: "Warning: A component is changing an uncontrolled input to be controlled ..."
      editing   : TodoId    = "", // currently editing Id
      newTodo   : TodoTitle = "") {

    // ---------------------------------------------------------------------------
    def resetTitle             : State = copy(newTodo = "")
    def resetCurrentlyEditingId: State = copy(editing = "")

    // ---------------------------------------------------------------------------
    def currentlyEdited(id: TodoId): Boolean = editing == id

    // ---------------------------------------------------------------------------
    def shown(todo: Model.Todo): Boolean =
      nowShowing match {
          case Filter.all       =>  true
          case Filter.active    => !todo.completed
          case Filter.completed =>  todo.completed }
  }

  // ===========================================================================
  override def initialState = State()

  // ---------------------------------------------------------------------------
  override def componentDidMount() { Routing(filter => setState(_.copy(nowShowing = filter))) }

  // ===========================================================================
  override def render() = {
    val allTodos: Seq[Todo] = props.model.readAll()

    val    activeCount = allTodos.count(!_.completed)
    val completedCount = allTodos.count( _.completed)

    // ---------------------------------------------------------------------------
    div(

      // header
      header(className := "header")(
        h1("todos"),
        input(
          className   := "new-todo",
          placeholder := "What needs to be done?",
          value       := state.newTodo,

          onKeyDown   := (event => handleNewTodoKeyDown(event.keyCode, { () => event.preventDefault() })),
          onChange    := (event => setState(_.copy(newTodo = event.target.value))),

          autoFocus   := true ) ),

      // ---------------------------------------------------------------------------
      // main
      allTodos.ifTrue(_.nonEmpty) {
        section(className:="main")(
            input(
              id        := "toggle-all",
              className := "toggle-all",
             `type`     := "checkbox",
              onChange  := { event => props.model.toggleAll(event.target.checked) },
              checked   := activeCount == 0),

            label(htmlFor := "toggle-all"),

            ul(className := "todo-list")(
              allTodos
                .filter(state.shown)
                .map { shownTodo =>
                  div(key := shownTodo.id)( // not sure why but key doesn't work properly on <li> (TODO)
                    TodoItem(
                      key              = shownTodo.id,
                      todo             = shownTodo,
                      currentlyEditing = state.currentlyEdited(shownTodo.id),

                      onToggle         = {  id         => props.model.toggle(id) },
                      onDestroy        = {  id         => props.model.remove(id) },
                      onEdit           = {  id         => setState(_.copy(editing = id)) },
                      onSave           = { (id, title) => props.model.save(id, title); setState(_.resetCurrentlyEditingId) },
                      onCancel         = { ()          =>                              setState(_.resetCurrentlyEditingId) }) ) } ) ) },

      // ---------------------------------------------------------------------------
      // footer
      (activeCount > 0 || completedCount > 0).ifTrue {
        TodoFooter(
             activeCount   =    activeCount,
          completedCount   = completedCount,
          nowShowing       = state.nowShowing,
          onClearCompleted = { () => props.model.clearCompleted() } ) })
  }

  // ===========================================================================
  private def handleNewTodoKeyDown(
      keyCode       : Int,
      preventDefault: () => Unit) {
    if (keyCode == dom.ext.KeyCode.Enter) {
      state
        .newTodo
        .trim()
        .asNoneIf(_.isEmpty)
        .map { nonEmptyTrimmedValue =>
          preventDefault()
          props.model.add(nonEmptyTrimmedValue)
          setState(_.resetTitle) }
    }
  }

}

// ===========================================================================
