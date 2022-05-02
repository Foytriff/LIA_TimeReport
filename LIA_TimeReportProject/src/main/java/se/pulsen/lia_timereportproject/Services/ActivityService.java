package se.pulsen.lia_timereportproject.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.pulsen.lia_timereportproject.Entities.Activity;
import se.pulsen.lia_timereportproject.Entities.Project;
import se.pulsen.lia_timereportproject.Repositories.ActivityRepo;

import java.util.List;

@Service
public class ActivityService {

    @Autowired
    ActivityRepo activityRepo;

    public List<Activity> findAll(){
        return activityRepo.findAll();
    }

    public List<Activity> findActivitiesForProject(Project project){
        return activityRepo.findActivitiesByProject(project);
    }

    public Activity getActivityFromID(String activityID){
        return activityRepo.findActivityByActivityID(activityID).orElseThrow();
    }
}
