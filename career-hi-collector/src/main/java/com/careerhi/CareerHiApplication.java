package com.careerhi;

import com.careerhi.collector.service.GptAnalysisService;
import com.careerhi.collector.service.OcrService;
import com.careerhi.collector.service.SeleniumService;
import com.careerhi.common.domain.JobPost;
import com.careerhi.common.domain.JobPostRepository;
import com.careerhi.common.domain.SkillStatsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
@EntityScan("com.careerhi.common.domain")
@EnableJpaRepositories("com.careerhi.common.domain")
public class CareerHiApplication {

	public static void main(String[] args) {
		var context = SpringApplication.run(CareerHiApplication.class, args);

		System.out.println("============== [🚀 실전 채용 데이터 수집 시작] ==============");

		SeleniumService seleniumService = context.getBean(SeleniumService.class);
		OcrService ocrService = context.getBean(OcrService.class);
		JobPostRepository jobPostRepository = context.getBean(JobPostRepository.class);
		GptAnalysisService gptService = context.getBean(GptAnalysisService.class);


		String baseUrl = "https://www.work24.go.kr/wk/a/b/1200/retriveDtlEmpSrchList.do?basicSetupYn=&careerTo=&keywordJobCd=&occupation=133100%2C133101%2C133200%2C133300%2C133301%2C134100&seqNo=&cloDateEndtParam=&payGbn=&templateInfo=&rot2WorkYn=&shsyWorkSecd=&resultCnt=10&keywordJobCont=&cert=&moreButtonYn=&minPay=&codeDepth2Info=11000&eventNo=&mode=&major=&resrDutyExcYn=&eodwYn=&sortField=DATE&staArea=&sortOrderBy=DESC&keyword=&termSearchGbn=&carrEssYns=&benefitSrchAndOr=O&occupationParam=133100%2C133101%2C133200%2C133300%2C133301%2C134100&disableEmpHopeGbn=&actServExcYn=&keywordStaAreaNm=&maxPay=&emailApplyYn=&codeDepth1Info=11000&keywordEtcYn=&regDateStdtParam=&publDutyExcYn=&keywordJobCdSeqNo=&viewType=&exJobsCd=&templateDepthNmInfo=&region=&employGbn=&empTpGbcd=1&computerPreferential=&infaYn=&cloDateStdtParam=&siteClcd=all&searchMode=Y&birthFromYY=&indArea=&careerTypes=&subEmpHopeYn=&tlmgYn=&academicGbn=&templateDepthNoInfo=&foriegn=&entryRoute=&mealOfferClcd=&basicSetupYnChk=&station=&holidayGbn=&srcKeyword=&academicGbnoEdu=noEdu&enterPriseGbn=&cloTermSearchGbn=&birthToYY=&keywordWantedTitle=&stationNm=&benefitGbn=&keywordFlag=&notSrcKeyword=&essCertChk=&depth2SelCode=&keywordBusiNm=&preferentialGbn=&rot3WorkYn=&regDateEndtParam=&pfMatterPreferential=&termContractMmcnt=&careerFrom=&laborHrShortYn=";

		for (int page = 1; page <= 100; page++) {
			System.out.println("\n####### PAGE " + page + " 수집 중... #######");
			String currentUrl = baseUrl + "&currentPageNo=" + page + "&pageIndex=" + page;
			List<String> jobUrls = seleniumService.scrapJobUrls(currentUrl);

			if (jobUrls.isEmpty()) {
				System.out.println("⛔ 더 이상 공고가 없습니다. 종료합니다.");
				break;
			}

			int newJobCount = 0;

			for (String detailUrl : jobUrls) {
				if (jobPostRepository.existsBySourceUrl(detailUrl)) {
					System.out.print(".");
					continue;
				}

				newJobCount++;
				System.out.println("\nProcessing [Page " + page + "] New Job: " + detailUrl);

				try {
					String pageText = seleniumService.getPageText(detailUrl);
					String imgUrl = seleniumService.findImageUrl(detailUrl);
					String ocrText = (imgUrl != null) ? ocrService.readImageText(imgUrl) : "";

					String rawData = "URL: " + detailUrl + "\n[본문]\n" + pageText + "\n[이미지내용]\n" + ocrText;
					GptAnalysisService.AnalysisResult result = gptService.analyze(rawData);

					JobPost jobPost = new JobPost();
					jobPost.setSourceUrl(detailUrl);
					jobPost.setCompanyName(result.companyName());
					jobPost.setJobTitle(result.jobTitle());
					jobPost.setJobCategory(result.jobCategory());
					jobPost.setRequiredSkills(result.requiredSkills());
					jobPost.setPreferredSkills(result.preferredSkills());
					jobPost.setPostedAt(LocalDate.now());

					jobPostRepository.save(jobPost);
					System.out.println("   ✨ 저장: " + result.jobCategory() + " | " + result.jobTitle());

				} catch (Exception e) {
					System.out.println("   ❌ 에러: " + e.getMessage());
				}

				try { Thread.sleep(1000); } catch (InterruptedException e) {}
			}

			if (newJobCount == 0) {
				System.out.println("\n⏭️ (중복 패스) 다음 페이지로 넘어갑니다...");
			}
		}

		System.out.println("\n============== [수집 종료] ==============");

		SkillStatsService statsService = context.getBean(SkillStatsService.class);
		statsService.generateStatsFromDb();

		System.out.println("📊 DB 기반 스킬 통계 업데이트 완료!");
	}
}