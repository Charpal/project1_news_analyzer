package example

import scala.io.StdIn.readLine
import scala.collection.mutable.ListBuffer
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken}
import com.danielasfregola.twitter4s.entities.{HashTag, Tweet}
import com.danielasfregola.twitter4s.entities.LocationTrends
import scala.concurrent.ExecutionContext.Implicits.global


object Menu {
    class User(private var firstName: String, private var lastName: String, 
    private var email: String, private var userName: String, private var passWord: String, 
    private var permission: String){
        def setFirstName(newName: String): Unit = {
            firstName = newName
        }

        def getFirstName(): String = {
            firstName
        }

        def setLastName(newName: String): Unit = {
            lastName = newName
        }

        def getLastName(): String = {
            lastName
        }

        def setEmail(newEmail: String): Unit = {
            email = newEmail
        }

        def getEmail(): String = {
            email
        }

        def setUserName(newName: String): Unit = {
            userName = newName
        }

        def getUserName(): String = {
            userName
        }

        def setPassWord(newPass: String): Unit = {
            passWord = newPass
        }

        def getPassWord(): String = {
            passWord
        }

        def setPermissionLevel(newPermission: String): Unit = {
            permission = newPermission
        }

        def getPermissionLevel(): String = {
            permission
        }
    }

//     class BaseUser(firstName: String, lastName: String, 
//  email: String, userName: String, passWord: String) extends User(firstName, lastName, email,
//  userName, passWord){
//     override def getPermissionLevel(): String = "Base"
//  }

//     class AdminUser(firstName: String, lastName: String, 
//  email: String, userName: String, passWord: String) extends User(firstName, lastName, email,
//  userName, passWord){
//      override def getPermissionLevel(): String = "Admin"
//  }

    var activeAccount = new User("N/A", "N/A", "N/A", "N/A", "N/A", "N/A")
    var userDataBase = new ListBuffer[User]()

//     def getTopHashtags(tweets: Seq[Tweet], n: Int = 10): Seq[(String, Int)] = {
//     val hashtags: Seq[Seq[HashTag]] = tweets.map { tweet =>
//       tweet.entities.map(_.hashtags).getOrElse(List.empty)
//     }
//     val hashtagTexts: Seq[String]            = hashtags.flatten.map(_.text.toLowerCase)
//     val hashtagFrequencies: Map[String, Int] = hashtagTexts.groupBy(identity).map { case (k, v) => k -> v.size }
//     hashtagFrequencies.toSeq.sortBy { case (_, frequency) => -frequency }.take(n)
//   }    

def printTrendingTopics(locationTrends: Seq[LocationTrends]): Unit =
    locationTrends.foreach { locationTrend =>
      println()
      println("Trends for: " + locationTrend.locations.map(_.name).mkString("/"))
      locationTrend.trends.take(10).foreach(t => println(t.name))
    }

