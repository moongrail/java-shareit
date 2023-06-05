package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exceptions.handler.ErrorHandler;

@SpringBootApplication
public class ShareItApp {

	public static void main(String[] args) {
		SpringApplication.run(ShareItApp.class, args);
	}

}
