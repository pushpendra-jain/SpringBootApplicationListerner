package com.jp.web.SpringBootApplicationListenerDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.HashSet;

@SpringBootApplication
@EnableAutoConfiguration
public class SpringBootApplicationListenerDemoApplication extends SpringBootServletInitializer implements WebApplicationInitializer {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(SpringBootApplicationListenerDemoApplication.class);

		//This is how we will register the listener with spring boot lifecycle
		springApplication.addListeners(new SystemPropertiesLoader());

		springApplication.run(args);

		System.out.println("System property [desiredPropertyName] = " + System.getProperty("desiredPropertyName"));
	}

	// For a traditional web application
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return super.configure(builder).listeners(new SystemPropertiesLoader());
	}
}

