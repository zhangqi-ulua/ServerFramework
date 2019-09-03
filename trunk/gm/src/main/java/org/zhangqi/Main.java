package org.zhangqi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan("org.zhangqi")
public class Main {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Main.class);
		app.addListeners(new ApplicationReadyEventListener());
		app.run(args);
	}
}
