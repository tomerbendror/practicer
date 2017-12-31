package com.practice.controller;

import com.practice.dto.GenericResponseDto;
import com.practice.dto.PracticeDto;
import com.practice.dto.PracticeTtsStateDto;
import com.practice.model.Answer;
import com.practice.model.Question;
import com.practice.model.QuestionType;
import com.practice.repository.PracticeRepository;
import com.practice.security.UserDetails;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tomer
 */
@Controller
@RequestMapping("/practice")
public class PracticeController extends BaseController {
    private static final Logger logger = Logger.getLogger(GroupController.class);

    @Autowired
    private PracticeRepository practiceRepository;

    @RequestMapping(value = "/{practiceId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<GenericResponseDto<PracticeDto>> getPractice(@PathVariable("practiceId") Long practiceId) {
        UserDetails userDetails = getUserDetails();
        Long parentId = userDetails.isParent() ? userDetails.getId() : null;
        PracticeDto practiceDto = practiceRepository.getPracticeDto(practiceId, parentId);
        if (practiceDto == null) {
            return new ResponseEntity<>(GenericResponseDto.<PracticeDto>failure("entityNotFound"), HttpStatus.OK);
        }
        return new ResponseEntity<>(GenericResponseDto.success(practiceDto), HttpStatus.OK);
    }

    @RequestMapping(value = "/{practiceId}", method = RequestMethod.POST)
    @PreAuthorize("@practiceRepository.isPracticeOwner(#practiceId)")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<PracticeDto>> updatePractice(@PathVariable("practiceId") Long practiceId, @Valid @RequestBody PracticeDto practiceDto) {
        UserDetails parentDetails = getUserDetails();
        PracticeDto updatedPractice = practiceRepository.updatePractice(parentDetails.getId(), practiceId, practiceDto);
        return new ResponseEntity<>(GenericResponseDto.success(updatedPractice), HttpStatus.OK);
    }

    @RequestMapping(value = "/{practiceId}/mergeQuestions", method = RequestMethod.POST)
    @PreAuthorize("@practiceRepository.isPracticeOwner(#practiceId)")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<PracticeDto>> mergeQuestions(@PathVariable("practiceId") Long practiceId, @RequestBody Long srcPracticeId) {
        UserDetails parentDetails = getUserDetails();
        PracticeDto updatedPractice = practiceRepository.mergeQuestions(parentDetails.getId(), practiceId, srcPracticeId);
        return new ResponseEntity<>(GenericResponseDto.success(updatedPractice), HttpStatus.OK);
    }

    @RequestMapping(value = "/{practiceId}/duplicatePractice", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<GenericResponseDto<PracticeDto>> duplicatePractice(@PathVariable("practiceId") Long practiceId, @RequestBody PracticeDto practiceDto) {
        UserDetails parentDetails = getUserDetails();
        PracticeDto createdPractice = practiceRepository.duplicatePractice(parentDetails.getId(), practiceId, practiceDto);
        return new ResponseEntity<>(GenericResponseDto.success(createdPractice), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasRole('parent') || hasRole('admin')")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<PracticeDto>> createPractice(@Valid @RequestBody PracticeDto createdPracticeDto) {
        UserDetails parentDetails = getUserDetails();
        PracticeDto createdPractice = practiceRepository.createPractice(parentDetails.getId(), createdPracticeDto);
        return new ResponseEntity<>(GenericResponseDto.success(createdPractice), HttpStatus.OK);
    }

    @RequestMapping(value="/uploadFile", method=RequestMethod.POST)
    public String handleFileUpload(Model model, @RequestParam("practiceName") String practiceName,
                                                 @RequestParam("questionsType") QuestionType questionsType, @RequestParam("practiceFile") MultipartFile file){
        if (StringUtils.isBlank(practiceName)) {
            practiceName = getUserDetails().getUsername() + "_" + System.currentTimeMillis();
        }
        try {
            PracticeDto practiceDto = new PracticeDto();
            practiceDto.setName(practiceName);
            practiceDto.setDescription("description");
            practiceDto.setDescription("");
            practiceDto.setMaxMistakesNum(3);
            practiceDto.setMaxSecondsForQuestion(20);
            practiceDto.setRandomOrder(true);
            practiceDto.setQuestionType(questionsType);
            practiceDto.setQuestionsPerPractice(40);
            practiceDto.setUseTTS(true);


            @SuppressWarnings("unchecked")
            List<String> lines = IOUtils.readLines(file.getInputStream(), "UTF-8");
            for (int lineIndex = 1; lineIndex < lines.size(); lineIndex++) {    // skip the first row, leave it for future metadata
                String line = lines.get(lineIndex);
                String[] split = line.split(",");
                if (split.length >= 2) {
                    String questionStr = split[0].trim();
                    if (StringUtils.isNoneBlank(questionStr)) {
                        Question question = new Question();
                        question.setId(-1L);
                        question.setQuestionType(questionsType);
                        question.setOrderIndex(lineIndex);
                        question.setQuestionStr(questionStr);

                        List<Answer> answers = new LinkedList<>();
                        for (int i = 1; i < split.length; i++) {
                            String answerStr = split[i].trim();
                            if (StringUtils.isNotBlank(answerStr)) {
                                Answer answer = new Answer();
                                answer.setId(-1L);
                                answer.setQuestion(question);
                                answer.setIsCorrect(true);
                                answer.setAnswerStr(answerStr);
                                answers.add(answer);
                            }
                        }
                        if (!answers.isEmpty()) {
                            question.getAnswers().addAll(answers);
                            practiceDto.getQuestions().add(question);
                        }
                    }
                }
            }

            if (!practiceDto.getQuestions().isEmpty()) {
                createPractice(practiceDto);
                model.addAttribute("message", "התרגיל '" + practiceName + "' נוצר בהצלחה");
                return "app/import-practice-from-file";
            }
            model.addAttribute("message", "לא נמצאו שאלות - התרגיל לא נוצר");
            return "app/import-practice-from-file";
        } catch (IOException e) {
            logger.error("fail to read the input file");
            model.addAttribute("message", "כישלון בקריאת הקובץ");
            return "app/import-practice-from-file";
        }
    }

    @RequestMapping(value="/{practiceId}", method = RequestMethod.DELETE)
    @PreAuthorize("@practiceRepository.isPracticeOwner(#practiceId) || hasRole('admin')")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<PracticeDto>> removePractice(@PathVariable("practiceId") Long practiceId) {
        PracticeDto removedPractice = practiceRepository.removePractice(practiceId);
        return new ResponseEntity<>(GenericResponseDto.success(removedPractice), HttpStatus.OK);
    }

    @RequestMapping(value = "/{practiceId}/ttsState", method = RequestMethod.GET)
    @PreAuthorize("@practiceRepository.isPracticeOwner(#practiceId)")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<PracticeTtsStateDto>> getPracticeTtsState(@PathVariable("practiceId") Long practiceId) {
        PracticeTtsStateDto practiceTtsStateDto = practiceRepository.getPracticeTtsState(practiceId);
        if (practiceTtsStateDto == null) {
            return new ResponseEntity<>(GenericResponseDto.<PracticeTtsStateDto>failure("entityNotFound"), HttpStatus.OK);
        }
        return new ResponseEntity<>(GenericResponseDto.success(practiceTtsStateDto), HttpStatus.OK);
    }
}
