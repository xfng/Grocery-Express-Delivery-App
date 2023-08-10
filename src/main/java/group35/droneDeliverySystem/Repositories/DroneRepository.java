package group35.droneDeliverySystem.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.repository.CrudRepository;

import group35.droneDeliverySystem.Classes.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DroneRepository extends JpaRepository<Drone, String> {

    // all the queries are defined in the interface

    Drone findById_StoreNameAndId_DroneID(String storeName, String droneID);

    List<Drone> findById_StoreName(String storeName);

    boolean existsById_StoreNameAndId_DroneID(String storeName, String droneID);

}
