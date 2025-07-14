package ma.hmdurabatmaroc.hmdurabat;

import ma.hmdurabatmaroc.hmdurabat.security.entities.Role;
import ma.hmdurabatmaroc.hmdurabat.security.entities.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.transaction.support.TransactionTemplate;
import javax.persistence.EntityManager;
import java.util.Collections;

@SpringBootApplication
public class HmdurabatApplication {

	public static void main(String[] args) {
		SpringApplication.run(HmdurabatApplication.class, args);
	}

	@Bean
	CommandLineRunner start(TransactionTemplate transactionTemplate, EntityManager entityManager, PasswordEncoder passwordEncoder) {
		return args -> {
			transactionTemplate.execute(status -> {
				// Create new roles if they don't exist
				Role superAdminRole = getOrCreateRole(entityManager, "SUPER_ADMIN");
				Role contentManagerRole = getOrCreateRole(entityManager, "CONTENT_MANAGER");
				Role communicationManagerRole = getOrCreateRole(entityManager, "COMMUNICATION_MANAGER");
				Role clientManagerRole = getOrCreateRole(entityManager, "CLIENT_MANAGER");

				// Create superadmin user if not exists
				User superadminUser = entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'superadmin'", User.class)
						.getResultList()
						.stream()
						.findFirst()
						.orElseGet(() -> {
							User newUser = new User();
							newUser.setUsername("superadmin");
							newUser.setPassword(passwordEncoder.encode("superadmin"));
							newUser.setActive(true);
							newUser.setRoles(Collections.singleton(superAdminRole));
							entityManager.persist(newUser);
							return newUser;
						});

				entityManager.flush();
				System.out.println("Successfully initialized superadmin user with SUPER_ADMIN role");
				return null;
			});
		};
	}

	private static Role getOrCreateRole(EntityManager entityManager, String roleName) {
		return entityManager.createQuery("SELECT r FROM Role r WHERE r.roleName = :roleName", Role.class)
				.setParameter("roleName", roleName)
				.getResultList()
				.stream()
				.findFirst()
				.orElseGet(() -> {
					Role newRole = new Role();
					newRole.setRoleName(roleName);
					entityManager.persist(newRole);
					return newRole;
				});
	}
}
