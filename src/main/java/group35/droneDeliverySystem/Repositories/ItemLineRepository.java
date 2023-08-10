package group35.droneDeliverySystem.Repositories;

import group35.droneDeliverySystem.Classes.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemLineRepository extends JpaRepository<ItemLine, String> {

    // ItemLine findById_ItemNameAndId_StoreNameAndId_OrderID(String itemLine,
    // String storeName, String orderID);
    ItemLine findById_ItemKey_StoreNameAndId_ItemKey_ItemNameAndId_OrderID(String itemLine, String storeName,
            String orderID);
    // boolean existsById_ItemNameAndId_StoreNameAndId_OrderID(String itemLine,
    // String storeName, String orderID);

    boolean existsById_ItemKey_StoreNameAndId_ItemKey_ItemNameAndId_OrderID(String itemLine, String storeName,
            String orderID);

    // List<ItemLine> findById_StoreNameAndId_OrderID(String storeName, String
    // orderID);
    List<ItemLine> findById_ItemKey_StoreNameAndId_OrderID(String storeName, String orderID);

    int countById_ItemKey_StoreNameAndId_OrderID(String storeName, String orderID);

    boolean existsById_ItemKey_StoreNameAndId_OrderIDAndId_ItemKey_ItemName(String storeName, String orderID,
            String itemName);
}
