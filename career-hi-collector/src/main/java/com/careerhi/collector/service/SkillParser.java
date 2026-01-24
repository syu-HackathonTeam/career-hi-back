package com.careerhi.collector.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillParser {

    private final List<String> SKILL_DICTIONARY = Arrays.asList(
            "JAVA", "PYTHON", "KOTLIN", "SCALA", "GROOVY",
            "C++", "C#", "C", "GO", "RUST", "SWIFT", "DART",
            "SPRING", "SPRING BOOT", "DJANGO", "FLASK", "FASTAPI",
            "NODE.JS", "EXPRESS", "NESTJS", "REACT", "VUE", "ANGULAR",
            "JPA", "HIBERNATE", "MYBATIS", "JQUERY",
            "MYSQL", "MARIADB", "POSTGRESQL", "ORACLE", "MSSQL", "MONGODB", "REDIS",
            "AWS", "AZURE", "GCP", "DOCKER", "KUBERNETES", "JENKINS", "GIT",
            "LINUX", "UBUNTU", "CENTOS", "NGINX", "APACHE", "KAFKA", "RABBITMQ"
    );

    public List<String> parseSkills(String rawText) {
        if (rawText == null || rawText.isEmpty()) {
            return new ArrayList<>();
        }

        String cleanText = rawText.toUpperCase()
                .replace("\n", " ")
                .replace("\r", " ")
                .replace(",", " ")
                .replace("/", " ");

        List<String> foundSkills = new ArrayList<>();

        for (String skill : SKILL_DICTIONARY) {
            if (cleanText.contains(skill)) {
                foundSkills.add(skill);
            }
        }

        return foundSkills.stream().distinct().collect(Collectors.toList());
    }
}