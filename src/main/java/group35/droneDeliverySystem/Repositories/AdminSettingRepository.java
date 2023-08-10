package group35.droneDeliverySystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import group35.droneDeliverySystem.Classes.AdminSetting;

public interface AdminSettingRepository extends JpaRepository<AdminSetting, String> {

    boolean existsBySystemIDAndPassword(String account, String password);

    AdminSetting findBySystemID(String account);
}
