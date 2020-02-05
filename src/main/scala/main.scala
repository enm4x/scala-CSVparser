import java.util.Date
import scala.collection.mutable.ListBuffer

abstract class Element

case class All(var films: List[Film], var actors: List[Actor], var cats: List[Cat], var cars: List[Car],var persons: List[Person])

case class Cat(var name: String, var race: String, var age: Int) extends Element {
  override def toString: String =
    s"($name, $race, $age)"
}

case class Person(var firstName: String, var lastName: String, var salary: Int, var childrenNumber: Int, var age: Int) extends Element {
  override def toString: String =
    s"($firstName, $lastName, $salary, $childrenNumber)"
}

case class Car(var brand: String, var madeIn: String, var maxSpeed: Int, var gearSpeeds: Int) extends Element {
  override def toString: String =
    s"($brand, $madeIn, $maxSpeed, $gearSpeeds)"
}

case class Film(var actors: Seq[String], var releaseDate: Date) extends Element {
  override def toString: String =
    s"($actors, $releaseDate)"
}

case class Actor(var name:String, var filmography: Seq[String]) extends Element {
  override def toString: String =
    s"($name, $filmography)"
}




