package services.dao

import reactivemongo.bson._

object UtilBson {

  def tableTo[Obj](document: BSONDocument, key: String, transform: (TraversableBSONDocument) => Obj): Seq[Obj] = {
    val doc = document.toTraversable
    val objs = doc.getAs[BSONArray](key).getOrElse(BSONArray()).toTraversable.toList
    val seqObjs = objs.map(obj => transform(obj.asInstanceOf[BSONDocument].toTraversable))
    seqObjs.toList
  }
  
  def toArray[Obj](objs: Seq[Obj], transform: (Obj) => BSONDocument): BSONArray = {
    val array = objs.map(transform(_))
    BSONArray(array: _*)
  }

  def asString(doc: TraversableBSONDocument, key: String): String = {
    doc.getAs[BSONString](key).get.value
  }

  def asInt(doc: TraversableBSONDocument, key: String): Int = {
    doc.getAs[BSONInteger](key).get.value
  }

}