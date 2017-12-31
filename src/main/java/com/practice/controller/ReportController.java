package com.practice.controller;

import com.practice.dto.PracticeResultDto;
import com.practice.etc.UserSession;
import com.practice.job.StatisticsJob;
import com.practice.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Tomer
 */
@Controller
@RequestMapping("/report")
@SessionAttributes("userSession")
public class ReportController extends BaseController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private StatisticsJob statisticsJob;

    @RequestMapping(value="/practiceResult", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    public void addPracticeReport(@RequestBody PracticeResultDto practiceResultDto, UserSession userSession) {
        boolean parentViewAsChild = userSession.getViewAsChildParentId() != null;
        if (!parentViewAsChild) {  // not security issue, just to ignore case where parent view the app as one of his childs
            reportRepository.addPracticeReport(practiceResultDto);
        }
    }

    @RequestMapping(value="/dailyJob", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public void runDailyJob() {
        statisticsJob.dailyJob();
    }
}
