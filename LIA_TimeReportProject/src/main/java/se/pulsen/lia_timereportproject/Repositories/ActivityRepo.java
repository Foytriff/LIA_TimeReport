package se.pulsen.lia_timereportproject.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.pulsen.lia_timereportproject.Entities.Activity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepo extends JpaRepository<Activity, Integer> {

    List<Activity> findActivitiesByactivityName(String name);

    List<Activity> findActivitiesByProjectID(String projecID);

    Optional<Activity> findActivityByActivityName(String name);
}
