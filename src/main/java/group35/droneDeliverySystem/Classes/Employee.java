package group35.droneDeliverySystem.Classes;

import java.util.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "account")
public class Employee extends User {

    private String taxIdentifier;

    protected Employee() {
    }

    public Employee(String account, String firstName, String lastName, String phoneNumber, String taxIdentifier,
            String password, String roleName) {
        super(account, firstName, lastName, phoneNumber, password, roleName);
        this.taxIdentifier = taxIdentifier;
    }

    public String getTaxIdentifier() {
        return this.taxIdentifier;
    }
}
