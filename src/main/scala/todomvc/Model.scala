package todomvc

import scala.util.chaining._ // for .pipe
import scala.scalajs.js
import org.scalajs.dom.window.localStorage

// ===========================================================================
class Model(namespace: String) { import Model.Todo
  var inform: () => Unit = _ // probably not a great way to go about it...

  // ===========================================================================
  private def readTodos(): Seq[Todo] =
    localStorage.getItem(namespace)
      .pipe(Option.apply)
      .map (js.JSON.parse(_))
      .map (_.asInstanceOf[js.Array[js.Dynamic]])
      .map (_.map(Todo.fromJS).toList)
      .getOrElse(Nil)

  // ---------------------------------------------------------------------------
  private def writeTodos(values: Seq[Todo]) {
    localStorage.setItem(namespace,
      values
        .map (_.toJS)
        .pipe(js.Array.apply)
        .pipe(js.JSON.stringify(_)) )
  }

  // ===========================================================================
  def readAll(): Seq[Todo] = readTodos()

  // ---------------------------------------------------------------------------
  def add(title: TodoId) {
    (readTodos() :+ Todo(title)).pipe(writeTodos)
    inform()
  }

  // ---------------------------------------------------------------------------
  def remove(id: TodoId) {
    readTodos().filterNot(_.id == id).pipe(writeTodos)
    inform()
  }

  // ---------------------------------------------------------------------------
  def save(id: TodoId, newTitle: TodoTitle) {
    readTodos().map { todo => if (todo.id != id) todo else todo.copy(title = newTitle) }.pipe(writeTodos)
    inform()
  }

  // ---------------------------------------------------------------------------
  def toggle(id: TodoId) {
    readTodos().map { todo => if (todo.id != id) todo else todo.copy(completed = !todo.completed) }.pipe(writeTodos)
    inform()
  }

  // ---------------------------------------------------------------------------
  def toggleAll(checked: Boolean) {
    readTodos().map(_.copy(completed = checked)).pipe(writeTodos)
    inform()
  }

  // ---------------------------------------------------------------------------
  def clearCompleted() {
    readTodos().filterNot(_.completed).pipe(writeTodos)
    inform()
  }

}

// ===========================================================================
object Model {

  case class Todo(
        title    : String,

        // TODO: can't use java.time.Instant in scalajs?
        id       : String  = new js.Date().getTime.toString, // eg 1621282715000
        completed: Boolean = false) {

      override def toString: String = s"""${if (completed) "x" else "o"}-"${title}""""

      // TODO: can't get upickle to work ("object write is not a member of package upickle") - docs outdated maybe?
      def toJS = js.Dynamic.literal(
          "title"     -> title,
          "id"        -> id,
          "completed" -> completed)
    }

    // ---------------------------------------------------------------------------
    object Todo {

      def fromJS(value: js.Dynamic): Todo =
        Todo(
            title      = value.title    .asInstanceOf[String],
            id         = value.id       .asInstanceOf[String],
            completed  = value.completed.asInstanceOf[Boolean])

    }

}

// ===========================================================================
