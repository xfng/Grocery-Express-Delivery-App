package group35.droneDeliverySystem.Classes;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class DroneID {
    @Column(name = "store_name")
    private String storeName;
    @Column(name = "drone_id")
    private String droneID;

    protected DroneID() {
    }

    public DroneID(String storeName, String droneID) {
        this.storeName = storeName;
        this.droneID = droneID;
    }


    public String getStoreName() {
        return storeName;
    }

    public String getDroneID() {
        return droneID;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setDroneID(String orderID) {
        this.droneID = droneID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DroneID that = (DroneID) o;
        return Objects.equals(droneID, that.droneID) && Objects.equals(storeName, that.storeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(droneID, storeName);
    }

}
