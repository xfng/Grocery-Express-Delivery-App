package group35.droneDeliverySystem.Classes;

import java.util.*;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDate;

@Entity
@Table(name = "`order`")
public class Order {

    @EmbeddedId
    private OrderID id;

    private String customerAccount;

    @Column(name = "assigned_drone_id")
    private String assignedDroneID;

    private LocalDate createdDate;

    @Column(name = "ordertype_id")
    private String ordertypeID;

    // @Transient
    // private TreeMap<String, ItemLine> orderLines;

    public Order() {
    }

    public Order(String storeName, String orderID, String droneID, String customerAccount, String orderTypeID) {
        this.id = new OrderID(storeName, orderID);
        this.assignedDroneID = droneID;
        this.customerAccount = customerAccount;
        // this.orderLines = new TreeMap<String, ItemLine>();
        this.createdDate = null;
        this.ordertypeID = orderTypeID;

    }

    public String getOrderID() {
        return this.id.getOrderID();
    }

    public String getStoreName() {
        return this.id.getStoreName();
    }

    public String getCustomerAccount() {
        return this.customerAccount;
    }

    public String getAssignedDroneID() {
        return this.assignedDroneID;
    }

    public void updateAssignedDroneID(String droneID) {
        this.assignedDroneID = droneID;
    }

    public void updateCreatedDate(LocalDate currentDate) {
        this.createdDate = currentDate;
    }

    public LocalDate getCreatedDate() {
        return this.createdDate;
    }

    // public TreeMap<String, ItemLine> getOrderLines() {
    // if (this.orderLines != null) {
    // return this.orderLines;
    // } else {
    // return null;
    // }

    // }

    // public void updateOrderLines(String itemName, ItemLine itemLine) {
    // this.orderLines.put(itemName, itemLine);
    // }

    // public int getOrderTotalCost() {
    // TreeMap<String, ItemLine> orderLines = getOrderLines();
    // int orderTotalCost = 0;
    // for (ItemLine line : orderLines.values()) {
    // orderTotalCost += line.getTotalCost();
    // }
    // return orderTotalCost;
    // }

    // public int getOrderTotalWeight() {
    // TreeMap<String, ItemLine> orderLines = getOrderLines();
    // int orderTotalWeight = 0;
    // for (ItemLine line : orderLines.values()) {
    // orderTotalWeight += line.getTotalWeight();
    // }
    // return orderTotalWeight;
    // }

    // public void removeReturnItems(Order o) {
    // TreeMap<String, ItemLine> originalItemLines = this.orderLines;
    // TreeMap<String, ItemLine> removeItemLines = o.orderLines;
    // for (Map.Entry<String, ItemLine> pair : removeItemLines.entrySet()) {
    // String itemID = pair.getKey();
    // ItemLine itemline = pair.getValue();
    // originalItemLines.get(itemID).updateReturnCount(itemline.getQuantity());
    // }
    // this.orderLines = originalItemLines;
    // }

    public String getOrderTypeID() {
        return this.ordertypeID;
    }

    public void updateOrderType(String orderTypeID) {
        this.ordertypeID = orderTypeID;
    }

}
