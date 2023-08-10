package group35.droneDeliverySystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import group35.droneDeliverySystem.Classes.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

}
