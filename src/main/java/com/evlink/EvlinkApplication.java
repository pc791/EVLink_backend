package com.evlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableScheduling
@SpringBootApplication
@PropertySource(value = {"classpath:/properties/config.properties"}, encoding = "UTF-8")
public class EvlinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvlinkApplication.class, args);
	}
	
	// CORS 설정: 프론트엔드 도메인 허용 + withCredentials 허용
    @Bean
    WebMvcConfigurer crosConfigurer() {
		return new WebMvcConfigurer() {
			
			// CORS(Cross-Origin Resource Sharing 설정
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				//System.out.println("Cross Allow Origin 실행!");
				registry.addMapping("/**")
				        .allowedOrigins(
				        		"http://localhost:3001", 
				        		"http://localhost:3000", 
				        		"http://127.0.0.1:3001", 
				        		"http://127.0.0.1:3000",
				        		"http://3.34.69.170:3000",
				        		"http://3.34.69.170:3001"
				        		)
				        .allowedHeaders("*")
				        .allowedMethods("*")
				        .allowCredentials(true) // true로 설정해야 withCredentials 동작
				        .maxAge(3600);
			}
		};
	}

}
