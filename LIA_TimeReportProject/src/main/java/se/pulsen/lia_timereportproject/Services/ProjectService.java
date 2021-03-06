package se.pulsen.lia_timereportproject.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.pulsen.lia_timereportproject.Entities.Project;
import se.pulsen.lia_timereportproject.Repositories.ProjectRepo;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    ProjectRepo projectRepo;

    public List<Project> findAll(){
        return projectRepo.findAll();
    }

    public List<Project> projectsForCustomer(String customerID){
        return projectRepo.findProjectsByCustomerID(customerID);
    }

    public Project getProjectFromID(String projectID){
        return projectRepo.findProjectByProjectID(projectID).orElseThrow();
    }

    public String getProjectNameFromProjectID(String projectID){
        Project project = projectRepo.findProjectByProjectID(projectID).orElseThrow();
        return project.getProjectName();
    }
}
