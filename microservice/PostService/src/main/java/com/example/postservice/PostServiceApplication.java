package com.example.postservice;

import com.example.postservice.entity.React;
import com.example.postservice.repository.ReactRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class PostServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(ReactRepository reactRepo) {
		return arg -> {
			List<React> reacts = reactRepo.findAll();
			if(reacts.isEmpty()){
				reacts.add(new React("LIKE"));
				reacts.add(new React("LOVE"));
				reacts.add(new React("HAHA"));
				reacts.add(new React("SAD"));
				reactRepo.saveAll(reacts);
			}
		};
	}

}
