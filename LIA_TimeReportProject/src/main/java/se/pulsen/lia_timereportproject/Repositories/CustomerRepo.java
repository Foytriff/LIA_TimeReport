package se.pulsen.lia_timereportproject.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.pulsen.lia_timereportproject.Entities.Customer;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Integer> {

}
