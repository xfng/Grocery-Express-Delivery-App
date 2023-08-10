package group35.droneDeliverySystem.Classes;

// import java.util.ArrayList;
import java.util.TreeMap;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Transient;

import java.util.Map;
import java.time.LocalDate;

@Entity
@PrimaryKeyJoinColumn(name = "account")
public class Customer extends User {
    private int rating;
    private int creditRecord;

    @Transient
    private TreeMap<String, Order> customerReturningOrder;

    protected Customer() {
    }

    public Customer(String account, String firstName, String lastName, String phoneNumber, int rating,
            int creditRecord) {
        super(account, firstName, lastName, phoneNumber, "default", "Customer");
        this.rating = rating;
        this.creditRecord = creditRecord;
        this.customerReturningOrder = new TreeMap<String, Order>();
    }

    public int getRating() {
        return this.rating;
    }

    public int getCreditRecord() {
        return this.creditRecord;
    }

    public void updateCreditRecord(int orderTotalCost) {
        this.creditRecord = this.creditRecord - orderTotalCost;
    }

    public void addReturningOrder(String orderID, Order order) {
        this.customerReturningOrder.put(orderID, order);

    }

//    public int getReturningOrderCount(int limitPeriod) {
//        int returnCount = 0;
//        for (Map.Entry<String, Order> pair : this.customerReturningOrder.entrySet()) {
//            String orderID = pair.getKey();
//            Order order = pair.getValue();
//            LocalDate returningDate = order.getCreatedDate();
//            int dayDiff = Utility.getDaysDiff(returningDate);
//            if (dayDiff <= limitPeriod) {
//                returnCount++;
//            }
//        }
//        return returnCount;
//    }

    public void getReturningOrderCount() {
    }

}

// public boolean addNewLine(Item item, int quantity){
// double itemWeight = item.getItemWeight();
// double itemPrice = item.getItemPrice();
// double newLinePrice = itemWeight * itemPrice * quantity;
//
// if (newLinePrice <= this.creditRecord){
// return true;
// } else {
// return false;
// }
//
// }
//
//
//
// public void updateCreditRecord(){
// }
//
// public double getTotalOutstandingOrderCost(){
//
// return 0.0;
// }
//
// public void updateOutstandingOrder(){
// }
//
// public void updateFinishedOrder(){
//
// }
