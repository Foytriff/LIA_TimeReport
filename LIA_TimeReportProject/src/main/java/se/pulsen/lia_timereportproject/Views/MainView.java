package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLink;
import org.springframework.security.core.context.SecurityContextHolder;
import se.pulsen.lia_timereportproject.security.PrincipalUtils;

public class MainView extends AppLayout {

    public MainView() {
        // Adding navbar
        HorizontalLayout navbar = new HorizontalLayout();
        VerticalLayout drawer = new VerticalLayout();
        H1 navbarTitle = new H1("My page");
        Button loginButton = new Button("Login", evt -> UI.getCurrent().navigate(LoginView.class));

        Button logoutButton = new Button("Logout", evt -> PrincipalUtils.logout());

        SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        navbar.add(new DrawerToggle(), navbarTitle);

        navbar.add(PrincipalUtils.isAuthenticated() ? logoutButton : loginButton);

        navbar.setWidthFull();
        navbar.setMargin(true);
        navbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);

        RouterLink employeeView = new RouterLink("Create Report", EmployeeView.class);
        RouterLink myReports = new RouterLink("My Reports", ReportsView.class);
        RouterLink adminView = new RouterLink("Manage Employees", AdminView.class);

        drawer.add(employeeView, myReports);

        if(PrincipalUtils.getRole().equals("[ROLE_ADMIN]")){
            drawer.add(adminView);
        }

        addToDrawer(drawer);

        addToNavbar(navbar);
    }
}
