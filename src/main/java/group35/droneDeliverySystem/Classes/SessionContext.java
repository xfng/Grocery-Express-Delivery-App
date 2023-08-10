package group35.droneDeliverySystem.Classes;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SessionContext {

    private String loggedInUser;
    private String loggedInRole;

    public void login(String account) {
        this.loggedInUser = account;
    }

    public void setLoggedInRole(String role) {
        this.loggedInRole = role;
    }

    public void logout() {
        this.loggedInUser = null;
        this.loggedInRole = null;
    }

    public boolean isLoggedIn() {
        return this.loggedInUser != null;
    }

    public String getLoggedInUser() {
        return this.loggedInUser;
    }

    public String getLoggedInRole() {
        return this.loggedInRole;
    }
}
