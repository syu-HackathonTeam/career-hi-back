package com.careerhi.common.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SkillStatsService {

    private final JobPostRepository jobPostRepository;
    private final SkillStatRepository skillStatRepository;

    @Transactional
    public void generateStatsFromDb() {
        skillStatRepository.deleteAll();

        List<JobPost> allPosts = jobPostRepository.findAll();

        Map<String, Map<String, Integer>> categorySkillCounts = new HashMap<>();
        Map<String, Integer> totalSkillCounts = new HashMap<>();

        for (JobPost post : allPosts) {
            String category = post.getJobCategory() != null ? post.getJobCategory() : "기타 개발";

            String req = post.getRequiredSkills() != null ? post.getRequiredSkills() : "";
            String pref = post.getPreferredSkills() != null ? post.getPreferredSkills() : "";
            String combined = (req + "," + pref).toUpperCase().replace(" ", "");

            String[] skills = combined.split(",");

            for (String skill : skills) {
                if (skill.isEmpty() || skill.equalsIgnoreCase("NULL") || skill.length() > 50) continue;

                categorySkillCounts.putIfAbsent(category, new HashMap<>());
                Map<String, Integer> skillMap = categorySkillCounts.get(category);
                skillMap.put(skill, skillMap.getOrDefault(skill, 0) + 1);

                totalSkillCounts.put(skill, totalSkillCounts.getOrDefault(skill, 0) + 1);
            }
        }

        List<SkillStat> statsToSave = new ArrayList<>();

        categorySkillCounts.forEach((category, skillMap) -> {
            skillMap.forEach((skillName, count) -> {
                statsToSave.add(new SkillStat(category, skillName, count));
            });
        });

        totalSkillCounts.forEach((skillName, count) -> {
            statsToSave.add(new SkillStat("전체", skillName, count));
        });

        skillStatRepository.saveAll(statsToSave);
        System.out.println("📊 [완료] 카테고리별 통계 + '전체' 통합 통계가 DB에 저장되었습니다.");
    }
}