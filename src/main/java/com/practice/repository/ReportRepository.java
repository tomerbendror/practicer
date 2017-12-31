package com.practice.repository;

import com.practice.dto.PracticeResultDto;
import com.practice.dto.QuestionResultDto;
import com.practice.model.ChildUser;
import com.practice.model.Practice;
import com.practice.model.statistics.PracticeResult;
import com.practice.model.statistics.QuestionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by Tomer
 */
@Repository("reportRepository")
@Transactional
public class ReportRepository extends BaseRepository {

    @Autowired
    private ChildRepository childRepository;

    public void addPracticeReport(PracticeResultDto practiceResultDto) {
        ChildUser child = childRepository.getChild(getUserDetails().getId());
        Practice practice = getEm().find(Practice.class, practiceResultDto.getPracticeId());

        PracticeResult practiceResult = new PracticeResult();

        float totalTimeSecond = 0;
        List<QuestionResult> questionResults = practiceResult.getQuestionResults();
        for (QuestionResultDto questionResultDto : practiceResultDto.getQuestionResults()) {
            QuestionResult questionResult = new QuestionResult(questionResultDto);
            questionResult.setPracticeResult(practiceResult);
            questionResults.add(questionResult);
            totalTimeSecond += questionResultDto.getQuestionTimeSecond();
        }

        practiceResult.setPractice(practice);
        practiceResult.setChildUser(child);
        practiceResult.setScore(practiceResultDto.getScore());
        practiceResult.setTimeSecond((int) totalTimeSecond);

        getEm().persist(practiceResult);
    }

    public List<Long> recentPracticesIds(Long childId) {
        ChildUser child = childRepository.getChild(childId);
        String sqlStr = "SELECT practice.id FROM PracticeResult pr where pr.childUser=:child GROUP BY pr.practice.id ORDER BY MAX(pr.createdTime) DESC";
        TypedQuery<Long> query = getEm().createQuery(sqlStr, Long.class);
        query.setParameter("child", child);
        query.setMaxResults(20);
        return query.getResultList();
    }
}
