package com.practice.controller;

import com.practice.dto.ChildPracticesSummaryDto;
import com.practice.dto.ChildPropertiesDto;
import com.practice.dto.GenericResponseDto;
import com.practice.repository.ChildRepository;
import com.practice.repository.ReportRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * User: tomer
 */
@Controller
@RequestMapping("/child")
public class ChildController extends BaseController {
    private static final Logger logger = Logger.getLogger(ChildController.class);

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ReportRepository reportRepository;

    @RequestMapping(value="/practicesSummary", method = RequestMethod.GET)
    @PreAuthorize("hasRole('child')")
    @ResponseBody
    @Transactional(readOnly = true)
    public ChildPracticesSummaryDto getChildPracticesSummary() {
        return childRepository.getChildPracticesSummaryDto(getUserDetails().getId());
    }

    @RequestMapping(value="/properties", method = RequestMethod.GET)
    @PreAuthorize("hasRole('child')")
    @ResponseBody
    @Transactional(readOnly = true)
    public ChildPropertiesDto getChildSummary() {
        return childRepository.getChildPropertiesDto(getUserDetails().getId());
    }

    @RequestMapping(value="/recentPractices", method = RequestMethod.GET)
    @PreAuthorize("hasRole('child')")
    @ResponseBody
    @Transactional(readOnly = true)
    public List<Long> getRecentPractices() {
        return reportRepository.recentPracticesIds(getUserDetails().getId());
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasRole('child')")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<ChildPropertiesDto>> updateChildProperties(@RequestBody ChildPropertiesDto childPropertiesDto) {
        Long childId = getUserDetails().getId();
        ChildPropertiesDto updated = childRepository.updateChild(childId, childPropertiesDto);
        return new ResponseEntity<>(GenericResponseDto.success(updated), HttpStatus.OK);
    }
}
