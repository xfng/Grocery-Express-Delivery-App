package group35.droneDeliverySystem.Repositories;

import group35.droneDeliverySystem.Classes.ItemLine;
import org.springframework.data.jpa.repository.JpaRepository;
import group35.droneDeliverySystem.Classes.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, String> {

    boolean existsById_StoreNameAndId_ItemName(String storeName, String itemName);

    Item findById_StoreNameAndId_ItemName(String storeName, String itemName);

    List<Item> findById_StoreName(String storeName);


}
