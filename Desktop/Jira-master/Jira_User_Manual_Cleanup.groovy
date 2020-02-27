import com.atlassian.jira.project.ProjectCategory
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.project.ProjectCategory
import com.atlassian.jira.bc.project.component.ProjectComponent

def projectManager = ComponentAccessor.projectManager
def projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
def user = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser()
def prjComp = ComponentAccessor.getProjectComponentManager()

def allProjects = projectManager.getProjects()

def component = []
def projects = []
def projectKeys = []
def prjs = []

allProjects.each{ a ->
    if(a.getLeadUserName().toString().equals("rdaniel")) {
        projects.add(a.name)
        projectKeys.add(a.key)
    }
}

prjs.add(projects)

def i = 0
def archived = []

for(i=0; i<projects.size(); ++i) {
    def prj = projectManager.getProjectObjByName(projects.get(i).toString())
    ProjectCategory pc = prj.getProjectCategoryObject()
    archived.add(pc.getName())
    component.add(prj.getComponents())
    if((pc.getName().toString().equals("Archived Projects")) && prj.getLeadUserName().toString().equals("rdaniel")) {
        projectManager.updateProject(prj, prj.name, prj.description, "s-jira", prj.url, prj.assigneeType)       
    }
}

log.warn(projects)
log.warn(projectKeys)
log.warn(archived)
for(i=0; i<component.size(); ++i) {
    log.warn(component.get(i))
}

for(i=0; i<prjs.size(); i++) {
    def prj = projectManager.getProjectObjByName(projects.get(i).toString())
    log.warn(prj.getLeadUserKey())
}

return (archived)