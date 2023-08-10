package group35.droneDeliverySystem.Classes;

import java.util.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.persistence.Table;
import jakarta.persistence.EmbeddedId;

@Entity
@Table(name = "`drone`")
public class Drone {

    @EmbeddedId
    private DroneID id;
    private int remainingTrips;
    private int liftingCapacity;
    private int remainingCapacity;
    @Column(name = "assigned_drone_pilot_id")
    private String assignedDronePilotID;

    @Transient
    private TreeSet<String> orderIDSet;

    protected Drone() {
    } // we need an empty constructor for the repository to use
      // The default constructor exists only for the sake of JPA. You do not use it
      // directly, so it is designated as protected

    public Drone(String storeName, String droneID, int liftingCapacity, int remainingTrips) {
        this.id = new DroneID(storeName, droneID);

        this.liftingCapacity = liftingCapacity;
        this.remainingCapacity = this.liftingCapacity;
        this.remainingTrips = remainingTrips;
        this.orderIDSet = new TreeSet<String>();
    }

    // getters and setters
    public String getDroneID() {
        return this.id.getDroneID();

    }

    public Integer getLiftingCapacity() {
        return this.liftingCapacity;
    }

    public void updateOrderIDSet(String orderID) {
        this.orderIDSet.add(orderID);
    }

    public void removeOrderID(String orderID) {
        this.orderIDSet.remove(orderID);
    }

     public int getNumOrders() {
     return this.orderIDSet.size();
     }

    public int getRemainingCapacity() {
        return this.remainingCapacity;
    }

    public void updateRemainingCapacity(int Weight) {
        this.remainingCapacity = this.remainingCapacity - Weight;
    }

    public void resetRemainingCapacity() {
        this.remainingCapacity = this.liftingCapacity;
    }

    public int getRemainingTrips() {
        return this.remainingTrips;
    }

    public void updateRemainingTrips(int numberOfTrips) {
        this.remainingTrips += numberOfTrips;
    }

    public String getAssignedDronePilotID() {
        if (this.assignedDronePilotID == null) {
            return null;
        } else {
            return this.assignedDronePilotID;
        }
    }

    public void updateAssignedDronePilotID(String assignedDronePilotID) {
        this.assignedDronePilotID = assignedDronePilotID;
    }


}
