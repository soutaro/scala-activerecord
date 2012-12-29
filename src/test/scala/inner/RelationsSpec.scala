package com.github.aselab.activerecord.inner

import org.specs2.mutable._
import org.specs2.specification._

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._
import models._

object RelationsSpec extends ActiveRecordSpecification {
  override def before = {
    super.before
    TestTables.createTestData
  }

  "Relations" should {
    def relation = PrimitiveModel.all

    "#orderBy" >> {
      "single field" >> {
        relation.orderBy(m => m.int desc).toList mustEqual PrimitiveModel.all.toList.reverse
      }

      "multiple fields" >> {
        relation.orderBy(m => m.oint asc, m => m.int desc).toList mustEqual PrimitiveModel.all.toList.sortWith {
          (m1, m2) => (m1.oint, m2.oint) match {
            case (a, b) if a == b => m1.int > m2.int
            case (Some(a), Some(b)) => a < b
            case (None, Some(b)) => true
            case (Some(a), None) => false
            case _ => throw new Exception("")
          }
        }
      }
    }

    "#limit returns only specified count" >> {
      relation.limit(10).toList mustEqual PrimitiveModel.all.toList.take(10)
    }

    "#page" >> {
      relation.page(30, 10).toList mustEqual PrimitiveModel.all.toList.slice(30, 40)
    }

    "#count" >> {
      relation.count mustEqual(100)
    }

    "#select" >> {
      relation.select(m => (m.id, m.string)).toList mustEqual PrimitiveModel.all.toList.map(m => (m.id, m.string))
    }

    "#joins" >> withRollback {
      val u1 = User("string50").create
      PrimitiveModel.joins[User]((p, u) => p.string === u.name)
        .where((p, u) => u.name === u1.name).headOption mustEqual
        PrimitiveModel.findBy("string", u1.name)
    }

    "toSql" >> {
      PrimitiveModel.all.toSql mustEqual
        inTransaction { PrimitiveModel.all.toQuery.statement }
      PrimitiveModel.where(m => m.id === 1).toSql mustEqual
        inTransaction { PrimitiveModel.where(m => m.id === 1).toQuery.statement }
    }
  }
}
