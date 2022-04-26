package se.pulsen.lia_timereportproject.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.pulsen.lia_timereportproject.Entities.Customer;
import se.pulsen.lia_timereportproject.Repositories.CustomerRepo;

import java.util.List;


@Service
public class CustomerService {

    @Autowired
    CustomerRepo customerRepo;

    public List<Customer> findAll(){
        return customerRepo.findAll();
    }

    public String customerIDFromName(String custName){
        Customer customer = customerRepo.findCustomerByCustomerName(custName).orElseThrow();
        return  customer.getCustomerID();
    }

}
