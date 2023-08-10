package group35.droneDeliverySystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import group35.droneDeliverySystem.Classes.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {

        Customer findByAccount(String account);
    
        boolean existsByAccount(String account);


}
