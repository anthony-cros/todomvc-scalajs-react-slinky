// ===========================================================================
package object todomvc {
  type Todo = todomvc.Model.Todo

  // ---------------------------------------------------------------------------
  // for improved readability
  type TodoId    = String
  type TodoTitle = String

  // ===========================================================================
  // convenience extension methods for readability

  implicit class Boolean_(u: Boolean) {
    def ifTrue[B](f: => B) = if (u) Some(f) else None
  }

  // ---------------------------------------------------------------------------
  implicit class Anything_[A](u: A) {
    def ifTrue[B](p: A => Boolean)(f: => B) = if (p(u)) Some(f) else None

    // ---------------------------------------------------------------------------
    /** dearly missing in scala standard lib IMO, see https://github.com/galliaproject/gallia-core/blob/6cdf8b4/src/main/scala/aptus/misc/As.scala#L14 */
    def asSomeIf(p: A => Boolean): Option[A] = if (p(u)) Some(u) else None
    def asNoneIf(p: A => Boolean): Option[A] = if (p(u)) None    else Some(u)
  }

  // ===========================================================================
  sealed trait Filter extends enumeratum.EnumEntry // TODO: better enums are coming to scala 3

    // ---------------------------------------------------------------------------
    object Filter extends enumeratum.Enum[Filter] {
      val values = findValues

      // ---------------------------------------------------------------------------
      case object all       extends Filter
      case object active    extends Filter
      case object completed extends Filter
    }
}

// ===========================================================================
