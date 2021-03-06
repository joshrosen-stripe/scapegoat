package com.sksamuel.scapegoat

import org.scalatest.{FreeSpec, Matchers, OneInstancePerTest, PrivateMethodTester}

import scala.reflect.internal.util.NoPosition
import scala.tools.nsc.reporters.StoreReporter

/** @author Stephen Samuel */
class FeedbackTest
    extends FreeSpec
    with Matchers
    with OneInstancePerTest with PrivateMethodTester {

  val position = NoPosition
  val defaultSourcePrefix = "src/main/scala/"

  class DummyInspection(text: String, defaultLevel: Level) extends Inspection(text, defaultLevel) {
    override def inspector(context: InspectionContext): Inspector = ???
  }

  "Feedback" - {
    "should report to reporter" - {
      "for error" in {
        val inspection = new DummyInspection("My default is Error", Levels.Error)
        val reporter = new StoreReporter
        val feedback = new Feedback(false, reporter, defaultSourcePrefix)
        feedback.warn(position, inspection)
        reporter.infos should contain(reporter.Info(position, "My default is Error", reporter.ERROR))
      }
      "for warning" in {
        val inspection = new DummyInspection("My default is Warning", Levels.Warning)
        val reporter = new StoreReporter
        val feedback = new Feedback(false, reporter, defaultSourcePrefix)
        feedback.warn(position, inspection)
        reporter.infos should contain(reporter.Info(position, "My default is Warning", reporter.WARNING))
      }
      "for info" in {
        val inspection = new DummyInspection("My default is Info", Levels.Info)
        val reporter = new StoreReporter
        val feedback = new Feedback(false, reporter, defaultSourcePrefix)
        feedback.warn(position, inspection)
        reporter.infos should contain(reporter.Info(position, "My default is Info", reporter.INFO))
      }
    }
    "should use proper paths" - {
      "for `src/main/scala`" in {
        val normalizeSourceFile = PrivateMethod[String]('normalizeSourceFile)
        val reporter = new StoreReporter
        val feedback = new Feedback(true, reporter, defaultSourcePrefix)
        val source = "src/main/scala/com/sksamuel/scapegoat/Test.scala"
        val result = feedback invokePrivate normalizeSourceFile(source)
        result should ===("com.sksamuel.scapegoat.Test.scala")
      }

      "for `app`" in {
        val normalizeSourceFile = PrivateMethod[String]('normalizeSourceFile)
        val reporter = new StoreReporter
        val feedback = new Feedback(true, reporter, "app/")
        val source = "app/com/sksamuel/scapegoat/Test.scala"
        val result = feedback invokePrivate normalizeSourceFile(source)
        result should ===("com.sksamuel.scapegoat.Test.scala")
      }

      "should add trailing / to the sourcePrefix automatically" in {
        val normalizeSourceFile = PrivateMethod[String]('normalizeSourceFile)
        val reporter = new StoreReporter
        val feedback = new Feedback(true, reporter, "app/custom")
        val source = "app/custom/com/sksamuel/scapegoat/Test.scala"
        val result = feedback invokePrivate normalizeSourceFile(source)
        result should ===("com.sksamuel.scapegoat.Test.scala")
      }
    }
  }
}