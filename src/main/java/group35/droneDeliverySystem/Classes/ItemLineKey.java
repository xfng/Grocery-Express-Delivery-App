package group35.droneDeliverySystem.Classes;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class ItemLineKey implements Serializable {

    @Embedded
    private ItemKey itemKey;


    @Column(name = "order_id")
    private String orderID;

    protected ItemLineKey() {
    }

    public ItemLineKey(String itemName, String orderID, String storeName) {
        this.itemKey = new ItemKey(storeName, itemName);
        this.orderID = orderID;
    }

    public String getItemName() {
        return itemKey.getItemName();
    }

    public String getOrderID() {
        return orderID;
    }

    public String getStoreName() {
        return itemKey.getStoreName();
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ItemLineKey that = (ItemLineKey) o;
        return Objects.equals(itemKey, that.itemKey) && Objects.equals(orderID, that.orderID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemKey, orderID);
    }

}
