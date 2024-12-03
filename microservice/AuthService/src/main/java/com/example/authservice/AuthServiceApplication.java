package com.example.authservice;

import com.example.authservice.entity.Role;
import com.example.authservice.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(RoleRepository roleRepo) {
		return arg -> {
			List<Role> roles = roleRepo.findAll();
			if (roles.isEmpty()) {
				roles.add(new Role("USER"));
				roles.add(new Role("ADMIN"));
				roleRepo.saveAll(roles);
			}
		};
	}

}
