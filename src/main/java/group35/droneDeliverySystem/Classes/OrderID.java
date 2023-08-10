package group35.droneDeliverySystem.Classes;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class OrderID implements Serializable {
    @Column(name = "store_name")
    private String storeName;
    
    @Column(name = "order_id")
    private String orderID;

    protected OrderID() {
    }

    public OrderID(String storeName, String orderID) {
        this.storeName = storeName;
        this.orderID = orderID;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
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
        OrderID that = (OrderID) o;
        return Objects.equals(orderID, that.orderID) && Objects.equals(storeName, that.storeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderID, storeName);
    }
}