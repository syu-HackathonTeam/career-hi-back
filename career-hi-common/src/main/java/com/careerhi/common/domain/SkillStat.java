package com.careerhi.common.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "market_skill_stats")
@Getter @Setter @NoArgsConstructor
public class SkillStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stat_id;

    private String job_category;
    private String skill_name;
    private Integer demand_count;
    private LocalDate stat_date;

    public SkillStat(String job_category, String skill_name, Integer demand_count) {
        this.job_category = job_category;
        this.skill_name = skill_name;
        this.demand_count = demand_count;
        this.stat_date = LocalDate.now();
    }
}