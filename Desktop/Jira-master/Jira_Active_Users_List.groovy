import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.Users
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.security.login.LoginManager
import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.config.util.JiraHome
import com.atlassian.mail.Email
import org.apache.commons.lang3.StringEscapeUtils
import javax.mail.BodyPart
import javax.mail.Multipart
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.activation.MailcapCommandMap
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart
import javax.activation.CommandMap
import javax.mail.internet.MimeUtility
//import static java.util.Calendar.*

//defining the fields for managing users and details
UserManager userManager = ComponentAccessor.getUserManager()
LoginManager loginManager = ComponentAccessor.getComponentOfType(LoginManager.class)
UserService userService = ComponentAccessor.getComponent(UserService.class)
UserService.UpdateUserValidationResult updateUserValidationResult
def groupManager = ComponentAccessor.getGroupManager()
User newUser

// getting the mailServer and the directory to save files
def mailServer = ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer()
def directory = ComponentAccessor.getComponentOfType(JiraHome.class).localHomePath

def csvString_Updated = new StringBuilder("Username, Name, Email, LastLoginTime, Directory ID \n")

//variables for storing last login time
Date date = new Date()
def lastLoginTime
def llt
def directory_name


userManager.getAllUsers().findAll{u -> u.active == true}.each
{
    u->
    lastLoginTime = loginManager.getLoginInfo(u.getUsername()).getLastLoginTime()



    if(lastLoginTime != null )
    {
        llt = new Date()
        llt.setTime(lastLoginTime)
        llt.format("dd/MM/yyy")


            try {
            csvString_Updated.append( StringEscapeUtils.escapeCsv(u.username.toString()) + ","
                                     + StringEscapeUtils.escapeCsv(u.displayName.toString()) + ","
                                     + StringEscapeUtils.escapeCsv(u.emailAddress.toString()) + ","
                                     + StringEscapeUtils.escapeCsv(llt.format("dd/MM/yyy").toString()) + ","
                                     + StringEscapeUtils.escapeCsv(u.directoryId.toString()) + ","
                                     +  "\n")
        }

        catch(e) {
            log.warn("Unable to add to list: " + u)
        }

    }
}


def randomizer = UUID.randomUUID().toString()
def fileLocation = directory + "/" + randomizer + "Active_Users_List.csv"
log.warn("Saving file on: " + fileLocation)

//Creating the new file
File file = new File(fileLocation)
file.createNewFile()
file.append(csvString_Updated.toString())