package se.pulsen.lia_timereportproject.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.pulsen.lia_timereportproject.Entities.Activity;
import se.pulsen.lia_timereportproject.Entities.Project;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepo extends JpaRepository<Activity, Integer> {
    List<Activity> findActivitiesByProject(Project project);
    Optional<Activity> findActivityByActivityID(String activityID);
}
