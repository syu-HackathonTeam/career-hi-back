package com.careerhi.common.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "market_job_postings")
@Getter @Setter
@NoArgsConstructor
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "job_category")
    private String jobCategory;

    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills;

    @Column(name = "preferred_skills", columnDefinition = "TEXT")
    private String preferredSkills;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "posted_at")
    private LocalDate postedAt;

    @Column(name = "crawled_at")
    private LocalDateTime crawledAt;

    @PrePersist
    public void prePersist() {
        this.crawledAt = LocalDateTime.now();
    }
}