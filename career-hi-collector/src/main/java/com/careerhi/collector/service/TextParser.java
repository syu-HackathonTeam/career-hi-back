package com.careerhi.collector.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TextParser {

    private final List<String> SKILL_DICTIONARY = Arrays.asList(
            "JAVA", "PYTHON", "KOTLIN", "SCALA", "C++", "C#",

            "SPRING", "SPRING BOOT", "SPRINGBOOT", "JPA", "HIBERNATE", "MYBATIS", "THYMELEAF", "JSP",

            "DJANGO", "FLASK", "NODE.JS", "NESTJS", "REACT", "VUE", "ANGULAR", "JQUERY",
            "JAVASCRIPT", "TYPESCRIPT", "HTML", "CSS",

            "MYSQL", "MARIADB", "POSTGRESQL", "ORACLE", "MSSQL", "MONGODB", "REDIS",

            "SAP", "SAP ERP", "ABAP", "ERP",

            "AWS", "AZURE", "GCP", "DOCKER", "KUBERNETES", "K8S", "JENKINS", "GIT", "GITHUB", "BITBUCKET",
            "LINUX", "UBUNTU", "CENTOS", "NGINX", "KAFKA",

            "KUBEFLOW", "MLFLOW", "AIRFLOW", "ARGO", "GPU", "PYTORCH", "TENSORFLOW", "LLM"
    );

    public ParsedResult parse(String text) {
        ParsedResult result = new ParsedResult();
        if (text == null || text.isEmpty()) return result;

        String cleanText = text.toUpperCase().replace("\r", "");
        String[] lines = cleanText.split("\n");

        boolean isRequired = false;
        boolean isPreferred = false;
        boolean foundAnySection = false;

        for (String line : lines) {
            line = line.trim();
            if (line.length() < 2) continue;

            if (line.contains("자격") || line.contains("필수") || line.contains("QUALIFICATION") || line.contains("모집분야")) {
                isRequired = true; isPreferred = false; foundAnySection = true; continue;
            }
            if (line.contains("우대") || line.contains("PREFERRED") || line.contains("PLUS")) {
                isPreferred = true; isRequired = false; foundAnySection = true; continue;
            }

            for (String skill : SKILL_DICTIONARY) {
                if (line.contains(skill)) {
                    if (isPreferred) result.addPreferred(skill);
                    else result.addRequired(skill);
                }
            }
        }

        if (!foundAnySection || (result.required.isEmpty() && result.preferred.isEmpty())) {
            for (String skill : SKILL_DICTIONARY) {
                if (cleanText.contains(skill)) {
                    result.addRequired(skill);
                }
            }
        }

        return result;
    }

    @lombok.Getter
    public static class ParsedResult {
        private final List<String> required = new ArrayList<>();
        private final List<String> preferred = new ArrayList<>();

        public void addRequired(String s) { if(!required.contains(s)) required.add(s); }
        public void addPreferred(String s) { if(!preferred.contains(s)) preferred.add(s); }

        public String getRequiredString() { return String.join(", ", required); }
        public String getPreferredString() { return String.join(", ", preferred); }
    }
}