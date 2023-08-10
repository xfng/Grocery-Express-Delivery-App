package group35.droneDeliverySystem;

import group35.droneDeliverySystem.Classes.SessionContext;

import group35.droneDeliverySystem.Classes.Drone;
import group35.droneDeliverySystem.Repositories.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import group35.droneDeliverySystem.Repositories.*;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

	// private static final Logger log = LoggerFactory.getLogger(App.class);
	static StoreRepository storeRepository;
	static CustomerRepository customerRepository;
	static UserRepository userRepository;
	static DroneRepository droneRepository;
	static OrderRepository orderRepository;
	static ItemRepository itemRepository;
	static ItemLineRepository itemLineRepository;
	static PilotRepository pilotRepository;
	static EmployeeRepository employeeRepository;
	static AdminSettingRepository adminSettingRepository;
	static SessionContext sessionContext;

	@Component
	public static class AppRunner implements ApplicationRunner {

		private final Commands tool;

		@Autowired
		public AppRunner(StoreRepository storeRepository, CustomerRepository customerRepository,
				UserRepository userRepository, DroneRepository droneRepository,
				OrderRepository orderRepository, ItemRepository itemRepository,
				ItemLineRepository itemLineRepository, PilotRepository pilotRepository,
				EmployeeRepository employeeRepository, AdminSettingRepository adminSettingRepository,
				SessionContext sessionContext) {
			this.tool = new Commands(storeRepository, customerRepository, userRepository, droneRepository,
					orderRepository, itemRepository, itemLineRepository, pilotRepository, employeeRepository,
					adminSettingRepository, sessionContext);
		}

		@Override
		public void run(ApplicationArguments args) {
			System.out.println("Welcome to the Grocery Express Delivery Service!");
			tool.run();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
