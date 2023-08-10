package group35.droneDeliverySystem;

import group35.droneDeliverySystem.Classes.*;
import group35.droneDeliverySystem.Repositories.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class Commands implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Commands.class);
    private final StoreRepository storeRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PilotRepository pilotRepository;
    private final DroneRepository droneRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final ItemLineRepository itemLineRepository;
    private final EmployeeRepository employeeRepository;
    private final AdminSettingRepository adminSettingRepository;
    private final SessionContext sessionContext;

    @Autowired
    public Commands(StoreRepository storeRepository, CustomerRepository customerRepository,
            UserRepository userRepository, DroneRepository droneRepository,
            OrderRepository orderRepository, ItemRepository itemRepository,
            ItemLineRepository itemLineRepository, PilotRepository pilotRepository,
            EmployeeRepository employeeRepository, AdminSettingRepository adminSettingRepository,
            SessionContext sessionContext) {

        this.storeRepository = storeRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.pilotRepository = pilotRepository;
        this.droneRepository = droneRepository;
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.itemLineRepository = itemLineRepository;
        this.employeeRepository = employeeRepository;
        this.adminSettingRepository = adminSettingRepository;
        this.sessionContext = sessionContext;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println("> " + command);
            if (command.startsWith("//")) {
                continue;
            } else if (command.equals("stop")) {
                System.out.println("stop acknowledged");
                break;
            } else if (command.startsWith("login")) {
                login(command);
            } else if (command.equals("logout")) {
                logout();
            } else {
                if (this.sessionContext.isLoggedIn()) {
                    if (this.sessionContext.getLoggedInRole().equals("Customer")) {
                        customerCommandReader(command);

                    } else if (this.sessionContext.getLoggedInRole().equals("StoreManager")) {
                        storeManagerCommandReader(command);
                    } else if (this.sessionContext.getLoggedInRole().equals("admin")) {
                        commandReader(command);
                    } else {
                        System.out.println("ERROR:unauthorized, you can't access this commands");
                    }
                } else {
                    System.out.println("ERROR:unauthenticated, please login first");
                }
            }
        }
        System.out.println("simulation terminated");

    }

    private void login(String command) {
        String[] params = command.split(",");
        String account = params[1];
        String password = params[2];

        if (this.userRepository.existsByAccountAndPassword(account, password)) {
            User user = this.userRepository.findByAccountAndPassword(account, password);
            this.sessionContext.login(user.getAccount());
            this.sessionContext.setLoggedInRole(user.getRole());
            System.out.println("OK: " + user.getRole() + " " + user.getAccount() + " login successful");
        } else if (this.adminSettingRepository.existsBySystemIDAndPassword(account, password)) {
            this.sessionContext.login(account);
            this.sessionContext.setLoggedInRole("admin");
            System.out.println("OK: admin login successful");
        } else {
            System.out.println("ERROR:invalid_credentials");
        }
    }

    private void logout() {
        this.sessionContext.logout();
        System.out.println("OK: logout successful");
    }

    private void commandReader(String command) {

        String[] params = command.split(",");

        if (params[0].equals("make_store")) {
            try {
                String storeName = params[1];
                int earnedRevenue = Integer.valueOf(params[2]);

                if (!storeRepository.existsByStoreName(storeName)) {
                    Store store = new Store(storeName, earnedRevenue);
                    this.storeRepository.save(store);
                    System.out.println("OK:change_completed");
                } else {
                    System.out.println("ERROR:store_identifier_already_exists");
                }

            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("display_stores")) {
            try {
                List<Store> stores = this.storeRepository.findAll();
                stores.sort(Comparator.comparing(Store::getName));
                for (Store store : stores) {
                    String storeName = store.getName();
                    String earnedRevenue = String.valueOf(store.getEarnedRevenue());
                    System.out.println("name:" + storeName + "," + "revenue:" + earnedRevenue);
                }
                System.out.println("OK:display_completed");
            } catch (Exception e) {
                return;
            }

        } else if (params[0].equals("sell_item")) {
            try {
                String storeName = params[1];
                String itemName = params[2];
                int itemWeight = Integer.valueOf(params[3]);

                if (this.storeRepository.existsByStoreName(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);
                    TreeMap<String, Integer> storeItem = store.getStoreItem();
                    if (storeItem == null || !storeItem.containsKey(itemName)) {
                        store.updateStoreItem(itemName, itemWeight);

                        if (!this.itemRepository.existsById_StoreNameAndId_ItemName(storeName, itemName)) {
                            Item item = new Item(storeName, itemName, itemWeight);
                            this.itemRepository.save(item);
                        }
                        System.out.println("OK:change_completed");
                    } else {
                        System.out.println("ERROR:item_identifier_already_exists");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("display_items")) {
            try {
                String storeName = params[1];
                if (this.storeRepository.existsByStoreName(storeName)) {
                    List<Item> items = this.itemRepository.findById_StoreName(storeName);
                    items.sort(Comparator.comparing(Item::getItemName));
                    for (Item item : items) {
                        String itemName = item.getItemName();
                        String itemWeight = String.valueOf(item.getItemWeight());
                        System.out.println(itemName + "," + itemWeight);
                    }
                    System.out.println("OK:display_completed");
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                return;
            }

        } else if (params[0].equals("make_pilot")) {
            try {
                String account = params[1];
                String firstName = params[2];
                String lastName = params[3];
                String phoneNumber = params[4];
                String taxID = params[5];
                String licenseID = params[6];
                int experienceLevel = Integer.valueOf(params[7]);

                if (!this.pilotRepository.existsByAccount(account)) {
                    if (!this.pilotRepository.existsByLicenseID(licenseID)) {
                        Pilot pilot = new Pilot(account, firstName, lastName, phoneNumber, taxID, "pilot", "Pilot",
                                licenseID,
                                experienceLevel);
                        this.pilotRepository.save(pilot);

                        System.out.println("OK:change_completed");
                    } else {
                        System.out.println("ERROR:pilot_license_already_exists");
                    }
                } else {
                    System.out.println("ERROR:pilot_identifier_already_exists");
                }

            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("display_pilots")) {
            try {
                List<Pilot> pilots = this.pilotRepository.findAll();
                pilots.sort(Comparator.comparing(Pilot::getAccount));
                for (Pilot pilot : pilots) {
                    String fullName = pilot.getName();
                    String phoneNumber = pilot.getPhoneNumber();
                    String taxID = pilot.getTaxIdentifier();
                    String licenseID = pilot.getLicenseID();
                    String experienceLevel = String.valueOf(pilot.getDeliveryHistory());

                    System.out.println("name:" + fullName + "," + "phone:" + phoneNumber + "," + "taxID:" + taxID + ","
                            + "licenseID:" + licenseID + "," + "experience:" + experienceLevel);
                }
                System.out.println("OK:display_completed");
            } catch (Exception e) {
                return;
            }

        } else if (params[0].equals("make_drone")) {
            try {
                String storeName = params[1];
                String droneID = params[2];
                int liftingCapacity = Integer.valueOf(params[3]);
                int refueling = Integer.valueOf((params[4]));

                if (this.storeRepository.existsByStoreName(storeName)) {
                    if (!droneRepository.existsById_StoreNameAndId_DroneID(storeName, droneID)) {
                        Drone drone = new Drone(storeName, droneID, liftingCapacity, refueling);
                        this.droneRepository.save(drone);
                        System.out.println("OK:change_completed");
                    } else {
                        System.out.println("ERROR:drone_identifier_already_exists");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("display_drones")) {
            try {
                String storeName = params[1];
                if (this.storeRepository.existsByStoreName(storeName)) {

                    List<Drone> drones = this.droneRepository.findById_StoreName(storeName);
                    drones.sort(Comparator.comparing(Drone::getDroneID));
                    for (Drone drone : drones) {
                        String droneID = drone.getDroneID();
                        String liftingCapacity = String.valueOf(drone.getLiftingCapacity());
                        int numberOfOrders = orderRepository
                                .countById_StoreNameAndAssignedDroneIDAndOrdertypeID(storeName, droneID, "1");
                        String remainingCapacity = String.valueOf(drone.getRemainingCapacity());
                        String remainingTrips = String.valueOf(drone.getRemainingTrips());
                        String pilotAccount = drone.getAssignedDronePilotID();
                        if (pilotAccount != null) {
                            Pilot pilot = this.pilotRepository.findByAccount(pilotAccount);
                            String flownBy = pilot.getName();
                            System.out.println("droneID:" + droneID + "," + "total_cap:" + liftingCapacity + ","
                                    + "num_orders:" + numberOfOrders + "," + "remaining_cap:" + remainingCapacity + ","
                                    + "trips_left:" + remainingTrips + "," + "flown_by:" + flownBy);
                        } else {
                            System.out.println("droneID:" + droneID + "," + "total_cap:" + liftingCapacity + ","
                                    + "num_orders:" + numberOfOrders + "," + "remaining_cap:" + remainingCapacity + ","
                                    + "trips_left:" + remainingTrips);
                        }
                    }
                    System.out.println("OK:display_completed");
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("fly_drone")) {

            try {
                String storeName = params[1];
                String newDroneID = params[2];
                String pilotAccount = params[3];
                if (this.pilotRepository.existsByAccount(pilotAccount)) {
                    Pilot pilot = this.pilotRepository.findByAccount(pilotAccount);
                    if (this.storeRepository.existsByStoreName(storeName)) {
                        if (this.droneRepository.existsById_StoreNameAndId_DroneID(storeName, newDroneID)) {
                            Drone newDrone = this.droneRepository.findById_StoreNameAndId_DroneID(storeName,
                                    newDroneID);
                            String oldAssignedPilotID = newDrone.getAssignedDronePilotID();
                            if (oldAssignedPilotID != null) {
                                Pilot oldPilot = this.pilotRepository.findByAccount(oldAssignedPilotID);
                                oldPilot.updateAssignedDroneID(null);
                                this.pilotRepository.save(oldPilot);
                            }
                            newDrone.updateAssignedDronePilotID(pilotAccount);
                            System.out.print(newDrone.getAssignedDronePilotID());
                            this.droneRepository.save(newDrone);
                            String oldDroneID = pilot.getAssignedDroneID();
                            if (oldDroneID != null) {
                                Drone oldDrone = this.droneRepository.findById_StoreNameAndId_DroneID(storeName,
                                        oldDroneID);
                                oldDrone.updateAssignedDronePilotID(null);
                                this.droneRepository.save(oldDrone);
                            }
                            pilot.updateAssignedDroneID(newDroneID);
                            this.pilotRepository.save(pilot);
                            System.out.println("OK:change_completed");
                        } else {
                            System.out.println("ERROR:drone_identifier_does_not_exist");
                        }
                    } else {
                        System.out.println("ERROR:store_identifier_does_not_exist");
                    }
                } else {
                    System.out.println("ERROR:pilot_identifier_does_not_exist");
                }
            } catch (Exception e) {
                return;
            }

        } else if (params[0].equals("make_customer")) {
            try {
                String account = params[1];
                String firstName = params[2];
                String lastName = params[3];
                String phoneNumber = params[4];
                int rating = Integer.valueOf(params[5]);
                int credits = Integer.valueOf(params[6]);

                if (!this.customerRepository.existsByAccount(account)) {
                    Customer customer = new Customer(account, firstName, lastName, phoneNumber, rating, credits);
                    this.customerRepository.save(customer);
                    System.out.println("OK:change_completed");
                } else {
                    System.out.println("ERROR:customer_identifier_already_exists");
                }

            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("display_customers")) {
            try {
                List<Customer> customers = this.customerRepository.findAll();
                customers.sort(Comparator.comparing(Customer::getAccount));
                for (Customer customer : customers) {
                    String customerName = customer.getName();
                    String phoneNumber = customer.getPhoneNumber();
                    String rating = String.valueOf(customer.getRating());
                    String credit = String.valueOf(customer.getCreditRecord());
                    System.out.println("name:" + customerName + "," + "phone:" + phoneNumber + "," + "rating:" + rating
                            + "," + "credit:" + credit);
                }
                System.out.println("OK:display_completed");
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("start_order")) {
            try {
                String storeName = params[1];
                String orderID = params[2];
                String droneID = params[3];
                String customerAccount = params[4];

                if (this.storeRepository.existsByStoreName(storeName)) {
                    if (!this.orderRepository.existsById_StoreNameAndId_orderID(storeName, orderID)) {
                        if (this.droneRepository.existsById_StoreNameAndId_DroneID(storeName, droneID)) {
                            if (this.customerRepository.existsByAccount(customerAccount)) {
                                Order order = new Order(storeName, orderID, droneID, customerAccount, "1");
                                this.orderRepository.save(order);
                                System.out.println("OK:change_completed");
                            } else {
                                System.out.println("ERROR:customer_identifier_does_not_exist");
                            }

                        } else {
                            System.out.println("ERROR:drone_identifier_does_not_exist");
                        }

                    } else {
                        System.out.println("ERROR:order_identifier_already_exists");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }

            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("display_orders")) {
            try {
                String storeName = params[1];
                if (this.storeRepository.existsByStoreName(storeName)) {
                    List<Order> orders = this.orderRepository.findById_StoreName(storeName);
                    orders.sort(Comparator.comparing(Order::getOrderID));
                    for (Order order : orders) {
                        int numberOfOrderItemLines = this.itemLineRepository.countById_ItemKey_StoreNameAndId_OrderID(
                                storeName,
                                order.getOrderID());
                        if (numberOfOrderItemLines > 0) {
                            List<ItemLine> itemLines = this.itemLineRepository.findById_ItemKey_StoreNameAndId_OrderID(
                                    storeName,
                                    order.getOrderID());
                            System.out.println("orderID:" + order.getOrderID());
                            for (ItemLine itemLine : itemLines) {
                                String itemName = itemLine.getItemName();
                                int quantity = itemLine.getQuantity();
                                int totalCost = itemLine.getTotalCost();
                                int totalWeight = itemLine.getTotalWeight();
                                System.out.println("item_name:" + itemName + "," + "total_quantity:" + quantity + ","
                                        + "total_cost:" + totalCost + "," + "total_weight:" + totalWeight);
                            }
                        } else {
                            System.out.println("orderID:" + order.getOrderID());
                        }
                    }
                    System.out.println("OK:display_completed");
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("request_item")) {
            try {
                String storeName = params[1];
                String orderID = params[2];
                String itemName = params[3];
                int quantity = Integer.valueOf(params[4]);
                int itemPrice = Integer.valueOf(params[5]);
                int linePrice = quantity * itemPrice;

                if (this.storeRepository.existsByStoreName(storeName)) {
                    if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, orderID)) {
                        Order order = this.orderRepository.findById_StoreNameAndId_orderID(storeName, orderID);
                        if (this.itemRepository.existsById_StoreNameAndId_ItemName(storeName, itemName)) {
                            String customerAccount = order.getCustomerAccount();
                            Customer customer = this.customerRepository.findByAccount(customerAccount);
                            String assignedDroneID = order.getAssignedDroneID();
                            Drone assignedDrone = this.droneRepository.findById_StoreNameAndId_DroneID(storeName,
                                    assignedDroneID);
                            Item storeItem = this.itemRepository.findById_StoreNameAndId_ItemName(storeName, itemName);
                            int remainingCapacity = assignedDrone.getRemainingCapacity();
                            int itemWeight = storeItem.getItemWeight();
                            int lineWeight = quantity * itemWeight;
                            int credit = customer.getCreditRecord();
                            if (!this.itemLineRepository
                                    .existsById_ItemKey_StoreNameAndId_OrderIDAndId_ItemKey_ItemName(storeName, orderID,
                                            itemName)) {
                                List<ItemLine> orderLines = this.itemLineRepository
                                        .findById_ItemKey_StoreNameAndId_OrderID(storeName, orderID);
                                int currentOrderCost = 0;
                                for (ItemLine line : orderLines) {
                                    currentOrderCost += line.getTotalCost();
                                }
                                if (credit - currentOrderCost >= 0) {
                                    if (remainingCapacity - lineWeight >= 0) {
                                        ItemLine itemLine = new ItemLine(orderID, storeName, itemName, itemWeight,
                                                quantity, itemPrice);
                                        this.itemLineRepository.save(itemLine);
                                        assignedDrone.updateRemainingCapacity(lineWeight);
                                        this.droneRepository.save(assignedDrone);
                                        System.out.println("OK:change_completed");
                                    } else {
                                        System.out.println("ERROR:drone_cant_carry_new_item");
                                    }
                                } else {
                                    System.out.println("ERROR:customer_cant_afford_new_item");
                                }
                            } else {
                                System.out.println("ERROR:item_already_ordered");
                            }
                        } else {
                            System.out.println("ERROR:item_identifier_does_not_exist");
                        }
                    } else {
                        System.out.println("ERROR:order_identifier_does_not_exist");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("purchase_order")) {
            try {
                String storeName = params[1];
                String orderID = params[2];

                if (this.storeRepository.existsByStoreName(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);

                    if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, orderID)) {
                        Order order = this.orderRepository.findById_StoreNameAndId_orderID(storeName, orderID);
                        String customerAccount = order.getCustomerAccount();
                        Customer customer = this.customerRepository.findByAccount(customerAccount);
                        List<ItemLine> itemLines = this.itemLineRepository.findById_ItemKey_StoreNameAndId_OrderID(
                                storeName,
                                orderID);
                        int orderTotalWeight = 0;
                        String assignedDroneID = order.getAssignedDroneID();
                        Drone drone = this.droneRepository.findById_StoreNameAndId_DroneID(storeName, assignedDroneID);
                        if (drone.getAssignedDronePilotID() != null) {

                            if (drone.getRemainingTrips() > 0) {
                                int orderTotalCost = 0;
                                for (ItemLine itemLine : itemLines) {
                                    orderTotalCost += itemLine.getTotalCost();
                                    orderTotalWeight += itemLine.getTotalWeight();
                                }
                                customer.updateCreditRecord(orderTotalCost);
                                store.updateEarnedRevenue(orderTotalCost);
                                drone.updateRemainingCapacity(-orderTotalWeight);
                                drone.updateRemainingTrips(-1);
                                int dronePendingOrderCount = orderRepository
                                        .countById_StoreNameAndAssignedDroneIDAndOrdertypeID(storeName, assignedDroneID,
                                                "1");
                                Pilot pilot = pilotRepository.findByAccount(drone.getAssignedDronePilotID());
                                pilot.updateDeliveryHistory();
                                order.updateCreatedDate(Utility.getCurrentTime());
                                order.updateOrderType("2");
                                store.updatePurchasedCount();
                                store.updateOverloads(dronePendingOrderCount);

                                this.orderRepository.save(order);
                                this.customerRepository.save(customer);
                                this.storeRepository.save(store);
                                this.droneRepository.save(drone);
                                this.pilotRepository.save(pilot);
                                // if
                                String pendingPickUpOrderID = null;
                                Order pendingPickUpOrder = null;
                                List<Order> storePendingPickUpOrders = this.orderRepository
                                        .findById_StoreNameAndOrdertypeID(storeName, "5");
                                for (Order pendingPickuOrder : storePendingPickUpOrders) {
                                    pendingPickUpOrderID = pendingPickuOrder.getOrderID();
                                    pendingPickUpOrder = pendingPickuOrder;
                                    if (pendingPickUpOrder.getCustomerAccount().equals(customerAccount)) {
                                        break;
                                    }
                                }

                                if (pendingPickUpOrder != null) {
                                    int totalWeight = 0;
                                    List<ItemLine> pendingPickUpOrderItemLines = itemLineRepository
                                            .findById_ItemKey_StoreNameAndId_OrderID(storeName, pendingPickUpOrderID);
                                    for (ItemLine itemLine : pendingPickUpOrderItemLines) {
                                        totalWeight += itemLine.getTotalWeight();
                                    }
                                    if (totalWeight <= drone.getRemainingCapacity() && drone.getRemainingTrips() > 0) {
                                        Order returningOrder = pendingPickUpOrder;
                                        returningOrder.updateOrderType("4");
                                        returningOrder.updateAssignedDroneID(assignedDroneID);
                                        int totalCost = 0;
                                        for (ItemLine itemLine : pendingPickUpOrderItemLines) {
                                            totalCost += itemLine.getTotalCost();
                                        }
                                        store.removePendingPickUpOrder(pendingPickUpOrderID);
                                        drone.updateRemainingTrips(-1);
                                        customer.updateCreditRecord(-totalCost);
                                        store.updateEarnedRevenue(-totalCost);
                                        pilot.updateDeliveryHistory();// to confirm this

                                        this.storeRepository.save(store);
                                        this.droneRepository.save(drone);
                                        this.customerRepository.save(customer);
                                        this.pilotRepository.save(pilot);
                                        this.orderRepository.save(returningOrder);
                                    }
                                }

                                System.out.println("OK:change_completed");
                            } else {
                                System.out.println("ERROR:drone_needs_fuel");
                            }
                        } else {
                            System.out.println("ERROR:drone_needs_pilot");
                        }
                    } else {
                        System.out.println("ERROR:order_identifier_does_not_exist");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }
        } else if (params[0].equals("cancel_order")) {
            try {
                String storeName = params[1];
                String orderID = params[2];
                if (this.storeRepository.existsByStoreName(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);
                    if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, orderID)) {
                        Order order = this.orderRepository.findById_StoreNameAndId_orderID(storeName, orderID);
                        int orderTotalWeight = 0;
                        if (order.getOrderTypeID().equals("1")) {
                            List<ItemLine> itemLines = this.itemLineRepository
                                    .findById_ItemKey_StoreNameAndId_OrderID(storeName, orderID);

                            for (ItemLine line : itemLines) {
                                orderTotalWeight += line.getTotalWeight();
                                this.itemLineRepository.delete(line);
                            }
                            this.orderRepository.delete(order);
                            String assignedDroneID = order.getAssignedDroneID();
                            Drone drone = this.droneRepository.findById_StoreNameAndId_DroneID(storeName,
                                    assignedDroneID);
                            drone.updateRemainingCapacity(-orderTotalWeight);
                            // drone.removeOrderID(orderID);
                            store.removeStoreOrder(orderID);
                            this.droneRepository.save(drone);
                            this.storeRepository.save(store);

                            System.out.println("OK:change_completed");
                        }
                    } else {
                        System.out.println("ERROR:order_identifier_does_not_exist");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("transfer_order")) {
            try {
                String storeName = params[1];
                String orderID = params[2];
                String newDroneID = params[3];

                if (this.storeRepository.existsByStoreName(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);

                    if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, orderID)) {
                        if (this.droneRepository.existsById_StoreNameAndId_DroneID(storeName, newDroneID)) {
                            Order order = this.orderRepository.findById_StoreNameAndId_orderID(storeName, orderID);
                            List<ItemLine> orderLines = this.itemLineRepository
                                    .findById_ItemKey_StoreNameAndId_OrderID(storeName, orderID);
                            int orderTotalWeight = 0;
                            for (ItemLine line : orderLines) {
                                orderTotalWeight += line.getTotalWeight();
                            }
                            String oldDroneID = order.getAssignedDroneID();
                            Drone oldDrone = this.droneRepository.findById_StoreNameAndId_DroneID(storeName,
                                    oldDroneID);
                            Drone newDrone = this.droneRepository.findById_StoreNameAndId_DroneID(storeName,
                                    newDroneID);
                            int remainCapacity = newDrone.getRemainingCapacity();
                            if (!oldDroneID.equals(newDroneID)) {
                                if (orderTotalWeight <= remainCapacity) {
                                    newDrone.updateRemainingCapacity(orderTotalWeight);
                                    oldDrone.updateRemainingCapacity(-orderTotalWeight);
                                    // oldDrone.removeOrderID(orderID);
                                    order.updateAssignedDroneID(newDroneID);
                                    store.updateTransferCount();
                                    this.droneRepository.save(newDrone);
                                    this.droneRepository.save(oldDrone);
                                    this.orderRepository.save(order);
                                    this.storeRepository.save(store);
                                    System.out.println("OK:change_completed");
                                } else {
                                    System.out.println("ERROR:new_drone_does_not_have_enough_capacity");
                                }
                            } else {
                                System.out.println("OK:new_drone_is_current_drone_no_change");
                            }

                        } else {
                            System.out.println("ERROR:drone_identifier_does_not_exist");
                        }
                    } else {
                        System.out.println("ERROR:order_identifier_does_not_exist");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("display_efficiency")) {
            try {
                List<Store> stores = this.storeRepository.findAll();

                for (Store store : stores) {
                    String storeName = store.getName();
                    int purchased = store.getPurchasedCount();
                    int overloads = store.getOverloads();
                    int transfers = store.getTransferCount();
                    System.out.println("name:" + storeName + "," + "purchases:" + purchased + "," + "overloads:"
                            + overloads + "," + "transfers:" + transfers);
                }

                System.out.println("OK:display_completed");
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("request_return")) {
            try {
                String storeName = params[1];
                String returningOrderID = params[2];
                String purchasedOrderID = params[3];
                String customerAccount = params[4];

                if (this.customerRepository.existsById(customerAccount)) {
                    if (this.storeRepository.existsById(storeName)) {
                        if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, purchasedOrderID)) {
                            if (!this.orderRepository.existsById_StoreNameAndId_orderID(storeName, returningOrderID)) {
                                Order purchasedOrder = this.orderRepository.findById_StoreNameAndId_orderID(storeName,
                                        purchasedOrderID);
                                if (purchasedOrder.getOrderTypeID().equals("2")) {
                                    LocalDate purchasedDate = purchasedOrder.getCreatedDate();
                                    int limitPeriod = Utility.getLimitPeriod(); // e.g., 30 days
                                    List<Order> customerReturningOrders = this.orderRepository
                                            .findByCustomerAccountAndOrdertypeID(customerAccount, "4");

                                    int returnCount = 0;
                                    for (Order order : customerReturningOrders) {
                                        LocalDate returningDate = order.getCreatedDate();
                                        System.out.println(returningDate);
                                        int dayDiff = Utility.getDaysDiff(returningDate);
                                        if (dayDiff <= limitPeriod) {
                                            returnCount++;
                                            System.out.println(returnCount);
                                        }
                                    }
                                    if (Utility.validateReturnTimeFrame(purchasedDate)
                                            && Utility.validateReturnFrequency(returnCount)) {

                                        String assignedDroneID = null;
                                        Order returningOrder = new Order(storeName, returningOrderID, assignedDroneID,
                                                customerAccount, "3");
                                        this.orderRepository.save(returningOrder);
                                        System.out.println("OK:change_completed");
                                    } else {
                                        System.out.println("ERROR:order_can_not_be_return");
                                    }
                                } else {
                                    System.out.println("ERROR:purchase_order_identifier_does_not_exist");
                                }
                            } else {
                                System.out.println("ERROR:return_order_already_exists");
                            }
                        } else {
                            System.out.println("ERROR:purchase_order_identifier_does_not_exist");
                        }
                    } else {
                        System.out.println("ERROR:store_identifier_does_not_exist");
                    }
                } else {
                    System.out.println("ERROR:customer_identifier_does_not_exist");
                }

            } catch (Exception e) {
                log.info(e.toString());
                return;
            }
        } else if (params[0].equals("display_purchased_orders")) {
            try {
                String storeName = params[1];
                List<Order> orders = this.orderRepository.findAll();
                for (Order order : orders) {
                    String orderStoreName = order.getStoreName();
                    String orderType = order.getOrderTypeID();
                    String orderID = order.getOrderID();
                    if (orderStoreName.equals(storeName) && orderType.equals("2")) {
                        int numberOfOrderItemLines = this.itemLineRepository.countById_ItemKey_StoreNameAndId_OrderID(
                                storeName,
                                order.getOrderID());
                        if (numberOfOrderItemLines > 0) {
                            List<ItemLine> itemLines = this.itemLineRepository.findById_ItemKey_StoreNameAndId_OrderID(
                                    storeName,
                                    order.getOrderID());
                            System.out.println("orderID:" + order.getOrderID());

                            for (ItemLine itemLine : itemLines) {
                                String itemName = itemLine.getItemName();
                                int quantity = itemLine.getQuantity();
                                int totalCost = itemLine.getTotalCost();
                                int totalWeight = itemLine.getTotalWeight();
                                int returnCount = itemLine.getReturnCount();
                                if (returnCount > 0) {
                                    System.out.println("item_name:" + itemName + "," + "total_quantity:" + quantity
                                            + "," + "total_cost:" + totalCost + "," + "total_weight:" + totalWeight
                                            + "," + "return_count:" + returnCount);
                                } else {
                                    System.out
                                            .println("item_name:" + itemName + "," + "total_quantity:" + quantity + ","
                                                    + "total_cost:" + totalCost + "," + "total_weight:" + totalWeight);
                                }
                            }
                        } else {
                            System.out.println("orderID:" + orderID);
                        }
                        System.out.println("OK:display_completed");

                    } else {
                    }
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("return_item")) {
            try {
                String storeName = params[1];
                String purchasedOrderID = params[2];
                String returnOrderID = params[3];
                String itemName = params[4];
                int returnQuantity = Integer.parseInt(params[5]);

                if (this.storeRepository.existsById(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);
                    if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, purchasedOrderID)) {
                        if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, returnOrderID)) {
                            Order purchasedOrder = this.orderRepository.findById_StoreNameAndId_orderID(storeName,
                                    purchasedOrderID);
                            Order returningOrder = this.orderRepository.findById_StoreNameAndId_orderID(storeName,
                                    returnOrderID);
                            if (this.itemLineRepository.existsById_ItemKey_StoreNameAndId_ItemKey_ItemNameAndId_OrderID(
                                    storeName, itemName, purchasedOrderID)) {
                                ItemLine purchasedLine = this.itemLineRepository
                                        .findById_ItemKey_StoreNameAndId_ItemKey_ItemNameAndId_OrderID(storeName,
                                                itemName, purchasedOrderID);
                                if (!this.itemLineRepository
                                        .existsById_ItemKey_StoreNameAndId_ItemKey_ItemNameAndId_OrderID(storeName,
                                                itemName, returnOrderID)) {
                                    if (purchasedLine.getQuantity()
                                            - purchasedLine.getReturnCount() >= returnQuantity) {
                                        Item storeItem = this.itemRepository.findById_StoreNameAndId_ItemName(storeName,
                                                itemName);
                                        int itemWeight = storeItem.getItemWeight();
                                        int lineWeight = returnQuantity * itemWeight;
                                        int itemPrice = purchasedLine.getUnitPrice();
                                        ItemLine returnLine = new ItemLine(returnOrderID, storeName, itemName,
                                                itemWeight, returnQuantity, itemPrice);
                                        this.itemLineRepository.save(returnLine);
                                        System.out.println("OK:change_completed");
                                    } else {
                                        System.out.println("ERROR:request_amount_dose_not_allow");
                                    }
                                } else {
                                    System.out.println("ERROR:item_already_added_to_returning_order");
                                }
                            } else {
                                System.out.println("ERROR:item_identifier_does_not_exist");
                            }
                        } else {
                            System.out.println("ERROR:returning_orderID_does_not_exist");
                        }
                    } else {
                        System.out.println("ERROR:purchased_order_invalid");
                    }

                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }

            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("placed_return")) {
            try {
                String storeName = params[1];
                String returnOrderID = params[2];
                String purchasedOrderID = params[3];
                if (this.storeRepository.existsById(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);
                    if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, returnOrderID)) {
                        Order returningOrder = this.orderRepository.findById_StoreNameAndId_orderID(storeName,
                                returnOrderID);
                        String customerAccount = returningOrder.getCustomerAccount();
                        Customer customer = this.customerRepository.findByAccount(customerAccount);
                        List<ItemLine> orderLines = this.itemLineRepository
                                .findById_ItemKey_StoreNameAndId_OrderID(storeName, returnOrderID);
                        int totalWeight = 0;
                        for (ItemLine line : orderLines) {
                            totalWeight += line.getTotalWeight();
                        }

                        for (ItemLine returnLine : orderLines) {
                            String itemName = returnLine.getItemName();
                            int returnCount = returnLine.getQuantity();
                            ItemLine purchasedLine = this.itemLineRepository
                                    .findById_ItemKey_StoreNameAndId_ItemKey_ItemNameAndId_OrderID(storeName, itemName,
                                            purchasedOrderID);
                            purchasedLine.updateReturnCount(returnCount);
                            this.itemLineRepository.save(purchasedLine);
                        }

                        List<Drone> storeDrones = this.droneRepository.findById_StoreName(storeName);
                        for (Drone drone : storeDrones) {
                            String droneID = drone.getDroneID();
                            int numOrders = this.orderRepository
                                    .countById_StoreNameAndAssignedDroneIDAndOrdertypeID(storeName, droneID, "1");
                            int remainingCapacity = drone.getRemainingCapacity();

                            if (numOrders == 0 && remainingCapacity > totalWeight) {
                                returningOrder.updateAssignedDroneID(droneID);
                                drone.updateRemainingTrips(-1);
                                this.droneRepository.save(drone);

                                int totalCost = 0;
                                for (ItemLine line : orderLines) {
                                    totalCost += line.getTotalCost();
                                }
                                customer.updateCreditRecord(-totalCost);
                                this.customerRepository.save(customer);
                                store.updateEarnedRevenue(-totalCost);
                                this.storeRepository.save(store);

                                returningOrder.updateOrderType("4");
                                returningOrder.updateCreatedDate(Utility.getCurrentTime());
                                this.orderRepository.save(returningOrder);
                                this.storeRepository.save(store);
                                break;
                            }
                            System.out.println("OK:change_completed");
                        }
                        if (returningOrder.getAssignedDroneID() == null) {
                            returningOrder.updateCreatedDate(Utility.getCurrentTime());
                            returningOrder.updateOrderType("5");
                            this.orderRepository.save(returningOrder);
                            System.out.println("ERROR:drones are unavailable to pick up, try later");
                        }
                    } else {
                        System.out.println("ERROR:returning_orderID_does_not_exist");
                    }

                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("display_returning_orders")) {
            try {
                String storeName = params[1];

                if (this.storeRepository.existsById(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);
                    List<Order> Orders = this.orderRepository.findAll();
                    for (Order order : Orders) {
                        String orderID = order.getOrderID();
                        if (order.getOrderTypeID().equals("3")) {
                            if (itemLineRepository.countById_ItemKey_StoreNameAndId_OrderID(storeName, orderID) > 0) {
                                List<ItemLine> orderLines = this.itemLineRepository
                                        .findById_ItemKey_StoreNameAndId_OrderID(storeName, orderID);
                                System.out.println("orderID:" + orderID);
                                for (ItemLine itemLine : orderLines) {
                                    String itemName = itemLine.getItemName();
                                    int quantity = itemLine.getQuantity();
                                    int totalCost = itemLine.getTotalCost();
                                    int totalWeight = itemLine.getTotalWeight();
                                    System.out
                                            .println("item_name:" + itemName + "," + "total_quantity:" + quantity + ","
                                                    + "total_cost:" + totalCost + "," + "total_weight:" + totalWeight);
                                }
                            } else {
                                System.out.println("orderID:" + orderID);
                            }
                        } else {
                        }
                    }
                    System.out.println("OK:display_completed");
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }
        } else if (params[0].equals("cancel_returning_order")) {
            try {
                String storeName = params[1];
                String returningOrderID = params[2];
                if (this.storeRepository.existsById(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);
                    if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, returningOrderID)) {
                        Order returningOrder = this.orderRepository.findById_StoreNameAndId_orderID(storeName,
                                returningOrderID);

                        if (returningOrder.getOrderTypeID().equals("3")) {
                            List<ItemLine> orderLines = this.itemLineRepository
                                    .findById_ItemKey_StoreNameAndId_OrderID(storeName, returningOrderID);
                            for (ItemLine returnLine : orderLines) {
                                this.itemLineRepository.delete(returnLine);
                            }
                            this.orderRepository.delete(returningOrder);
                        } else {
                            System.out.println("ERROR:order_identifier_does_not_exist");
                        }
                        System.out.println("OK:change_completed");
                    } else {
                        System.out.println("ERROR:order_identifier_does_not_exist");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("adjust_frequency")) {
            try {
                String limitCount = params[1];
                String limitPeriod = params[2];
                if (!limitCount.matches("\\d+") | !limitPeriod.matches("\\d+")) {
                    System.out.println("ERROR:the inputs are not integers");
                }

                AdminSetting adminSetting = this.adminSettingRepository.findBySystemID("admin");
                adminSetting.setLimitCount(Integer.parseInt(limitCount));
                adminSetting.setLimitPeriod(Integer.parseInt(limitPeriod));
                adminSettingRepository.save(adminSetting);

                System.out.println("OK:change_completed");
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("adjust_returningTimeFrame")) {
            try {
                String timeFrame = params[1];
                if (!timeFrame.matches("\\d+")) {
                    System.out.println("ERROR:the input is not an integer");
                }
                AdminSetting adminSetting = this.adminSettingRepository.findBySystemID("admin");
                adminSetting.setReturnPeriod(Integer.parseInt(timeFrame));
                this.adminSettingRepository.save(adminSetting);
                System.out.println("OK:change_completed");
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }
        } else {
            System.out.println("ERROR:invalid_command or no authority to run this command");
            return;
        }

    }

    public void storeManagerCommandReader(String command) {
        String[] params = command.split(",");
        if (params[0].equals("make_store")) {
            try {
                String storeName = params[1];
                int earnedRevenue = Integer.valueOf(params[2]);

                if (!storeRepository.existsByStoreName(storeName)) {
                    Store store = new Store(storeName, earnedRevenue);
                    this.storeRepository.save(store);
                    System.out.println("OK:change_completed");
                } else {
                    System.out.println("ERROR:store_identifier_already_exists");
                }

            } catch (Exception e) {
                log.info(e.toString());
                return;
            }
        } else if (params[0].equals("sell_item")) {
            try {
                String storeName = params[1];
                String itemName = params[2];
                int itemWeight = Integer.valueOf(params[3]);

                if (this.storeRepository.existsByStoreName(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);
                    TreeMap<String, Integer> storeItem = store.getStoreItem();
                    if (storeItem == null || !storeItem.containsKey(itemName)) {
                        store.updateStoreItem(itemName, itemWeight);

                        if (!this.itemRepository.existsById_StoreNameAndId_ItemName(storeName, itemName)) {
                            Item item = new Item(storeName, itemName, itemWeight);
                            this.itemRepository.save(item);
                        }
                        System.out.println("OK:change_completed");
                    } else {
                        System.out.println("ERROR:item_identifier_already_exists");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("display_items")) {
            try {
                String storeName = params[1];
                if (this.storeRepository.existsByStoreName(storeName)) {
                    List<Item> items = this.itemRepository.findById_StoreName(storeName);
                    items.sort(Comparator.comparing(Item::getItemName));
                    for (Item item : items) {
                        String itemName = item.getItemName();
                        String itemWeight = String.valueOf(item.getItemWeight());
                        System.out.println(itemName + "," + itemWeight);
                    }
                    System.out.println("OK:display_completed");
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                return;
            }
        } else if (params[0].equals("make_drone")) {
            try {
                String storeName = params[1];
                String droneID = params[2];
                int liftingCapacity = Integer.valueOf(params[3]);
                int refueling = Integer.valueOf((params[4]));

                if (this.storeRepository.existsByStoreName(storeName)) {
                    if (!droneRepository.existsById_StoreNameAndId_DroneID(storeName, droneID)) {
                        Drone drone = new Drone(storeName, droneID, liftingCapacity, refueling);
                        this.droneRepository.save(drone);
                        System.out.println("OK:change_completed");
                    } else {
                        System.out.println("ERROR:drone_identifier_already_exists");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("display_drones")) {
            try {
                String storeName = params[1];
                if (this.storeRepository.existsByStoreName(storeName)) {

                    List<Drone> drones = this.droneRepository.findById_StoreName(storeName);
                    drones.sort(Comparator.comparing(Drone::getDroneID));
                    for (Drone drone : drones) {
                        String droneID = drone.getDroneID();
                        String liftingCapacity = String.valueOf(drone.getLiftingCapacity());
                        int numberOfOrders = orderRepository
                                .countById_StoreNameAndAssignedDroneIDAndOrdertypeID(storeName, droneID, "1");
                        String remainingCapacity = String.valueOf(drone.getRemainingCapacity());
                        String remainingTrips = String.valueOf(drone.getRemainingTrips());
                        String pilotAccount = drone.getAssignedDronePilotID();
                        if (pilotAccount != null) {
                            Pilot pilot = this.pilotRepository.findByAccount(pilotAccount);
                            String flownBy = pilot.getName();
                            System.out.println("droneID:" + droneID + "," + "total_cap:" + liftingCapacity + ","
                                    + "num_orders:" + numberOfOrders + "," + "remaining_cap:" + remainingCapacity + ","
                                    + "trips_left:" + remainingTrips + "," + "flown_by:" + flownBy);
                        } else {
                            System.out.println("droneID:" + droneID + "," + "total_cap:" + liftingCapacity + ","
                                    + "num_orders:" + numberOfOrders + "," + "remaining_cap:" + remainingCapacity + ","
                                    + "trips_left:" + remainingTrips);
                        }
                    }
                    System.out.println("OK:display_completed");
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("fly_drone")) {
            try {
                String storeName = params[1];
                String newDroneID = params[2];
                String pilotAccount = params[3];
                if (this.pilotRepository.existsByAccount(pilotAccount)) {
                    Pilot pilot = this.pilotRepository.findByAccount(pilotAccount);
                    if (this.storeRepository.existsByStoreName(storeName)) {
                        if (this.droneRepository.existsById_StoreNameAndId_DroneID(storeName, newDroneID)) {
                            Drone newDrone = this.droneRepository.findById_StoreNameAndId_DroneID(storeName,
                                    newDroneID);
                            String oldAssignedPilotID = newDrone.getAssignedDronePilotID();
                            if (oldAssignedPilotID != null) {
                                Pilot oldPilot = this.pilotRepository.findByAccount(oldAssignedPilotID);
                                oldPilot.updateAssignedDroneID(null);
                                this.pilotRepository.save(oldPilot);
                            }
                            newDrone.updateAssignedDronePilotID(pilotAccount);
                            System.out.print(newDrone.getAssignedDronePilotID());
                            this.droneRepository.save(newDrone);
                            String oldDroneID = pilot.getAssignedDroneID();
                            if (oldDroneID != null) {
                                Drone oldDrone = this.droneRepository.findById_StoreNameAndId_DroneID(storeName,
                                        oldDroneID);
                                oldDrone.updateAssignedDronePilotID(null);
                                this.droneRepository.save(oldDrone);
                            }
                            pilot.updateAssignedDroneID(newDroneID);
                            this.pilotRepository.save(pilot);
                            System.out.println("OK:change_completed");
                        } else {
                            System.out.println("ERROR:drone_identifier_does_not_exist");
                        }
                    } else {
                        System.out.println("ERROR:store_identifier_does_not_exist");
                    }
                } else {
                    System.out.println("ERROR:pilot_identifier_does_not_exist");
                }
            } catch (Exception e) {
                return;
            }

        } else if (params[0].equals("display_orders")) {
            try {
                String storeName = params[1];
                if (this.storeRepository.existsByStoreName(storeName)) {
                    List<Order> orders = this.orderRepository.findById_StoreName(storeName);
                    orders.sort(Comparator.comparing(Order::getOrderID));
                    for (Order order : orders) {
                        int numberOfOrderItemLines = this.itemLineRepository.countById_ItemKey_StoreNameAndId_OrderID(
                                storeName,
                                order.getOrderID());
                        if (numberOfOrderItemLines > 0) {
                            List<ItemLine> itemLines = this.itemLineRepository.findById_ItemKey_StoreNameAndId_OrderID(
                                    storeName,
                                    order.getOrderID());
                            System.out.println("orderID:" + order.getOrderID());
                            for (ItemLine itemLine : itemLines) {
                                String itemName = itemLine.getItemName();
                                int quantity = itemLine.getQuantity();
                                int totalCost = itemLine.getTotalCost();
                                int totalWeight = itemLine.getTotalWeight();
                                System.out.println("item_name:" + itemName + "," + "total_quantity:" + quantity + ","
                                        + "total_cost:" + totalCost + "," + "total_weight:" + totalWeight);
                            }
                        } else {
                            System.out.println("orderID:" + order.getOrderID());
                        }
                    }
                    System.out.println("OK:display_completed");
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("display_purchased_orders")) {
            try {
                String storeName = params[1];
                List<Order> orders = this.orderRepository.findAll();
                for (Order order : orders) {
                    String orderStoreName = order.getStoreName();
                    String orderType = order.getOrderTypeID();
                    String orderID = order.getOrderID();
                    if (orderStoreName.equals(storeName) && orderType.equals("2")) {
                        int numberOfOrderItemLines = this.itemLineRepository.countById_ItemKey_StoreNameAndId_OrderID(
                                storeName,
                                order.getOrderID());
                        if (numberOfOrderItemLines > 0) {
                            List<ItemLine> itemLines = this.itemLineRepository.findById_ItemKey_StoreNameAndId_OrderID(
                                    storeName,
                                    order.getOrderID());
                            System.out.println("orderID:" + order.getOrderID());

                            for (ItemLine itemLine : itemLines) {
                                String itemName = itemLine.getItemName();
                                int quantity = itemLine.getQuantity();
                                int totalCost = itemLine.getTotalCost();
                                int totalWeight = itemLine.getTotalWeight();
                                int returnCount = itemLine.getReturnCount();
                                if (returnCount > 0) {
                                    System.out.println("item_name:" + itemName + "," + "total_quantity:" + quantity
                                            + "," + "total_cost:" + totalCost + "," + "total_weight:" + totalWeight
                                            + "," + "return_count:" + returnCount);
                                } else {
                                    System.out
                                            .println("item_name:" + itemName + "," + "total_quantity:" + quantity + ","
                                                    + "total_cost:" + totalCost + "," + "total_weight:" + totalWeight);
                                }
                            }
                        } else {
                            System.out.println("orderID:" + orderID);
                        }
                        System.out.println("OK:display_completed");

                    } else {
                    }
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("display_returning_orders")) {
            try {
                String storeName = params[1];

                if (this.storeRepository.existsById(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);
                    List<Order> Orders = this.orderRepository.findAll();
                    for (Order order : Orders) {
                        String orderID = order.getOrderID();
                        if (order.getOrderTypeID().equals("3")) {
                            if (itemLineRepository.countById_ItemKey_StoreNameAndId_OrderID(storeName, orderID) > 0) {
                                List<ItemLine> orderLines = this.itemLineRepository
                                        .findById_ItemKey_StoreNameAndId_OrderID(storeName, orderID);
                                System.out.println("orderID:" + orderID);
                                for (ItemLine itemLine : orderLines) {
                                    String itemName = itemLine.getItemName();
                                    int quantity = itemLine.getQuantity();
                                    int totalCost = itemLine.getTotalCost();
                                    int totalWeight = itemLine.getTotalWeight();
                                    System.out
                                            .println("item_name:" + itemName + "," + "total_quantity:" + quantity + ","
                                                    + "total_cost:" + totalCost + "," + "total_weight:" + totalWeight);
                                }
                            } else {
                                System.out.println("orderID:" + orderID);
                            }
                        } else {
                        }
                    }
                    System.out.println("OK:display_completed");
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("transfer_order")) {
            try {
                String storeName = params[1];
                String orderID = params[2];
                String newDroneID = params[3];

                if (this.storeRepository.existsByStoreName(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);

                    if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, orderID)) {
                        if (this.droneRepository.existsById_StoreNameAndId_DroneID(storeName, newDroneID)) {
                            Order order = this.orderRepository.findById_StoreNameAndId_orderID(storeName, orderID);
                            List<ItemLine> orderLines = this.itemLineRepository
                                    .findById_ItemKey_StoreNameAndId_OrderID(storeName, orderID);
                            int orderTotalWeight = 0;
                            for (ItemLine line : orderLines) {
                                orderTotalWeight += line.getTotalWeight();
                            }
                            String oldDroneID = order.getAssignedDroneID();
                            Drone oldDrone = this.droneRepository.findById_StoreNameAndId_DroneID(storeName,
                                    oldDroneID);
                            Drone newDrone = this.droneRepository.findById_StoreNameAndId_DroneID(storeName,
                                    newDroneID);
                            int remainCapacity = newDrone.getRemainingCapacity();
                            if (!oldDroneID.equals(newDroneID)) {
                                if (orderTotalWeight <= remainCapacity) {
                                    newDrone.updateRemainingCapacity(orderTotalWeight);
                                    oldDrone.updateRemainingCapacity(-orderTotalWeight);
                                    // oldDrone.removeOrderID(orderID);
                                    order.updateAssignedDroneID(newDroneID);
                                    store.updateTransferCount();
                                    this.droneRepository.save(newDrone);
                                    this.droneRepository.save(oldDrone);
                                    this.orderRepository.save(order);
                                    this.storeRepository.save(store);
                                    System.out.println("OK:change_completed");
                                } else {
                                    System.out.println("ERROR:new_drone_does_not_have_enough_capacity");
                                }
                            } else {
                                System.out.println("OK:new_drone_is_current_drone_no_change");
                            }

                        } else {
                            System.out.println("ERROR:drone_identifier_does_not_exist");
                        }
                    } else {
                        System.out.println("ERROR:order_identifier_does_not_exist");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }
        }
    }

    public void customerCommandReader(String command) {
        String[] params = command.split(",");

        if (params[0].equals("start_order")) {
            try {
                String storeName = params[1];
                String orderID = params[2];
                String droneID = params[3];
                String customerAccount = params[4];

                if (this.storeRepository.existsByStoreName(storeName)) {
                    if (!this.orderRepository.existsById_StoreNameAndId_orderID(storeName, orderID)) {
                        if (this.droneRepository.existsById_StoreNameAndId_DroneID(storeName, droneID)) {
                            if (this.customerRepository.existsByAccount(customerAccount)) {
                                Order order = new Order(storeName, orderID, droneID, customerAccount, "1");
                                this.orderRepository.save(order);
                                System.out.println("OK:change_completed");
                            } else {
                                System.out.println("ERROR:customer_identifier_does_not_exist");
                            }

                        } else {
                            System.out.println("ERROR:drone_identifier_does_not_exist");
                        }

                    } else {
                        System.out.println("ERROR:order_identifier_already_exists");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }

            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("request_item")) {
            try {
                String storeName = params[1];
                String orderID = params[2];
                String itemName = params[3];
                int quantity = Integer.valueOf(params[4]);
                int itemPrice = Integer.valueOf(params[5]);
                int linePrice = quantity * itemPrice;

                if (this.storeRepository.existsByStoreName(storeName)) {
                    if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, orderID)) {
                        Order order = this.orderRepository.findById_StoreNameAndId_orderID(storeName, orderID);
                        if (this.itemRepository.existsById_StoreNameAndId_ItemName(storeName, itemName)) {
                            String customerAccount = order.getCustomerAccount();
                            Customer customer = this.customerRepository.findByAccount(customerAccount);
                            String assignedDroneID = order.getAssignedDroneID();
                            Drone assignedDrone = this.droneRepository.findById_StoreNameAndId_DroneID(storeName,
                                    assignedDroneID);
                            Item storeItem = this.itemRepository.findById_StoreNameAndId_ItemName(storeName, itemName);
                            int remainingCapacity = assignedDrone.getRemainingCapacity();
                            int itemWeight = storeItem.getItemWeight();
                            int lineWeight = quantity * itemWeight;
                            int credit = customer.getCreditRecord();
                            if (!this.itemLineRepository
                                    .existsById_ItemKey_StoreNameAndId_OrderIDAndId_ItemKey_ItemName(storeName, orderID,
                                            itemName)) {
                                List<ItemLine> orderLines = this.itemLineRepository
                                        .findById_ItemKey_StoreNameAndId_OrderID(storeName, orderID);
                                int currentOrderCost = 0;
                                for (ItemLine line : orderLines) {
                                    currentOrderCost += line.getTotalCost();
                                }
                                if (credit - currentOrderCost >= 0) {
                                    if (remainingCapacity - lineWeight >= 0) {
                                        ItemLine itemLine = new ItemLine(orderID, storeName, itemName, itemWeight,
                                                quantity, itemPrice);
                                        this.itemLineRepository.save(itemLine);
                                        assignedDrone.updateRemainingCapacity(lineWeight);
                                        this.droneRepository.save(assignedDrone);
                                        System.out.println("OK:change_completed");
                                    } else {
                                        System.out.println("ERROR:drone_cant_carry_new_item");
                                    }
                                } else {
                                    System.out.println("ERROR:customer_cant_afford_new_item");
                                }
                            } else {
                                System.out.println("ERROR:item_already_ordered");
                            }
                        } else {
                            System.out.println("ERROR:item_identifier_does_not_exist");
                        }
                    } else {
                        System.out.println("ERROR:order_identifier_does_not_exist");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("purchase_order")) {
            try {
                String storeName = params[1];
                String orderID = params[2];

                if (this.storeRepository.existsByStoreName(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);

                    if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, orderID)) {
                        Order order = this.orderRepository.findById_StoreNameAndId_orderID(storeName, orderID);
                        String customerAccount = order.getCustomerAccount();
                        Customer customer = this.customerRepository.findByAccount(customerAccount);
                        List<ItemLine> itemLines = this.itemLineRepository.findById_ItemKey_StoreNameAndId_OrderID(
                                storeName,
                                orderID);
                        int orderTotalWeight = 0;
                        String assignedDroneID = order.getAssignedDroneID();
                        Drone drone = this.droneRepository.findById_StoreNameAndId_DroneID(storeName, assignedDroneID);
                        if (drone.getAssignedDronePilotID() != null) {

                            if (drone.getRemainingTrips() > 0) {
                                int orderTotalCost = 0;
                                for (ItemLine itemLine : itemLines) {
                                    orderTotalCost += itemLine.getTotalCost();
                                    orderTotalWeight += itemLine.getTotalWeight();
                                }
                                customer.updateCreditRecord(orderTotalCost);
                                store.updateEarnedRevenue(orderTotalCost);
                                drone.updateRemainingCapacity(-orderTotalWeight);
                                drone.updateRemainingTrips(-1);
                                int dronePendingOrderCount = orderRepository
                                        .countById_StoreNameAndAssignedDroneIDAndOrdertypeID(storeName, assignedDroneID,
                                                "1");
                                Pilot pilot = pilotRepository.findByAccount(drone.getAssignedDronePilotID());
                                pilot.updateDeliveryHistory();
                                order.updateCreatedDate(Utility.getCurrentTime());
                                order.updateOrderType("2");
                                store.updatePurchasedCount();
                                store.updateOverloads(dronePendingOrderCount);

                                this.orderRepository.save(order);
                                this.customerRepository.save(customer);
                                this.storeRepository.save(store);
                                this.droneRepository.save(drone);
                                this.pilotRepository.save(pilot);
                                // if
                                String pendingPickUpOrderID = null;
                                Order pendingPickUpOrder = null;
                                List<Order> storePendingPickUpOrders = this.orderRepository
                                        .findById_StoreNameAndOrdertypeID(storeName, "5");
                                for (Order pendingPickuOrder : storePendingPickUpOrders) {
                                    pendingPickUpOrderID = pendingPickuOrder.getOrderID();
                                    pendingPickUpOrder = pendingPickuOrder;
                                    if (pendingPickUpOrder.getCustomerAccount().equals(customerAccount)) {
                                        break;
                                    }
                                }

                                if (pendingPickUpOrder != null) {
                                    int totalWeight = 0;
                                    List<ItemLine> pendingPickUpOrderItemLines = itemLineRepository
                                            .findById_ItemKey_StoreNameAndId_OrderID(storeName, pendingPickUpOrderID);
                                    for (ItemLine itemLine : pendingPickUpOrderItemLines) {
                                        totalWeight += itemLine.getTotalWeight();
                                    }
                                    if (totalWeight <= drone.getRemainingCapacity() && drone.getRemainingTrips() > 0) {
                                        Order returningOrder = pendingPickUpOrder;
                                        returningOrder.updateOrderType("4");
                                        returningOrder.updateAssignedDroneID(assignedDroneID);
                                        int totalCost = 0;
                                        for (ItemLine itemLine : pendingPickUpOrderItemLines) {
                                            totalCost += itemLine.getTotalCost();
                                        }
                                        store.removePendingPickUpOrder(pendingPickUpOrderID);
                                        drone.updateRemainingTrips(-1);
                                        customer.updateCreditRecord(-totalCost);
                                        store.updateEarnedRevenue(-totalCost);
                                        pilot.updateDeliveryHistory();// to confirm this

                                        this.storeRepository.save(store);
                                        this.droneRepository.save(drone);
                                        this.customerRepository.save(customer);
                                        this.pilotRepository.save(pilot);
                                        this.orderRepository.save(returningOrder);
                                    }
                                }

                                System.out.println("OK:change_completed");
                            } else {
                                System.out.println("ERROR:drone_needs_fuel");
                            }
                        } else {
                            System.out.println("ERROR:drone_needs_pilot");
                        }
                    } else {
                        System.out.println("ERROR:order_identifier_does_not_exist");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }
        } else if (params[0].equals("cancel_order")) {
            try {
                String storeName = params[1];
                String orderID = params[2];
                if (this.storeRepository.existsByStoreName(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);
                    if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, orderID)) {
                        Order order = this.orderRepository.findById_StoreNameAndId_orderID(storeName, orderID);
                        int orderTotalWeight = 0;
                        if (order.getOrderTypeID().equals("1")) {
                            List<ItemLine> itemLines = this.itemLineRepository
                                    .findById_ItemKey_StoreNameAndId_OrderID(storeName, orderID);

                            for (ItemLine line : itemLines) {
                                orderTotalWeight += line.getTotalWeight();
                                this.itemLineRepository.delete(line);
                            }
                            this.orderRepository.delete(order);
                            String assignedDroneID = order.getAssignedDroneID();
                            Drone drone = this.droneRepository.findById_StoreNameAndId_DroneID(storeName,
                                    assignedDroneID);
                            drone.updateRemainingCapacity(-orderTotalWeight);
                            store.removeStoreOrder(orderID);
                            this.droneRepository.save(drone);
                            this.storeRepository.save(store);

                            System.out.println("OK:change_completed");
                        }
                    } else {
                        System.out.println("ERROR:order_identifier_does_not_exist");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("request_return")) {
            try {
                String storeName = params[1];
                String returningOrderID = params[2];
                String purchasedOrderID = params[3];
                String customerAccount = params[4];

                if (this.customerRepository.existsById(customerAccount)) {
                    if (this.storeRepository.existsById(storeName)) {
                        if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, purchasedOrderID)) {
                            if (!this.orderRepository.existsById_StoreNameAndId_orderID(storeName, returningOrderID)) {
                                Order purchasedOrder = this.orderRepository.findById_StoreNameAndId_orderID(storeName,
                                        purchasedOrderID);
                                if (purchasedOrder.getOrderTypeID().equals("2")) {
                                    LocalDate purchasedDate = purchasedOrder.getCreatedDate();
                                    int limitPeriod = Utility.getLimitPeriod(); // e.g., 30 days
                                    List<Order> customerReturningOrders = this.orderRepository
                                            .findByCustomerAccountAndOrdertypeID(customerAccount, "4");

                                    int returnCount = 0;
                                    for (Order order : customerReturningOrders) {
                                        LocalDate returningDate = order.getCreatedDate();
                                        System.out.println(returningDate);
                                        int dayDiff = Utility.getDaysDiff(returningDate);
                                        if (dayDiff <= limitPeriod) {
                                            returnCount++;
                                            System.out.println(returnCount);
                                        }
                                    }
                                    if (Utility.validateReturnTimeFrame(purchasedDate)
                                            && Utility.validateReturnFrequency(returnCount)) {

                                        String assignedDroneID = null;
                                        Order returningOrder = new Order(storeName, returningOrderID, assignedDroneID,
                                                customerAccount, "3");
                                        this.orderRepository.save(returningOrder);
                                        System.out.println("OK:change_completed");
                                    } else {
                                        System.out.println("ERROR:order_can_not_be_return");
                                    }
                                } else {
                                    System.out.println("ERROR:purchase_order_identifier_does_not_exist");
                                }
                            } else {
                                System.out.println("ERROR:return_order_already_exists");
                            }
                        } else {
                            System.out.println("ERROR:purchase_order_identifier_does_not_exist");
                        }
                    } else {
                        System.out.println("ERROR:store_identifier_does_not_exist");
                    }
                } else {
                    System.out.println("ERROR:customer_identifier_does_not_exist");
                }

            } catch (Exception e) {
                log.info(e.toString());
                return;
            }
        } else if (params[0].equals("return_item")) {
            try {
                String storeName = params[1];
                String purchasedOrderID = params[2];
                String returnOrderID = params[3];
                String itemName = params[4];
                int returnQuantity = Integer.parseInt(params[5]);

                if (this.storeRepository.existsById(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);
                    if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, purchasedOrderID)) {
                        if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, returnOrderID)) {
                            Order purchasedOrder = this.orderRepository.findById_StoreNameAndId_orderID(storeName,
                                    purchasedOrderID);
                            Order returningOrder = this.orderRepository.findById_StoreNameAndId_orderID(storeName,
                                    returnOrderID);
                            if (this.itemLineRepository.existsById_ItemKey_StoreNameAndId_ItemKey_ItemNameAndId_OrderID(
                                    storeName, itemName, purchasedOrderID)) {
                                ItemLine purchasedLine = this.itemLineRepository
                                        .findById_ItemKey_StoreNameAndId_ItemKey_ItemNameAndId_OrderID(storeName,
                                                itemName, purchasedOrderID);
                                if (!this.itemLineRepository
                                        .existsById_ItemKey_StoreNameAndId_ItemKey_ItemNameAndId_OrderID(storeName,
                                                itemName, returnOrderID)) {
                                    if (purchasedLine.getQuantity()
                                            - purchasedLine.getReturnCount() >= returnQuantity) {
                                        Item storeItem = this.itemRepository.findById_StoreNameAndId_ItemName(storeName,
                                                itemName);
                                        int itemWeight = storeItem.getItemWeight();
                                        int lineWeight = returnQuantity * itemWeight;
                                        int itemPrice = purchasedLine.getUnitPrice();
                                        ItemLine returnLine = new ItemLine(returnOrderID, storeName, itemName,
                                                itemWeight, returnQuantity, itemPrice);
                                        this.itemLineRepository.save(returnLine);
                                        System.out.println("OK:change_completed");
                                    } else {
                                        System.out.println("ERROR:request_amount_dose_not_allow");
                                    }
                                } else {
                                    System.out.println("ERROR:item_already_added_to_returning_order");
                                }
                            } else {
                                System.out.println("ERROR:item_identifier_does_not_exist");
                            }
                        } else {
                            System.out.println("ERROR:returning_orderID_does_not_exist");
                        }
                    } else {
                        System.out.println("ERROR:purchased_order_invalid");
                    }

                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }

            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else if (params[0].equals("placed_return")) {
            try {
                String storeName = params[1];
                String returnOrderID = params[2];
                String purchasedOrderID = params[3];
                if (this.storeRepository.existsById(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);
                    if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, returnOrderID)) {
                        Order returningOrder = this.orderRepository.findById_StoreNameAndId_orderID(storeName,
                                returnOrderID);
                        String customerAccount = returningOrder.getCustomerAccount();
                        Customer customer = this.customerRepository.findByAccount(customerAccount);
                        List<ItemLine> orderLines = this.itemLineRepository
                                .findById_ItemKey_StoreNameAndId_OrderID(storeName, returnOrderID);
                        int totalWeight = 0;
                        for (ItemLine line : orderLines) {
                            totalWeight += line.getTotalWeight();
                        }

                        for (ItemLine returnLine : orderLines) {
                            String itemName = returnLine.getItemName();
                            int returnCount = returnLine.getQuantity();
                            ItemLine purchasedLine = this.itemLineRepository
                                    .findById_ItemKey_StoreNameAndId_ItemKey_ItemNameAndId_OrderID(storeName, itemName,
                                            purchasedOrderID);
                            purchasedLine.updateReturnCount(returnCount);
                            this.itemLineRepository.save(purchasedLine);
                        }

                        List<Drone> storeDrones = this.droneRepository.findById_StoreName(storeName);
                        for (Drone drone : storeDrones) {
                            String droneID = drone.getDroneID();
                            int numOrders = this.orderRepository
                                    .countById_StoreNameAndAssignedDroneIDAndOrdertypeID(storeName, droneID, "1");
                            int remainingCapacity = drone.getRemainingCapacity();

                            if (numOrders == 0 && remainingCapacity > totalWeight) {
                                returningOrder.updateAssignedDroneID(droneID);
                                drone.updateRemainingTrips(-1);
                                this.droneRepository.save(drone);

                                int totalCost = 0;
                                for (ItemLine line : orderLines) {
                                    totalCost += line.getTotalCost();
                                }
                                customer.updateCreditRecord(-totalCost);
                                this.customerRepository.save(customer);
                                store.updateEarnedRevenue(-totalCost);
                                this.storeRepository.save(store);

                                returningOrder.updateOrderType("4");
                                returningOrder.updateCreatedDate(Utility.getCurrentTime());
                                this.orderRepository.save(returningOrder);
                                this.storeRepository.save(store);
                                break;
                            }
                            System.out.println("OK:change_completed");
                        }
                        if (returningOrder.getAssignedDroneID() == null) {
                            returningOrder.updateCreatedDate(Utility.getCurrentTime());
                            returningOrder.updateOrderType("5");
                            this.orderRepository.save(returningOrder);
                            System.out.println("ERROR:drones are unavailable to pick up, try later");
                        }
                    } else {
                        System.out.println("ERROR:returning_orderID_does_not_exist");
                    }

                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }
        } else if (params[0].equals("cancel_returning_order")) {
            try {
                String storeName = params[1];
                String returningOrderID = params[2];
                if (this.storeRepository.existsById(storeName)) {
                    Store store = this.storeRepository.findByStoreName(storeName);
                    if (this.orderRepository.existsById_StoreNameAndId_orderID(storeName, returningOrderID)) {
                        Order returningOrder = this.orderRepository.findById_StoreNameAndId_orderID(storeName,
                                returningOrderID);

                        if (returningOrder.getOrderTypeID().equals("3")) {
                            List<ItemLine> orderLines = this.itemLineRepository
                                    .findById_ItemKey_StoreNameAndId_OrderID(storeName, returningOrderID);
                            for (ItemLine returnLine : orderLines) {
                                this.itemLineRepository.delete(returnLine);
                            }
                            this.orderRepository.delete(returningOrder);
                        } else {
                            System.out.println("ERROR:order_identifier_does_not_exist");
                        }
                        System.out.println("OK:change_completed");
                    } else {
                        System.out.println("ERROR:order_identifier_does_not_exist");
                    }
                } else {
                    System.out.println("ERROR:store_identifier_does_not_exist");
                }
            } catch (Exception e) {
                log.info(e.toString());
                return;
            }

        } else {
            System.out.println("ERROR:unauthorized, you can't access this commands");
            return;
        }

    }

}
