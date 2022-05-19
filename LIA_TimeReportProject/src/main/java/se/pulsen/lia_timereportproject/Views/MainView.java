package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import se.pulsen.lia_timereportproject.Services.QueryFromEverywhere;
import se.pulsen.lia_timereportproject.security.PrincipalUtils;

public class MainView extends AppLayout {

    public MainView(@Autowired QueryFromEverywhere queryFromEverywhere) {
        // Adding navbar
        HorizontalLayout navbar = new HorizontalLayout();
        VerticalLayout drawer = new VerticalLayout();
        H1 navbarTitle = new H1("Timereports");
        Button loginButton = new Button("Login", evt -> UI.getCurrent().navigate(LoginView.class));

        Button logoutButton = new Button("Logout", evt -> PrincipalUtils.logout());
        Label loggedInLabel = new Label();
        if(PrincipalUtils.isAuthenticated()){
            loggedInLabel.setText(queryFromEverywhere.getNameForLoggedInUser() + ": ");
        }


        HorizontalLayout logout = new HorizontalLayout(loggedInLabel, logoutButton);
        logout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        logout.setAlignItems(FlexComponent.Alignment.CENTER);

        SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        navbar.add(new DrawerToggle(), navbarTitle);

        navbar.add(PrincipalUtils.isAuthenticated() ? logout : loginButton);

        navbar.setWidthFull();
        navbar.setMargin(true);
        navbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);

        RouterLink employeeView = new RouterLink("Create Report", EmployeeView.class);
        RouterLink myReports = new RouterLink("My Reports", ReportsView.class);
        RouterLink statistics = new RouterLink("Statistics", StatisticsView.class);
        RouterLink adminView = new RouterLink("Manage Employees", AdminView.class);

        drawer.add(employeeView, myReports, statistics);

        if(PrincipalUtils.getRole().equals("[ROLE_ADMIN]")){
            myReports.setText("All Reports");
            drawer.add(adminView);
        }

        addToDrawer(drawer);

        addToNavbar(navbar);
    }
}
