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
}
