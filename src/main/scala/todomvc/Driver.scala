package todomvc

import scala.util.chaining._

// ===========================================================================
object TodoMvcReactDriver {

  def main(args: Array[String]): Unit = {
      new Model(namespace = "todo-scalajs-react-slinky")
        .tap { model => model.inform = () => render(model) }
        .pipe(render)
    }

    // ===========================================================================
    private def render(model: Model): slinky.core.facade.ReactInstance /* a js.Object */ =
      slinky.web.ReactDOM.render(
          TodoApp(model = model),
          org.scalajs.dom.document.querySelector(".todoapp") )

}

// ===========================================================================
