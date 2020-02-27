import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.util.JiraHome
import org.apache.commons.lang3.StringEscapeUtils
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.customfields.option.Option

def directory = ComponentAccessor.getComponentOfType(JiraHome.class).localHomePath
def customFields = ComponentAccessor.getCustomFieldManager().getCustomFieldObjects()
def custField = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectsByName("Test PT")
def optionsManager = ComponentAccessor.optionsManager

def directory1 = directory + "/inactive-list/PT_Names.csv"

File file = new File(directory1)

String[] lines = file.text.split('\n')

def id = []
def value = []

def csvString = new StringBuilder("Option Value \n")

def newOptions_ID = []
def newOptions_Name = []

def i = 0
log.warn(lines.size())

for(i=1; i<lines.size(); i++) {
    def s1 = lines[i].toString().replace(',', '//')
    s1 = s1.split('//')
    if(s1[1].toString() != "") {
        id.add(s1[1])
        value.add(s1[2])
    }
    else {
        csvString.append(s1[2])
    }
}

log.warn(id)
log.warn(value)

def sb = new StringBuilder()

customFields.toList().eachWithIndex { item, index ->
    if(item.name == "Test PT"){
        try {
            item.getConfigurationSchemes().each { fieldConfidScheme ->
                def fc = fieldConfidScheme.getOneAndOnlyConfig()
                
                def options = optionsManager.getOptions(item.getConfigurationSchemes().listIterator().next().getOneAndOnlyConfig())
                
                options.each { option ->
                    sb << option.value << " - " << option.getOptionId() << ", "
                    
                    for(i=0; i<id.size(); i++) {
                        if(option.getOptionId().toString().equals(id[i].toString())) {
                            log.warn(option.getOptionId() + ": " + id[i] + " - " + value[i])
                            option.setValue(value[i].toString())
                            break
                        }
                    }
                }
            }
            log.warn(sb.deleteCharAt(sb.length()-2))
        }
        catch(e) {
            log.warn("Cannot find Custom Field - Test for Scripts")
        }
    }
}

// Saving new file with new options

def directory2 = directory + "/inactive-list/PT_Names_New_ToBeAdded.csv"

File file1 = new File(directory2)
file1.createNewFile()
file1.setText(csvString.toString())