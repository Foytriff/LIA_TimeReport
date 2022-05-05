package se.pulsen.lia_timereportproject.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.pulsen.lia_timereportproject.Entities.Employee;
import se.pulsen.lia_timereportproject.Entities.Timereport;

import java.util.List;

@Repository
public interface TimereportRepo extends JpaRepository<Timereport, Integer> {
    List<Timereport> findTimereportsByEmployee(Employee employee);

    @Query(value = "SELECT * FROM Timereport WHERE EmployeeID = :empID AND ActivityID IN (SELECT ActivityID FROM Activity WHERE ProjectID IN \n" +
            "    (SELECT ProjectID FROM Project WHERE CustomerID IN \n" +
            "        (SELECT CustomerID FROM Customer WHERE CustomerID = :custID)))",
            nativeQuery = true)
    List<Timereport> getTimeSpentOnCustomer(@Param("custID") String customerID, @Param("empID") String employeeID);
}
