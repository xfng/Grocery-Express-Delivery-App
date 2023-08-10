package group35.droneDeliverySystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import group35.droneDeliverySystem.Classes.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByAccountAndPassword(String account, String password);

    User findByAccountAndPassword(String account, String password);

}
