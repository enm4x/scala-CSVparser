import java.io.{BufferedWriter, File, FileWriter}
import java.text.{ParseException, SimpleDateFormat}
import java.util.Date
import play.api.libs.json.Json
import scala.io.Source

object CsvReader extends App {

  /*
  * Aim of the function :
  * adding an element to any list
  * using polymorphism with [T] before the parameters to authorize any type
  * cf : add an element car into the list ListedCar
  */
    def addElementToList[T](list: List[T], obj: T): List[T] ={
     /*Pattern matching pour gerer les possibles effets de bords*/
      (obj, list) match{
        case (isFilm :Film, (elem1:Film)::rest) => obj :: list
        case (isActor :Actor, (elem1:Actor)::rest) =>  obj :: list
        case (isCat :Cat, (elem1:Cat)::rest) =>  obj :: list
        case (isCar :Car, (elem1:Car)::rest) =>  obj :: list
        case (isPerson :Person, (elem1:Person)::rest) => obj :: list
        case (_, _) => println("je suis une erreur"); Nil
      }
    }

  /*
  * Aim of the function :
  * if a string is a Date then change is type to a date type else null
  */
    def toDate(string: String): Date = {
      val DATE_FORMAT = "dd/MM/yyyy"
      val dateFormat = new SimpleDateFormat(DATE_FORMAT)
        try {
        //Try to parse date
          val pDate = dateFormat.parse(string)
          pDate
        } catch {
          case e: ParseException => null
        }
    }

  /*
  * Aim of the function :
  * recursive function parsing the file and detecting in which case we are in order to instantiate a new element in the correct class
  * iterator is used to check each line and we use a try and catch in order to detect the end of file and qui the function
  * we use the function addElementToList to create a copy of the previous list and to maintain the imutability of the data
  * return : all the list updated
  */
    def parseNewFile(buffer: Iterator[String], films: List[Film], actors: List[Actor], cats: List[Cat], cars: List[Car],
        persons: List[Person]): (List[Film], List[Actor], List[Cat], List[Car], List[Person]) = {
      try {
        val line = buffer.next()
        val cols = line.split(",").map(_.trim)

        cols.length match {
          case 2 => val parsedDate = toDate(cols(1));
              if (parsedDate != null) {
                val tmp_list = addElementToList(films, new Film(cols(0).split(";").toSeq, parsedDate));
                parseNewFile(buffer, tmp_list, actors, cats, cars, persons)
              } else {
                  val tmp_list = addElementToList(actors, new Actor(cols(0), cols(1).split(";").toSeq));
                  parseNewFile(buffer, films, tmp_list, cats, cars, persons)
              }
          case 3 => val tmp_list = addElementToList(cats, new Cat(cols(0), cols(1), cols(2).toInt));
                    parseNewFile(buffer, films, actors, tmp_list, cars, persons)
          case 4 => val tmp_list = addElementToList(cars, new Car(cols(0), cols(1), cols(2).toInt, cols(3).toInt));
                    parseNewFile(buffer, films, actors, cats, tmp_list, persons)
          case 5 => val tmp_list = addElementToList(persons,  new Person(cols(0), cols(1), cols(2).toInt, cols(3).toInt, cols(4).toInt));
                    parseNewFile(buffer, films, actors, cats, cars, tmp_list)
        }
      } catch {
        case e: Exception => return (films.dropRight(1), actors.dropRight(1), cats.dropRight(1), cars.dropRight(1), persons.dropRight(1))
      }
    }

  /*
  * Initialize all List with a first pointer
  * Allow us to retrieve the list type
  */
  def initiateList(): (List[Film], List[Actor], List[Cat], List[Car], List[Person]) = {
    // Initialize Lists of all object types and for each, add an empty element
    val listedFilm = List(new Film( Seq("") , null))
    val listedActor = List(new Actor("", Seq("")))
    val listedCat = List(new Cat("","", 0))
    val listedCar = List(new Car("", "",0, 0))
    val listedPerson = List(new Person("","", 0, 0, 0))

    return (listedFilm, listedActor, listedCat, listedCar, listedPerson)
  }

/*
* Aim of the function :
* take list as parameters and convert into json each element of each list
* output the result in a new json file
*/
  def jsonifyAndCreateFile(films: List[Film],actors: List[Actor],cats:  List[Cat],cars: List[Car],persons: List[Person]): Unit ={
    // Generate implicit values that condition the format of the JSON writing
    implicit val catWrites = Json.format[Cat]
    implicit val filmWrites = Json.format[Film]
    implicit val actorWrites = Json.format[Actor]
    implicit val carWrites = Json.format[Car]
    implicit val personWrites = Json.format[Person]
    implicit val allWrites = Json.format[All]

    // Generate an instance of the class that contains all the lists
    val all = new All(films, actors, cats, cars, persons)
    // Jsonify the all class
    val allJson = Json.toJson(all)
    // Open, create, write and close the writing of the Json content into the Json file
    val file = new File("./data/output.json")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(allJson.toString)
    bw.close()
  }


  def main() : Unit = {

  // Read CSV
  val source = Source.fromFile("./data/data.csv")
  val inputFile = source.getLines

  // Initiate a tuple containing all the Objects Lists
  val (listedFilm, listedActor, listedCat, listedCar, listedPerson) = initiateList()
  // Parse the inputFile and assign it to the corresponding lists
  val (films, actors, cats, cars, persons) = parseNewFile(inputFile, listedFilm, listedActor, listedCat, listedCar, listedPerson)
  // Jsoninfy the filled lists and create the output file
  jsonifyAndCreateFile(films, actors, cats, cars, persons)

  // Close the reading of the file
  source.close
  }

main()
}

