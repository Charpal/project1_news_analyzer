package example

import scala.io.StdIn.readLine
import scala.collection.mutable.ListBuffer
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken}
import com.danielasfregola.twitter4s.entities.{HashTag, Tweet}
import com.danielasfregola.twitter4s.entities.LocationTrends
import scala.concurrent.ExecutionContext.Implicits.global
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io.PrintWriter;

object Menu {

    var activeAccount = new User("N/A", "N/A", "N/A", "N/A", "N/A", "N/A")
    var userDataBase = new ListBuffer[User]()
    val testBaseUser = new User("John", "Smith", "johnsmith@gmail.com", "jsmith21", "Ahhh2469@", "Base")
    val testAdminUser = new User("James", "Ronn", "jamesronnh@gmail.com", "jronn23", "CXJ295!", "Admin")      
    userDataBase += testBaseUser
    userDataBase += testAdminUser
    
    val consumerToken = ConsumerToken(key = "aRwE2Y5vEymyAgunyvAEIfg9j", secret = "i9VAhxIIXdToktmjyTSFaD5VyuObw4hVEIrJiD6fP5lJqxcDpF")
    val accessToken = AccessToken(key = "1452823701288669191-roauPBv90VbvZFuZZM5bYjW5vkykac", secret = "zi2qEjy7ocUvyGo6jTwg40vmRZhIgC1LfH63T8C6H9AYr")
    val restClient = TwitterRestClient(consumerToken, accessToken)

    val path = "hdfs://sandbox-hdp.hortonworks.com:8020/user/maria_dev/"
    //replace testPath with path in the real deal
    val testPath = "src/main/scala/example/"

    def getTopHashtags(tweets: Seq[Tweet], n: Int = 10): Seq[(String, Int)] = {
    val hashtags: Seq[Seq[HashTag]] = tweets.map { tweet =>
      tweet.entities.map(_.hashtags).getOrElse(List.empty)
    }
    val hashtagTexts: Seq[String]            = hashtags.flatten.map(_.text.toLowerCase)
    val hashtagFrequencies: Map[String, Int] = hashtagTexts.groupBy(identity).map { case (k, v) => k -> v.size }
    hashtagFrequencies.toSeq.sortBy { case (_, frequency) => -frequency }.take(n)
  }

  def printUserTopHashtags(userName: String, restClient: TwitterRestClient): Unit ={
           val user = userName
        val result = restClient.userTimelineForUser(screen_name = user, count = 200).map{ratedData =>
            val tweets = ratedData.data
            val topHashtags: Seq[((String, Int), Int)] = getTopHashtags(tweets).zipWithIndex
            val rankings = topHashtags.map {
            case ((entity, frequency), idx) => s"[${idx + 1}] $entity (found $frequency times)"
            }
            println(s"$user TOP HASHTAGS:")
            println(rankings.mkString("\n"))
        }
  }    

def getGlobalTrendingTopics(restClient: TwitterRestClient): Unit = {
    //val filename = testPath + "globalTrends.txt"
    val filename = path + "globalTrends.txt"
    println(s"Creating file $filename ...")
    
    val conf = new Configuration()
    val fs = FileSystem.get(conf)
    
    // Check if file exists. If yes, delete it.
    println("Checking if it already exists...")
    val filepath = new Path( filename)
    val isExisting = fs.exists(filepath)
    if(isExisting) {
      println("Yes it does exist. Deleting it...")
      fs.delete(filepath, false)
    }

    val output = fs.create(new Path(filename))
    
    val writer = new PrintWriter(output)
    
   
    val globalTrends = for {
    globalTrendsResult <- restClient.globalTrends().map(_.data)
  } yield {
    writeTrendingTopics(globalTrendsResult, writer)
  }
    writer.close()
     println(s"Done creating file $filename ...")
}

def writeTrendingTopics(locationTrends: Seq[LocationTrends], writer: PrintWriter): Unit = {
    var rank = 1
    locationTrends.foreach { locationTrend =>
      locationTrend.trends.take(100).foreach(t => writer.write(s"$rank, $t.name\n"))
    }
}

def printTrendingTopics(locationTrends: Seq[LocationTrends]): Unit =
    locationTrends.foreach { locationTrend =>
      println()
      println("Trends for: " + locationTrend.locations.map(_.name).mkString("/"))
      locationTrend.trends.take(1000).foreach(t => println(t.name))
    }

    def main(args: Array[String]): Unit = {
        
        
        // val user = "CriticalRole"
        // val result = restClient.userTimelineForUser(screen_name = user, count = 200).map{ratedData =>
        //     val tweets = ratedData.data
        //     val topHashtags: Seq[((String, Int), Int)] = getTopHashtags(tweets).zipWithIndex
        //     val rankings = topHashtags.map {
        //     case ((entity, frequency), idx) => s"[${idx + 1}] $entity (found $frequency times)"
        //     }
        //     println("CRIT ROLE TOP HASHTAGS:")
        //     println(rankings.mkString("\n"))
        // }
        

        // val result = restClient.followersForUser("CriticalRole").map{ ratedData =>
        //     val followers = ratedData.data
        //     val follower = followers.users 
        //     for (person <- follower){
        //         println("Name: " + person.screen_name)
        //         printUserTopHashtags(person.screen_name, restClient)
        //     }
        // }
        
        //printUserTopHashtags("TheWheelOfTime", restClient)

        //WORKS

        //   lazy val usTrends = for {
        //     locations <- restClient.locationTrends.map(_.data)
        //     usWoeid = locations.find(_.name == "United States").map(_.woeid)
        //     if usWoeid.isDefined
        //     usTrendsResult <- restClient.trends(usWoeid.get).map(_.data)
        // } yield printTrendingTopics(usTrendsResult)

//        val blahblah = usTrends
        try{
        while(true){
            displayGreeting()
            var accountResult = createLogAccount()
            if (accountResult == "1" || accountResult == "2"){
                var actionResult = presentOptions(activeAccount.getUserName())
            } else if (accountResult == "3"){
                restClient.shutdown()
                return
            }
        } 
        } finally {
            restClient.shutdown()
        }

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
            case _ => println("Not a valid option, returning to the main menu.")
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

    def presentOptions(userName: String): String = {
        var userAction = ""
        if (activeAccount.getPermissionLevel() == "Admin"){
           while(userAction != "2"){
               println("\nGreetings " + userName + ", what would you like to do?")
               userAction = presentAdminOptions()
           } 
        }
        else if (activeAccount.getPermissionLevel() == "Base"){
            println("\nGreetings " + userName + ", what would you like to do?")
            println("[1]")
            println("[2]")
            println("[3]")
            println("[4]")
            println("[5]")
            println("[6]")
            userAction = getUserAction()
        }

        //4 for returning to the main menu
        return "4"
    }

    def presentAdminOptions(): String = {
        println("[1] Load Twitter Data")
        println("[2] LogOut")
        val userAction = getUserAction()
        userAction match{
            case "1" => {
                println("Loading data ...")
                getGlobalTrendingTopics(restClient)
            }
            case "2" => println("Logging out user.")
            case _ => println("Invalid option. Please try again.")
        }
        userAction
    }

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

}