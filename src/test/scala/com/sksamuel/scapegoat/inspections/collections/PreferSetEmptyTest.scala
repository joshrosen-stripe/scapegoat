package com.sksamuel.scapegoat.inspections.collections

import com.sksamuel.scapegoat.PluginRunner
import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest}

/** @author Stephen Samuel */
class PreferSetEmptyTest extends FreeSpec with Matchers with PluginRunner with OneInstancePerTest {

  override val inspections = Seq(new PreferSetEmpty)

  "new empty set" - {
    "should report warning" in {

      val code = """object Test {
                      val a = Set[String]()
                      val b = Set.empty
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 1
    }
  }
  "Set.empty" - {
    "should not report warning" in {

      val code = """object Test {
                      val b = Set.empty
                    } """.stripMargin

      compileCodeSnippet(code)
      compiler.scapegoat.feedback.warnings.size shouldBe 0
    }
  }
}