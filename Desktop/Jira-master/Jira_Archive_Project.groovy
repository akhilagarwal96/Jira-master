/* Archiving a project consists in 3 steps:

add suffix [Archived] on project display name
set category to Archived Projects
set permissions scheme to Archived (Not Hidden) Projects */

/* Script:
    1. Search for project using key
    2. Update Name (Add "[Archived]" to name)
    3. Set Project Category
    4. Removes Permission Schemes
    5. Adds "Archived (NOT Hidden) Projects" Permissions Scheme */

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.UpdateProjectParameters

// Get the Project Manager
def projectManager = ComponentAccessor.getProjectManager()
def archive = projectManager.getProjectCategoryObjectByName('Archived Projects')
def permission_scheme = "Archived (NOT Hidden) Projects"

// Iterate over each Project Object
def project = projectManager.getProjectObjects().each { project ->
    
    project.each {
        if (project.key.toString().equals('TRIAL')) {  //Input Project ID
            log.warn(project.name)

            //Project Name
            def new_name = project.name.toString() + " [Archived]"  //Adding suffix
            def updateProjectParams = UpdateProjectParameters.forProject(project.id).name(new_name)
            ComponentAccessor.projectManager.updateProject(updateProjectParams)
            
            //Project Category
            projectManager.setProjectCategory(project, archive)

            //Permission Scheme
            ComponentAccessor.getPermissionSchemeManager().removeSchemesFromProject(project)
            ComponentAccessor.getPermissionSchemeManager().addSchemeToProject(project, 
                                                                              ComponentAccessor.getPermissionSchemeManager().getSchemeObject(permission_scheme))
        }
    }
}