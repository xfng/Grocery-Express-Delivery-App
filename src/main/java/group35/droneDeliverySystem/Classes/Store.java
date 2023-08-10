package group35.droneDeliverySystem.Classes;

import java.util.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Store {
    @Id
    private String storeName;

    private int earnedRevenue;
    private int purchasedCount;
    private int overloads;
    private int transferCount;

    @Transient
    private TreeMap<String, Integer> storeItem;
    @Transient
    private TreeMap<String, Drone> storeDrone;
    @Transient
    private TreeMap<String, Order> storeOrder;
    @Transient
    private TreeMap<String, Order> storePurchasedOrder;
    @Transient
    private TreeMap<String, Order> storeReturningOrder;
    @Transient
    private TreeMap<String, Order> pendingPickupOrder;
    @Transient
    private TreeMap<String, Order> placedReturnOrder;

    protected Store() {
        this.storeItem = new TreeMap<String, Integer>();
        this.storeDrone = new TreeMap<String, Drone>();
        this.storeOrder = new TreeMap<String, Order>();
        this.storePurchasedOrder = new TreeMap<String, Order>();
        this.placedReturnOrder = new TreeMap<String, Order>();
        this.storeReturningOrder = new TreeMap<String, Order>();
        this.pendingPickupOrder = new TreeMap<String, Order>();
    }

    public Store(String storeName, int earnedRevenue) {
        this.storeName = storeName;
        this.earnedRevenue = earnedRevenue;
        this.storeItem = new TreeMap<String, Integer>();
        this.storeDrone = new TreeMap<String, Drone>();
        this.storeOrder = new TreeMap<String, Order>();
        this.storePurchasedOrder = new TreeMap<String, Order>();
        this.placedReturnOrder = new TreeMap<String, Order>();
        this.storeReturningOrder = new TreeMap<String, Order>();
        this.pendingPickupOrder = new TreeMap<String, Order>();
        this.purchasedCount = 0;
        this.overloads = 0;
        this.transferCount = 0;
    }

    public String getName() {
        return this.storeName;
    }

    public int getEarnedRevenue() {
        return this.earnedRevenue;
    }

    public TreeMap<String, Integer> getStoreItem() {
        return this.storeItem;
    }

    public void updateStoreItem(String itemName, Integer itemWeight) {
        this.storeItem.put(itemName, itemWeight);
    }

    public TreeMap<String, Drone> getStoreDrone() {
        return this.storeDrone;
    }

    public void updateStoreDrone(String droneID, Drone drone) {
        this.storeDrone.put(droneID, drone);
    }

    public TreeMap<String, Order> getStoreOrder() {
        return this.storeOrder;
    }

    public void updateStoreOrder(String orderID, Order Order) {
        this.storeOrder.put(orderID, Order);
    }

    public int getStoreOrderCount() {
        return this.storeOrder.size();
    }

    public TreeMap<String, Order> getPendingPickUpOrder() {
        return this.pendingPickupOrder;
    }

    public void addPendingPickUpOrder(String returnOrderID, Order returnOrder) {
        this.pendingPickupOrder.put(returnOrderID, returnOrder);
    }

    public void removePendingPickUpOrder(String pendingPickUpOrderID) {
        this.pendingPickupOrder.remove(pendingPickUpOrderID);
    }

    public void removeStoreOrder(String OrderID) {
        this.storeOrder.remove(OrderID);
    }

    public void removeReturningOrder(String returningOrderID) {
        this.storeReturningOrder.remove(returningOrderID);
    }

    public void updateEarnedRevenue(int totalCost) {
        this.earnedRevenue += totalCost;
    }

    public int getPurchasedCount() {
        return this.purchasedCount;
    }

    public void updatePurchasedCount() {
        this.purchasedCount += 1;
    }

    public int getOverloads() {
        return this.overloads;
    }

    public void updateOverloads(int dronePendingOrderCount) {
        this.overloads += dronePendingOrderCount;
    }

    public int getTransferCount() {
        return this.transferCount;
    }

    public void updateTransferCount() {
        this.transferCount += 1;
    }

    public TreeMap<String, Order> getPurchasedOrder() {
        return this.storePurchasedOrder;
    }

    public void updatePurchasedOrder(String orderID, Order Order) {
        this.storePurchasedOrder.put(orderID, Order);
    }

    public TreeMap<String, Order> getPlacedReturnOrder() {
        return this.placedReturnOrder;
    }

    public void updatePlacedReturnOrder(String placedReturnOrderID, Order placedReturnOrder) {
        this.placedReturnOrder.put(placedReturnOrderID, placedReturnOrder);
    }

    public void removePlacedReturnOrder(String placedReturnOrderID) {
        this.placedReturnOrder.remove(placedReturnOrderID);
    }

    public TreeMap<String, Order> getReturningOrder() {
        return this.storeReturningOrder;
    }

    public void updateReturningOrder(String returningOrderID, Order returningOrder) {
        this.storeReturningOrder.put(returningOrderID, returningOrder);
    }

}
