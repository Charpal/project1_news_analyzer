package example

import scala.collection.mutable.ListBuffer
//scp -P 2222 "C:\Users\Charles Dang\Documents\Revature\Projects\project1_news_analyzer\newsanalyzercli\target\scala-2.11\newsanalyzercli_2.11-0.1.0-SNAPSHOT.jar" maria_dev@sandbox-hdp.hortonworks.com:/home/maria_dev
//import scala.concurrent.ExecutionContext.Implicits.global
//File I/O
import scala.io.StdIn.readLine
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io._;
//Rest API
import org.apache.http.HttpEntity
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
//
import java.sql.SQLException
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.sql.DriverManager

//import net.liftweb.json._

object Menu {

    var activeAccount = new User("N/A", "N/A", "N/A", "N/A", "N/A", "N/A")
    val testBaseUser = new User("John", "Smith", "johnsmith@gmail.com", "jsmith21", "Ahhh2469@", "Base")
    val testAdminUser = new User("James", "Ronn", "jamesronnh@gmail.com", "jronn23", "CXJ295!", "Admin")      
    var userDataBase =  ListBuffer[User]()
    userDataBase += testBaseUser
    userDataBase += testAdminUser
    

    //val path = "hdfs://sandbox-hdp.hortonworks.com:8020/user/maria_dev/"
    val path = "/tmp/"
    //replace testPath with path in the real deal
    //val testPath = "src/main/scala/example/"

    
    def main(args: Array[String]){

        try{
        while(true){
            displayGreeting()
            var accountResult = createLogAccount()
            if (accountResult == "1" || accountResult == "2"){
                var actionResult = presentOptions()
            } else if (accountResult == "3"){
                //
                return
            }
        } 
        } finally {
           //
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
        userDataBase += userAccount
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

    def presentOptions(): String = {
        var userAction = ""
        if (activeAccount.getPermissionLevel() == "Admin"){
           while(userAction != "5"){
               println("\nGreetings " + activeAccount.getUserName() + ", what would you like to do?")
               userAction = presentAdminOptions()
           } 
        }
        else if (activeAccount.getPermissionLevel() == "Base"){
            //insert while loop
            while (userAction != "4"){
                println("\nGreetings " + activeAccount.getUserName() + ", what would you like to do?")
                userAction = presentBaseOptions()
            }
        }

        //4 for returning to the main menu
        return "4"
    }

    def presentAdminOptions(): String = {
        println("[1] Load Article Data")
        println("[2] Load Top Headlines in Entertainment")
        println("[3] Update UserName")
        println("[4] Update Password")
        println("[5] LogOut")
        val userAction = getUserAction()
        userAction match{
            case "1" => {
                println("Loading data ...")
                for (articleNum <- 1 to 5){ 
                var data = getRestContent(s"https://newsapi.org/v2/everything?q=%22wheel%20of%20time%22&page=${articleNum}&sortBy=popularity&apiKey=bad3a3f348d64271b5a6734abf9cc729")
                createFile(s"TWoT${articleNum}.json", data)
                loadDataIntoTable(s"TWoT${articleNum}.json", "wottb")
                }
                println("Finished loading data.")
            }
            case "2" => {
                println("Loading Data")
                var data = getRestContent("https://newsapi.org/v2/top-headlines?country=ca&category=entertainment&apiKey=bad3a3f348d64271b5a6734abf9cc729")
                createFile("USHeadlines.json", data)
                loadDataIntoTable("USHeadlines.json", "headlinestb")
                println("Finished loading data.")
            }
            case "3" => setNewUserName()
            case "4" => setNewPassWord()
            case "5" => println("Logging out user.")
            case _ => println("Invalid option. Please try again.")
        }
        userAction
    }

    def getRestContent(url: String): String = {
        val httpClient = new DefaultHttpClient()
        val httpResponse = httpClient.execute(new HttpGet(url))
        val entity = httpResponse.getEntity()
        var content = ""
        if (entity != null){
            val inputStream = entity.getContent()
            content = scala.io.Source.fromInputStream(inputStream).getLines.mkString
            inputStream.close
        }
        httpClient.getConnectionManager().shutdown()
        return content
    }

    def createFile(fileName: String, data:String): Unit = {
        val fullFileName = path + fileName
        //val fullFileName = testPath + fileName
        val conf = new Configuration()
        val fs = FileSystem.get(conf)
        val filePath = new Path(fullFileName)
        val isExisting = fs.exists(filePath)
        if (isExisting){
            fs.delete(filePath, false)
        }

        val output = fs.create(new Path(fullFileName))
        //val writer = new PrintWriter(output)
        
        val writer = new PrintWriter(new File(fullFileName))
        try {
            writer.write(data)
        } finally{
            writer.close()
        }
    }

    def loadDataIntoTable(fileName: String, tableName: String): Unit = {
         //load data inpath 'Headlines/USHeadlines.json' into table jsontest;
        //SELECT get_json_object(str, '$.status') as status FROM jsontest;


        //Failed with exception org.apache.hadoop.security.AccessControlException: User null does not belong to hadoop

        var con: java.sql.Connection = null;
        var fullFileName = path + fileName;
        try {
            var driverName = "org.apache.hive.jdbc.HiveDriver"
            val conStr = "jdbc:hive2://sandbox-hdp.hortonworks.com:10000/default";
            con = DriverManager.getConnection(conStr, "", "");
            val stmt = con.createStatement();
 
            println(s"Loading data from ${fullFileName} into ${tableName}")
            stmt.execute("LOAD DATA LOCAL INPATH '" + fullFileName +"' INTO TABLE " + tableName);
        } catch {
            case ex => {
            println("HERE!")
            ex.printStackTrace();
            throw new Exception (s"${ex.getMessage}")
            }
        } finally{
            try {
                if (con != null){
                    con.close()
                }
            } catch {
                case ex => {
                ex.printStackTrace();
                throw new Exception (s"${ex.getMessage}")
                } 
            }
        }
    }

    def setNewUserName(): Unit = {
        println("Current username: " + activeAccount.getUserName())
        val newUserName = readLine("New username: ")
        
        for (account <- userDataBase){
            if (account.getUserName().toUpperCase == activeAccount.getUserName.toUpperCase){
                account.setUserName(newUserName)
            }
        }
    }

    def setNewPassWord(): Unit = {
        println("Current password: " + activeAccount.getPassWord())
        val newPassWord = readLine("New password: ")
        
        for (account <- userDataBase){
            if (account.getPassWord() == activeAccount.getPassWord()){
                account.setPassWord(newPassWord)
            }
        }
    }

    def presentBaseOptions(): String = {
            println("[1] See Analysis")
            println("[2] Update Username")
            println("[3] Update Password")
            println("[4] LogOut")
        var userAction = getUserAction()
        userAction match {
            case "1" => {
                println("What would you like to analyze?")
                userAction = presentAnalysisOptions()
            }
            case "2" => setNewUserName()
            case "3" => setNewPassWord()
            case "4" => println("Logging out user.")
            case "_" => println("Invalid option. Please try again.")
        }
        userAction
    }

    def presentAnalysisOptions(): String = {
            println("[1] Top Headlines")
            println("[2] Trending Wheel of Time Articles")
            println("[3] Most Common Words in Wheel of Time Articles")
            println("[4] Game of Thrones Mentions")
            println("[5]")
            println("[6]")
        val userAction = getUserAction()
        userAction match {
            case "1" => {
                println("Obtaining Data ...")
                getHeadlines()
            }
            case "2" => {
                println("Obtaining Data ...")
                getPopularArticles()
            }
            case "3" => {
                println("Obtaining Data ...")
                getMostCommonWords()
            }
            case "4" => {
                println("Obtaining Data ...")
                searchGameofThrones()
            }
            case "_" => userAction
        }
        userAction
    }

    def getHeadlines(): Unit = {
        var con: java.sql.Connection = null;
        try {
            var driverName = "org.apache.hive.jdbc.HiveDriver"
            val conStr = "jdbc:hive2://sandbox-hdp.hortonworks.com:10000/default";
            con = DriverManager.getConnection(conStr, "", "");
            val stmt = con.createStatement();

            println("\nTop Headlines")
            for (articleCount <- 0 to 9){
                var results = stmt.executeQuery("SELECT get_json_object(str, '$.articles.title[" + articleCount + "]') FROM HeadlinesTB")
                while (results.next()){
                    println(s"${articleCount + 1}: ${results.getString(1)}")
                }
            }
            println("\n")
        } catch {
            case ex => {
            ex.printStackTrace();
            throw new Exception (s"${ex.getMessage}")
            }
        } finally{
            try {
                if (con != null){
                    con.close()
                }
            } catch {
                case ex => {
                ex.printStackTrace();
                throw new Exception (s"${ex.getMessage}")
                } 
            }
        }
    }

    def getPopularArticles(): Unit = {
        var con: java.sql.Connection = null;
        try {
            var driverName = "org.apache.hive.jdbc.HiveDriver"
            val conStr = "jdbc:hive2://sandbox-hdp.hortonworks.com:10000/default";
            con = DriverManager.getConnection(conStr, "", "");
            val stmt = con.createStatement();

            println("\nPopular Articles")
            for (articleCount <- 0 to 9){
                var results = stmt.executeQuery("SELECT get_json_object(str, '$.articles.title[" + articleCount + "]') FROM WoTTB LIMIT 1")
                while (results.next()){
                    println(s"${articleCount + 1}: ${results.getString(1)}")
                }
            }
            println("\n")
        } catch {
            case ex => {
            ex.printStackTrace();
            throw new Exception (s"${ex.getMessage}")
            }
        } finally{
            try {
                if (con != null){
                    con.close()
                }
            } catch {
                case ex => {
                ex.printStackTrace();
                throw new Exception (s"${ex.getMessage}")
                } 
            }
        }
    }


    def getMostCommonWords(): Unit = {
        var con: java.sql.Connection = null;
        try {
            var driverName = "org.apache.hive.jdbc.HiveDriver"
            val conStr = "jdbc:hive2://sandbox-hdp.hortonworks.com:10000/default";
            con = DriverManager.getConnection(conStr, "", "");
            val stmt = con.createStatement();

            println("\nMost Common Words")
            val query = "SELECT WORD, COUNT(*) AS Total FROM wottB LATERAL VIEW EXPLODE(SPLIT(str, ' ')) lTable AS WORD GROUP BY WORD ORDER BY Total DESC LIMIT 50"
            val result = stmt.executeQuery(query)
            while (result.next()){
                println(result.getString(1) + " " + result.getInt(2))
            }
            println("\n")
        } catch {
            case ex => {
            ex.printStackTrace();
            throw new Exception (s"${ex.getMessage}")
            }
        } finally{
            try {
                if (con != null){
                    con.close()
                }
            } catch {
                case ex => {
                ex.printStackTrace();
                throw new Exception (s"${ex.getMessage}")
                } 
            }
        }
    }

    def searchGameofThrones(): Unit = {
        //SELECT WORD, COUNT(*) AS Total FROM wottB LATERAL VIEW EXPLODE(SPLIT(str, ' ')) lTable as word WHERE WORD ="Game" GROUP BY WORD ORDER BY Total DESC;

        var con: java.sql.Connection = null;
        try {
            var driverName = "org.apache.hive.jdbc.HiveDriver"
            val conStr = "jdbc:hive2://sandbox-hdp.hortonworks.com:10000/default";
            con = DriverManager.getConnection(conStr, "", "");
            val stmt = con.createStatement();

            println("\nGame of Thrones Mentions")
            val query = "SELECT WORD, COUNT(*) AS Total FROM wottB LATERAL VIEW EXPLODE(SPLIT(str, ' ')) lTable as word WHERE WORD ='Game' GROUP BY WORD ORDER BY Total DESC"
            val result = stmt.executeQuery(query)
            while (result.next()){
                println(result.getString(1) + " " + result.getInt(2))
            }
            println("\n")
        } catch {
            case ex => {
            ex.printStackTrace();
            throw new Exception (s"${ex.getMessage}")
            }
        } finally{
            try {
                if (con != null){
                    con.close()
                }
            } catch {
                case ex => {
                ex.printStackTrace();
                throw new Exception (s"${ex.getMessage}")
                } 
            }
        }
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