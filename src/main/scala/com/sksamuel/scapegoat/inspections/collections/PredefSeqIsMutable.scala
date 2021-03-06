package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.{ Levels, Inspection, InspectionContext, Inspector, isScala213 }

/** @author Stephen Samuel */
class PredefSeqIsMutable extends Inspection("Predef.Seq is mutable", Levels.Info,
  "Predef.Seq aliases scala.collection.mutable.Seq. Did you intend to use an immutable Seq?") {

  def inspector(context: InspectionContext): Inspector = new Inspector(context) {
    override def postTyperTraverser = if (isScala213) None else Some(
      new context.Traverser {

        import context.global._

        override def inspect(tree: Tree): Unit = {
          tree match {
            case DefDef(mods, _, _, _, _, _) if tree.symbol.isAccessor =>
            case TypeTree() if tree.tpe.erasure.toString() == "Seq[Any]" => warn(tree)
            case _ => continue(tree)
          }
        }

        def warn(tree: Tree): Unit = {
          context.warn(tree.pos, self)
        }
      }
    )
  }
}
