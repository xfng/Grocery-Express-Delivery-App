package group35.droneDeliverySystem.Repositories;

import group35.droneDeliverySystem.Classes.*;
// import group35.droneDeliverySystem.Classes.ItemLine;
// import group35.droneDeliverySystem.Classes.OrderID;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import group35.droneDeliverySystem.Classes.Order;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    Order findById_StoreNameAndId_orderID(String storeName, String orderID);

    boolean existsById_StoreNameAndId_orderID(String storeName, String orderID);

    int countById_StoreNameAndAssignedDroneIDAndOrdertypeID(String storeName, String assignedDroneID, String ordertype);

    List<Order> findById_StoreName(String storeName);

    List<Order> findById_StoreNameAndOrdertypeID(String storeName, String ordertypeID);

    List<Order> findByCustomerAccountAndOrdertypeID(String account, String ordertypeID);

}
