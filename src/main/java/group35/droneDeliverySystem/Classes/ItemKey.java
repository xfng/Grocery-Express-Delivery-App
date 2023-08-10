package group35.droneDeliverySystem.Classes;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ItemKey implements Serializable {

    private String itemName;
    private String storeName;

    protected ItemKey() {
    }

    public ItemKey(String storeName, String itemName) {
        this.itemName = itemName;
        this.storeName = storeName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getStoreName() {
        return storeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ItemKey that = (ItemKey) o;
        return Objects.equals(itemName, that.itemName) && Objects.equals(storeName, that.storeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemName, storeName);
    }
}
