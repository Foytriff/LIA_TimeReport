package se.pulsen.lia_timereportproject.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import se.pulsen.lia_timereportproject.Entities.Project;

public interface ProjectRepo extends JpaRepository<Project, Integer> {
}
