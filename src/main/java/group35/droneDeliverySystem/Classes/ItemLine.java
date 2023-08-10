package group35.droneDeliverySystem.Classes;

import jakarta.persistence.*;

@Entity
public class ItemLine {

    @EmbeddedId
    private ItemLineKey id;


    private int itemWeight;
    private int quantity;
    private int unitPrice;
    private int returnCount;

    protected ItemLine() {
    }

    public ItemLine(String orderID, String storeName, String itemName, int itemWeight, int quantity, int unitPrice) {
        this.id = new ItemLineKey(itemName, orderID, storeName);
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.returnCount = 0;
        this.itemWeight = itemWeight;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getUnitPrice() {
        return this.unitPrice;
    }

    public int getTotalCost() {
        return this.unitPrice * this.quantity;
    }

    public int getReturnCount() {
        return this.returnCount;
    }

    public int getTotalWeight() {
        return itemWeight * quantity;
    }

    public void updateReturnCount(int returnQuantity) {
        this.returnCount += returnQuantity;
    }

    public String getItemName() {
        return this.id.getItemName();
    }

}
