package se.pulsen.lia_timereportproject.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.pulsen.lia_timereportproject.Entities.Customer;
import se.pulsen.lia_timereportproject.Entities.Project;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepo extends JpaRepository<Project, Integer> {

    List<Project> findProjectsByCustomerID(String customerID);

    Optional<Project> findProjectByProjectID(String projectID);

}
