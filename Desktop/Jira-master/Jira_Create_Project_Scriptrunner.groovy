import com.atlassian.jira.bc.project.ProjectCreationData
import com.atlassian.jira.bc.project.ProjectService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.AssigneeTypes
import com.atlassian.jira.project.type.ProjectTypeKey
import com.atlassian.jira.project.UpdateProjectParameters

// the key for the new project
final String projectKey = "TESTING1"

// the name of the new project
final String projectName = "Testing 1"

// the description for the new project - optional
final String projectDescription = "Project for Testing"

def projectService = ComponentAccessor.getComponent(ProjectService)
def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser

// available project type keys: business | software | service_desk
def projectTypeKey = new ProjectTypeKey("software")

def creationData = new ProjectCreationData.Builder().with {
    withName(projectName)
    withKey(projectKey)
    withDescription(projectDescription)
    withLead(loggedInUser)
    withUrl(null)
    withAssigneeType(AssigneeTypes.PROJECT_LEAD)
    withType(projectTypeKey)
}.build()

final ProjectService.CreateProjectValidationResult projectValidationResult = projectService.validateCreateProject(loggedInUser, creationData)
assert projectValidationResult.isValid() : projectValidationResult.errorCollection

projectService.createProject(projectValidationResult)

def projectManager = ComponentAccessor.getProjectManager()
def new_proj = projectManager.getProjectObjByKey("TESTING1")

def category = projectManager.getProjectCategoryObjectByName('Product Team')
projectManager.setProjectCategory(new_proj, category)

def fieldsScheme = ""