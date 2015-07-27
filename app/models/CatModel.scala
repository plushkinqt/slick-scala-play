package models

//import slick.driver.MySQLDriver.api._
import slick.driver.PostgresDriver.api._

object CatModel {

  case class Cat(id: Option[Int], name: String, color: Option[String], race: Option[String], gender: Option[String], url: Option[String])

  class Cats(tag: Tag) extends Table[Cat](tag, "cats"){

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def color = column[String]("color")
    def race = column[String]("race")
    def gender = column[String]("gender")
    def url = column[String]("url")

    def * = (id.?, name, color.?, race.?, gender.?, url.?) <> (Cat.tupled, Cat.unapply)

  }
}

