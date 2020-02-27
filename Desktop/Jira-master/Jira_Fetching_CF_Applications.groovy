import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.util.JiraHome
import com.atlassian.mail.Email
import org.apache.commons.lang3.StringEscapeUtils

import javax.activation.CommandMap
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.activation.MailcapCommandMap
import javax.mail.BodyPart
import javax.mail.Multipart
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart
import javax.mail.internet.MimeUtility

def mailServer = ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer()
def directory = ComponentAccessor.getComponentOfType(JiraHome.class).localHomePath

def customFields = ComponentAccessor.getCustomFieldManager().getCustomFieldObjects() //Get all custom fields
def project = ComponentAccessor.getProjectManager()

//def prname = project.getProjectObjects().each { prname }

log.warn(project)

def optionsManager = ComponentAccessor.optionsManager

def sb = new StringBuilder()

//  define a string builder to store the details for each field
def csvString = new StringBuilder("ID, Application, Product ID, Product Key, Product Team Name \n")

def prList = ComponentAccessor.getProjectManager().getProjectObjects()

// Loop over each field and extract its name and ID
customFields.toList().eachWithIndex { item, index ->
    if(item.name == "Application(s)"){ //Change "" with your custom field name
        try {
            def projectId = []
            def projectKey = []
            def projectName = []
            
            projectName.add("Default")
            
            def count = 0
            item.getConfigurationSchemes().each { fieldConfidScheme ->
                def fc = fieldConfidScheme.getOneAndOnlyConfig()
                //sb << "Options for ${fieldConfidScheme.name}\n\n" //appending to stringbuilder
                count = count + 1
                //log.warn(fieldConfidScheme.associatedProjectIds)
                //log.warn(fieldConfidScheme.associatedProjectObjects)
                sb << fieldConfidScheme.associatedProjectIds << ","
                
                def id = fieldConfidScheme.associatedProjectIds.toString().replace("[", "")
                id = id.replace("]", "")
                projectId.add(id)
                //projectKey.add(fieldConfidScheme.associatedProjectObjects)
                
                def key = fieldConfidScheme.associatedProjectObjects.toString().replace("[Project: ", "")
                key = key.toString().replace("]", "")
                key = key.replace("[", "")
                projectKey.add(key)
                
                def j = 0
                for(j=0; j<prList.size(); j++){
                    def s = '[' + prList.getAt(j).toString() + ']'
                    
                    if(s.equals(fieldConfidScheme.associatedProjectObjects.toString())){
                        //log.warn(prList.getAt(j).getName())
                        projectName.add(prList.getAt(j).getName())
                        break
                    }
                }
            }
            
            def i = 0
            projectName[0] = ""
            while(i<count)
            {
                
                def options = optionsManager.getOptions(item.iterator().next().getConfigurationSchemes().getAt(i).getOneAndOnlyConfig())
                
                options.each { option ->
                    
                    if(!option.disabled && option.value != '(none)')  {
                        csvString.append(StringEscapeUtils.escapeCsv(option.optionId.toString()) + ","
                                         + StringEscapeUtils.escapeCsv(option.value) + ","
                                         + projectId[i] + ","
                                         + projectKey[i] + ","
                                         + projectName[i] + "\n")
                    }
                }
                i = i + 1                
            }
        } catch(e) {
            log.warn("Unable to find options for custom field: " + item.name)
        }
    }
}

//Adding an UUID to the name to randomize the file and avoid overwrite of existing ones
def randomizer = UUID.randomUUID().toString()
def fileLocation = directory + "/customfieldsdata" + "/" + randomizer + "_customFields.csv" //custfieldsexport - for production
log.warn("Saving file on: " + fileLocation)
//Creating the new file
File file = new File(fileLocation)
file.createNewFile()
//Set the text on the file
file.append(csvString.toString())