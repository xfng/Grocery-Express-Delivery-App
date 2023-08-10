package group35.droneDeliverySystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import group35.droneDeliverySystem.Classes.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, String> {
    Store findByStoreName(String storeName);
    boolean existsByStoreName(String storeName);

}