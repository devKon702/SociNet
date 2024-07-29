package com.example.socinet;

import com.example.socinet.entity.React;
import com.example.socinet.entity.Role;
import com.example.socinet.repository.ReactRepository;
import com.example.socinet.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class SocinetApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocinetApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(RoleRepository roleRepo, ReactRepository reactRepo) {
        return arg -> {
            List<Role> roles = roleRepo.findAll();
            if (roles.isEmpty()) {
                roles.add(new Role("USER"));
                roles.add(new Role("ADMIN"));
                roleRepo.saveAll(roles);
            }

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
