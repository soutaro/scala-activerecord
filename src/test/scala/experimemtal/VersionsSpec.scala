package com.github.aselab.activerecord.experimental

import com.github.aselab.activerecord._
import org.specs2.mutable._
import org.specs2.specification._

case class DummyModel2(
  var string: String,
  var boolean: Boolean,
  var int: Int,
  var optionString: Option[String]
) extends ActiveRecord with Versionable

object DummyModel2 extends ActiveRecordCompanion[DummyModel2]

object VersionsSpec extends ActiveRecordSpecification {
  "Versions" should {
    val modelName = "com.github.aselab.activerecord.experimental.DummyModel2"

    "doUpdateでVersionに保存されること" in {
      val model = DummyModel2("str", true, 10, Some("aaa"))
      model.save
      val m1 = model.map("string" -> "bbb", "boolean" -> true)
      m1.save
      val m2 = m1.map("string" -> "bbb", "boolean" -> false)
      m2.save
      Version.all.toList must equalTo(List(
        Version(modelName, 1, "string", "str", "bbb"),
        Version(modelName, 1, "boolean", "true", "false")
      ))
    }
  }
}