package se.pulsen.lia_timereportproject.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.pulsen.lia_timereportproject.Entities.Timereport;

@Repository
public interface TimereportRepo extends JpaRepository<Timereport, Integer> {
}