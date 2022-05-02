package se.pulsen.lia_timereportproject.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import se.pulsen.lia_timereportproject.Entities.Employee;
import se.pulsen.lia_timereportproject.Services.EmployeeService;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    EmployeeService employeeService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeService.findEmployeeByUsername(username);

        System.out.println(employee.getUsername());
        if(setAdmin(employee.getUsername())){
            return new User(employee.getUsername(), employee.getPass(), List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        }

        return new User(employee.getUsername(), employee.getPass(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private boolean setAdmin(String username){
        switch (username){
            case "spooderman12":
                return true;
            default:
                return false;
        }
    }
}
