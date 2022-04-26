package se.pulsen.lia_timereportproject.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import se.pulsen.lia_timereportproject.Views.LoginView;

public class PrincipalUtils {

    public static String getName(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static boolean isAuthenticated(){
        return !SecurityContextHolder.getContext().getAuthentication().getName().equalsIgnoreCase("anonymousUser");
    }

    public static void logout(){
        new SecurityContextLogoutHandler().logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
        UI.getCurrent().navigate(LoginView.class);
    }

}
