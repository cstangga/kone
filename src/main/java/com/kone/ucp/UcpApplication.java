package com.kone.ucp;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableJpaAuditing
@SpringBootApplication
public class UcpApplication {

	public static void main(String[] args) {

		// 디렉토리 생성
		Path directoryPath = Paths.get("C:\\uploads");
		try {
			Files.createDirectory(directoryPath);
			log.info(directoryPath + " 디렉토리가 생성되었습니다.");
		} catch (FileAlreadyExistsException e) {
			log.info("디렉토리가 이미 존재합니다");
		} catch (NoSuchFileException e) {
			log.info("디렉토리 경로가 존재하지 않습니다");
		} catch (IOException e) {
			e.printStackTrace();
		}

		//스프링부트 초기화
		SpringApplication.run(UcpApplication.class, args);
	}

}
