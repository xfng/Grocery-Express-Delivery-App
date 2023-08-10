package group35.droneDeliverySystem.Classes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "account")
public class Pilot extends Employee {

    @Column(name = "license_id")
    private String licenseID;
    private int deliveryHistory;

    @Column(name = "assigned_drone_id")
    private String assignedDroneID;

    protected Pilot() {
    }

    public Pilot(String account, String firstName, String lastName, String phoneNumber, String taxIdentifier,
            String password, String roleName, String licenseID, int deliveryHistory) {
        super(account, firstName, lastName, phoneNumber, taxIdentifier, password, roleName);
        this.licenseID = licenseID;
        this.deliveryHistory = deliveryHistory;
    }
    //
    // public void updateDeliveryHistory(){
    // this.deliveryHistory = this.deliveryHistory - 1;
    //
    // }

    public String getLicenseID() {
        return this.licenseID;
    }

    public Integer getDeliveryHistory() {
        return this.deliveryHistory;
    }

    public void updateDeliveryHistory() {
        this.deliveryHistory += 1;
    }

    public String getAssignedDroneID() {
        if (this.assignedDroneID == null) {
            return null;
        } else {
            return this.assignedDroneID;
        }

    }

    public void updateAssignedDroneID(String droneID) {
        this.assignedDroneID = droneID;
    }

}
