package se.pulsen.lia_timereportproject.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.pulsen.lia_timereportproject.Entities.Employee;
import se.pulsen.lia_timereportproject.Entities.Timereport;

import java.util.List;

@Repository
public interface TimereportRepo extends JpaRepository<Timereport, Integer> {
    List<Timereport> findTimereportsByEmployee(Employee employee);
}
