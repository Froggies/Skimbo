package services.dao

import reactivemongo.bson._

object UtilBson {

  def tableTo[Obj](document: BSONDocument, key: String, transform: (BSONDocument) => Obj): Seq[Obj] = {
    val objs = document.getAs[Seq[BSONDocument]](key).getOrElse(Seq.empty)
    val seqObjs = objs.map(obj => transform(obj))
    seqObjs.toList
  }
  
  def toArray[Obj](objs: Seq[Obj], transform: (Obj) => BSONDocument): BSONArray = {
    val array = objs.map(transform(_))
    BSONArray(array)
  }

}