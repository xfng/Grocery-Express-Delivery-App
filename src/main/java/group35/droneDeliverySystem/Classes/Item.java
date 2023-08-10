package group35.droneDeliverySystem.Classes;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
public class Item {

    @EmbeddedId
    private ItemKey id;

    private int itemWeight;

    protected Item() {
    }

    public Item(String storeName, String itemName, int itemWeight) {
        this.id = new ItemKey(storeName, itemName);
        this.itemWeight = itemWeight;
    }

    public String getItemName() {
        return this.id.getItemName();
    }

    public int getItemWeight() {
        return this.itemWeight;
    }

}
