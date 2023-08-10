package group35.droneDeliverySystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import group35.droneDeliverySystem.Classes.Pilot;

public interface PilotRepository extends JpaRepository<Pilot, String> {

    Pilot findByAccount(String account);

    boolean existsByAccount(String account);

    boolean existsByLicenseID(String licenseID);
}
