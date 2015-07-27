package controllers

import javax.inject._
import domain.{CatDAO, DAOTrait}
import models.CatModel.{Cat, Cats}
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.io.File

import scala.concurrent.Future

/**
 * Cats manager
 */
class Application @Inject() (dao: DAOTrait) extends Controller {

  /**
   * This method calls for template of home page
   */
  def index = Action {
    Ok(views.html.index())
  }

  /**
   * This method calls for view of all cats
   */
  def catView = Action {
    Ok(views.html.list())
  }

  /**
   * This method calls for a view of a single cat
   */
  def oneCatView (id: Int)= Action {
    Ok(views.html.view())
  }

  /**
   * This method call for a view of cats editor page
   */
  def editCatView = Action {
    Ok(views.html.edit())
  }

  /**
   * This method returns data about all cats from database
   */
  def getCats = Action {
    Ok(Json.toJson(dao.getAll))
    //try {
      //Ok(Json.toJson(dao.getAll))
    //} catch {
    //  case e: Exception => InternalServerError("Some errors have occured while retriving all cats from database")
    //}

  }

  /**
   * This method returns data about one cat from database
   */
  def getOneCat(id: Int) = Action {
    try {
      Ok(Json.toJson(dao.findById(id)))
    } catch {
      case e: Exception => InternalServerError("Some errors have occured while retriving one cat from database")
    }
  }

  /**
   * This method call for form to create a new cat
   */
  def showCatForm = Action {
    Ok(views.html.newCatForm())
  }

  /**
   * This method creates a cat in database with data from form
   */
  def newCat = Action(parse.multipartFormData) { implicit request =>
    var filenameBuilder = Option("assets/images/default.jpg")
    request.body.file("picture").map { picture =>
      val filename = picture.filename
      val contentType = picture.contentType
      picture.ref.moveTo(new File(s"assets/images/$filename"))
      filenameBuilder = Option("assets/images/" + filename)
      Ok("File uploaded")
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Missing file")
    }
    catForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.index())
      },
      catData => {
        val newKitty: Cat = models.CatModel.Cat(null, catData.name, catData.color, catData.race, catData.gender, filenameBuilder)
        try {
          dao.insert(newKitty)
          Redirect(routes.Application.index).flashing("success" -> "Cat saved!")
        } catch {
          case e: Exception => InternalServerError("Some errors have occured while writing cat data to database")
        }
      }
    )
  }

  /**
   * This method edits a cat
   */
  def editCat = Action(parse.multipartFormData) { implicit request =>
    var filenameBuilder = Option("assets/images/default.jpg")
    request.body.file("picture").map { picture =>
      val filename = picture.filename
      val contentType = picture.contentType
      picture.ref.moveTo(new File(s"assets/images/$filename"))
      filenameBuilder = Option("assets/images/" + filename)
      Ok("File uploaded")
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Missing file")
    }
    catForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.index())
      },
      catData => {
        val editedKitty: Cat = models.CatModel.Cat(catData.id, catData.name, catData.color, catData.race, catData.gender, filenameBuilder)
        try {
          dao.update(catData.id.getOrElse(0), editedKitty)
          Redirect(routes.Application.index).flashing("success" -> "Cat updated!")
        } catch {
          case e: Exception => InternalServerError("Some errors have occured while updating cat data to database")
        }
      }
    )
  }

  /**
   * This method deletes a cat from database
   */
  def deleteCat(id: Int): Action[AnyContent] = Action {
    try {
      dao.delete(id)
      Ok(Json.obj("status" -> "OK", "message" -> ("Cat was deleted.")))
    } catch {
      case e: Exception => InternalServerError("Some errors have occured while deleting cat from database")
    }
  }

  /**
   * Cat's form to receive data from user
   */
  val catForm = Form(
    mapping(
      "id" -> optional(number),
      "name" -> nonEmptyText,
      "color" -> optional(nonEmptyText),
      "race" -> optional(nonEmptyText),
      "gender" -> optional(nonEmptyText),
      "url" -> optional(nonEmptyText))(Cat.apply)(Cat.unapply))

  //This method converts cat to JSON
  implicit val catWrites: Writes[Cat] = (
    (JsPath \ "id").write[Option[Int]] and
      (JsPath \ "name").write[String] and
      (JsPath \ "color").write[Option[String]] and
      (JsPath \ "race").write[Option[String]] and
      (JsPath \ "gender").write[Option[String]] and
      (JsPath \ "url").write[Option[String]]
    )(unlift(Cat.unapply))

}



