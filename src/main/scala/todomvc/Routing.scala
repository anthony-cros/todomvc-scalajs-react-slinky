package todomvc

import scala.util.chaining._ // for .pipe
import org.scalajs.dom

// ===========================================================================
object Routing { // no bindings for director? (not even in ScalablyTyped)

  def apply(updateState: Filter => Unit) {
    dom.window.onload       = _ => updateState(hashFilter())
    dom.window.onhashchange = _ => updateState(hashFilter())
  }

  // ---------------------------------------------------------------------------
  private def hashFilter(): Filter =
    dom.window.location
      .hash
      .stripPrefix("#/")
      .pipe     (Filter.withNameOption)
      .getOrElse(Filter.all)

}

// ===========================================================================
