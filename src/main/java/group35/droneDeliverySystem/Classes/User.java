package group35.droneDeliverySystem.Classes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    private String account;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private String roleName;

    protected User() {
    }

    public User(String account, String firstName, String lastName, String phoneNumber, String password,
            String roleName) {
        this.account = account;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.roleName = roleName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAccount() {
        return this.account;
    }

    public String getName() {
        String fullName = this.firstName + "_" + this.lastName;
        return fullName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getRole() {
        return this.roleName;
    }
}