    def main(args: Array[String]): Unit = {
        val consumerToken = ConsumerToken(key = "aRwE2Y5vEymyAgunyvAEIfg9j", secret = "i9VAhxIIXdToktmjyTSFaD5VyuObw4hVEIrJiD6fP5lJqxcDpF")
        val accessToken = AccessToken(key = "1452823701288669191-roauPBv90VbvZFuZZM5bYjW5vkykac", secret = "zi2qEjy7ocUvyGo6jTwg40vmRZhIgC1LfH63T8C6H9AYr")
        val restClient = TwitterRestClient(consumerToken, accessToken)
        // val result = restClient.homeTimeline(count = 10).map{ratedData =>
        //     val tweets = ratedData.data
        //     val topHashtags: Seq[((String, Int), Int)] = getTopHashtags(tweets).zipWithIndex
        //     val rankings = topHashtags.map {
        //     case ((entity, frequency), idx) => s"[${idx + 1}] $entity (found $frequency times)"
        //     }
        //     println("MY TOP HASHTAGS:")
        //     println(rankings.mkString("\n"))
        // }

        //WORKS
//         lazy val globalTrends = for {
//     globalTrendsResult <- restClient.globalTrends().map(_.data)
//   } yield printTrendingTopics(globalTrendsResult)

//        val blah = globalTrends

        displayGreeting()
        val testBaseUser = new User("John", "Smith", "johnsmith@gmail.com", "jsmith21", "Ahhh2469@", "Base")
        val testAdminUser = new User("James", "Ronn", "jamesronnh@gmail.com", "jronn23", "CXJ295!", "Admin")
        
        userDataBase += testBaseUser
        userDataBase += testAdminUser
        // getUserDetails(userDataBase(0))
        // getUserDetails(userDataBase(1))
        // setUserDetails(userDataBase(0), "Hacker", "Man", "prankedbro@gmail.com", "BigusD", "lol", "Admin")
        // setUserDetails(userDataBase(1), "Jamey", "Ron", "newperson@gmail.com", "jron25", "sdaskc32!", "Admin")
        // getUserDetails(userDataBase(0))
        // getUserDetails(userDataBase(1))
        
        var accountResult = createLogAccount()
        while (accountResult == "4"){
            displayGreeting()
            accountResult = createLogAccount()
        }
        if (accountResult == "3"){
           return
        }
        // getUserDetails(activeAccount)
        // getUserDetails(userDataBase.last)
        getAnalysis(activeAccount.getUserName())
        restClient.shutdown()

    }

    def getUserDetails(c: User):Unit = {
        println("\nPermission level: " + c.getPermissionLevel())
        println(c.getFirstName)
        println(c.getLastName)
        println(c.getEmail)
        println(c.getUserName)
        println(c.getPassWord)
    }

    def setUserDetails(user: User, newFirst: String, newLast: String, newEmail: String, 
    newUser: String, newPass: String, newPermission: String): Unit = {
        user.setFirstName(newFirst)
        user.setLastName(newLast)
        user.setEmail(newEmail)
        user.setUserName(newUser)
        user.setPassWord(newPass)
        user.setPermissionLevel(newPermission)
    }

    def displayGreeting(): Unit = {
        println("Hello! Welcome to your real-time news analyzer application.")
        println("[1] Create New Account")
        println("[2] Log In")
        println("[3] Exit")
    }

    def getUserAction(): String = {
        val userAction = readLine("Choose an action: ")
        println("\n")
        userAction
    }

    def createLogAccount(): String = {
        var userAction = getUserAction()
        userAction match {
            case "1" => createAccount()
            case "2" => userAction = logInAccount()
            case "3" => println("Thank you for your time. Come again!")
        }
        userAction
    }

    def createAccount(): Unit = {
        println("Let's get you set up! Please fill out our form accordingly.")
        val userFName = readLine("Please enter your first name: ")
        val userLName = readLine("Please enter your last name: ")
        val userEmail = readLine("Please enter your email address: ")
        val userName = readLine("Please create your user name: ")
        val userPass = readLine("Please create your password: ")

        val userAccount = new User(userFName, userLName, userEmail, userName, userPass, "Base")
        userDataBase.addOne(userAccount)
        activeAccount = userAccount
    }

    def logInAccount(): String = {
        println("Please enter your username and password.")
        val userName = readLine("Username: ")
        val passWord = readLine("Password: ")

        var loggedIn = false
        for (account <- userDataBase){
            if (account.getUserName().toUpperCase == userName.toUpperCase && account.getPassWord() == passWord){
                activeAccount = account
                loggedIn = true
            }
        }
        if (!loggedIn){
            val logResult = failedLogIn()
            logResult
        } else {
            return "2"
        }

    }

    def failedLogIn(): String = {
        println("Log in failed! Did not recognize username or password.")
        val userAction = readLine("Try again? (Yes/ No): ")
        if (userAction.toUpperCase == "YES"){
            val logResult = logInAccount()
            logResult
        }
        else {
            println("Returning to the main menu.\n")
            return "4"
        }
    }

    def getAnalysis(userName: String): Unit = {
        println("\nGreetings " + userName + ", what would you like to do?")
        println("[1]")
        println("[2]")
        println("[3]")
        println("[4]")
        println("[5]")
        println("[6]")
    }
}