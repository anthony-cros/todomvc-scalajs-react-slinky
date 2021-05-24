package todomvc

import slinky.core.annotations.react
import slinky.core.StatelessComponent
import slinky.web.html._

// ===========================================================================
@react class TodoFooter extends StatelessComponent {

  case class Props(
         activeCount  : Int,
      completedCount  : Int,
      nowShowing      : Filter,
      onClearCompleted: () => Unit) {

    def activeTodoWord: String = s"item${if (activeCount == 1) "" else "s"}"

    def selectedIf(filter: Filter): Option[String] = if (nowShowing == filter) Some("selected") else None
  }

  // ---------------------------------------------------------------------------
  override def render() =
    footer(className := "footer")(

      span(className := "todo-count")(
        strong(props.activeCount), " ", props.activeTodoWord, " left"),

      ul(className := "filters")(
        li(a(href := "#/",          className := props.selectedIf(Filter.all      ))("All")       ), " ",
        li(a(href := "#/active",    className := props.selectedIf(Filter.active   ))("Active")    ), " ",
        li(a(href := "#/completed", className := props.selectedIf(Filter.completed))("Completed") ) ),

    // ---------------------------------------------------------------------------
    props.completedCount.ifTrue(_ > 0) {
      button(
          className := "clear-completed",
          onClick   := props.onClearCompleted)(
        "Clear completed" ) } )

}

// ===========================================================================
