package com.example.mission;

import com.example.mission.user.entity.Directeur;
import com.example.mission.user.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MissionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MissionApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UtilisateurRepository utilisateurRepository,
						   PasswordEncoder passwordEncoder) {
		return args -> {
			if (utilisateurRepository.findByEmail("test@test.com").isEmpty()) {

				Directeur user = new Directeur();
				user.setNom("Test");
				user.setPrenom("User");
				user.setCin("12345678");
				user.setEmail("test@test.com");
				user.setTelephone("12345678");
				user.setMotDePasse(passwordEncoder.encode("password123"));

				utilisateurRepository.save(user);
			}
		};
	}
}
