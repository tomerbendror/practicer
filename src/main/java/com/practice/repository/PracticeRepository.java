package com.practice.repository;

import com.practice.controller.EmailController;
import com.practice.dto.PracticeDto;
import com.practice.dto.PracticeTtsStateDto;
import com.practice.model.*;
import com.practice.property.UserSharedPracticeWithGroupPropertyKey;
import com.practice.type.TTSGenerationState;
import com.practice.util.PracticerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.*;

/**
 * Created by Tomer
 */
@Repository("practiceRepository")
@Transactional
public class PracticeRepository extends BaseRepository {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private EmailController emailController;

    @Autowired
    private TTSRepository ttsRepository;

    public PracticeDto createPractice(Long parentId, PracticeDto practiceDto) {
        ParentUser creator = parentRepository.getParent(parentId);
        Practice newPractice = new Practice();
        newPractice.setCreator(creator);
        creator.getCreatedPractices().add(newPractice);
        PracticeDto retPracticeDto = mergePractice(parentId, newPractice, practiceDto);
        for (PracticeToGroup practiceToGroup : newPractice.getPracticeToGroups()) {
            ParentsGroup group = practiceToGroup.getParentsGroup();
            emailController.sendPracticeSharedWithGroupMail(creator, group, newPractice);
            creator.setPropertyValue(new UserSharedPracticeWithGroupPropertyKey(group.getId(), newPractice.getId()), true);
        }
        return retPracticeDto;
    }

    public PracticeDto updatePractice(Long parentId, Long practiceId, PracticeDto practiceDto) {
        Practice existPractice = genericDao.find(Practice.class, practiceId);
        ParentUser creator = parentRepository.getParent(parentId);
        PracticeDto retPracticeDto = mergePractice(parentId, existPractice, practiceDto);
        for (PracticeToGroup practiceToGroup : existPractice.getPracticeToGroups()) {
            ParentsGroup updatedGroup = practiceToGroup.getParentsGroup();
            UserSharedPracticeWithGroupPropertyKey propertyKey = new UserSharedPracticeWithGroupPropertyKey(updatedGroup.getId(), existPractice.getId());
            if (!creator.getPropertyValue(propertyKey)) {   // we allow to send mail to group only once
                emailController.sendPracticeSharedWithGroupMail(creator, updatedGroup, existPractice);
                creator.setPropertyValue(propertyKey, true);
            }
        }
        return retPracticeDto;
    }

    public PracticeDto mergeQuestions(Long parentId, Long trgPracticeId, Long srcPracticeId) {
        Practice trgPractice = genericDao.find(Practice.class, trgPracticeId);
        Practice srcPractice = genericDao.find(Practice.class, srcPracticeId);
        copyQuestion(trgPractice, srcPractice);
        return new PracticeDto(trgPractice, parentId, TTSGenerationState.DONE);   // DONE - as we should have all TTS already
    }

    public PracticeDto duplicatePractice(Long parentId, Long practiceId, PracticeDto newPracticeDto) {
        Practice existPractice = genericDao.find(Practice.class, practiceId);
        ParentUser parent = parentRepository.getParent(parentId);
        
        Practice newPractice = existPractice.duplicate();
        newPractice.setName(newPracticeDto.getName());
        newPractice.setCreator(parent);
        copyQuestion(newPractice, existPractice);
        getEm().persist(newPractice);

        return new PracticeDto(newPractice, parentId, TTSGenerationState.DONE);   // DONE - as we should have all TTS already
    }

    private void copyQuestion(Practice trgPractice, Practice srcPractice) {
        List<Question> srcQuestions = trgPractice.getQuestions();
        Map<String, Question> questionStrToQuestionMap = new HashMap<>(srcQuestions.size());
        for (Question question : srcQuestions) {
            String questionStr = PracticerUtils.stripHebrewVowels(question.getQuestionStr().toLowerCase().trim());
            questionStrToQuestionMap.put(questionStr, question);
        }

        for (Question newQuestion : srcPractice.getQuestions()) {
            String newQuestionStr = PracticerUtils.stripHebrewVowels(newQuestion.getQuestionStr().toLowerCase().trim());
            Question existQuestion = questionStrToQuestionMap.get(newQuestionStr);
            if (existQuestion == null) {    // add the question
                Question duplicateQuestion = newQuestion.duplicate();
                trgPractice.getQuestions().add(duplicateQuestion);
                duplicateQuestion.setPractice(trgPractice);
            } else {    // merge the question
                existQuestion.mergeWith(newQuestion);
            }
        }

        int questionOrder = 0;
        for (Question question : trgPractice.getQuestions()) {
            question.setOrderIndex(questionOrder++);
        }
    }

    private PracticeDto mergePractice(Long parentId, Practice existPractice, PracticeDto practiceDto) {
        existPractice.setName(practiceDto.getName());
        existPractice.setDescription(practiceDto.getDescription());
        existPractice.setMaxSecondsForQuestion(practiceDto.getMaxSecondsForQuestion());
        existPractice.setMaxMistakesNum(practiceDto.getMaxMistakesNum());
        existPractice.setQuestionsPerPractice(practiceDto.getQuestionsPerPractice());
        existPractice.setRandomOrder(practiceDto.getRandomOrder());
        existPractice.setUseTTS(practiceDto.getUseTTS());
        existPractice.setQuestionType(practiceDto.getQuestionsType());  // not supported for update, it's here for create flow
        existPractice.setTtsLang(practiceDto.getTtsLang());

        Set<Long> deletedQuestionsIdsSet = new HashSet<>(practiceDto.getDeletedQuestionsIds());
        List<Question> questionsToDelete = new ArrayList<>(deletedQuestionsIdsSet.size());

        // create or update the questions
        int questionOrder = 0;
        for (Question updateQuestion : practiceDto.getQuestions()) {
            if (updateQuestion.getId() != null && updateQuestion.getId() < 0) {   // new question
                Question newQuestion = Question.newQuestion(updateQuestion);
                newQuestion.setOrderIndex(questionOrder++);
                newQuestion.setPractice(existPractice);
                existPractice.getQuestions().add(newQuestion);
            } else {    // update or delete question
                for (Question existQuestion : existPractice.getQuestions()) {
                    if (existQuestion.getId().equals(updateQuestion.getId())) {
                        existQuestion.updateFrom(updateQuestion);
                        existQuestion.setOrderIndex(questionOrder++);
                        break;
                    }
                }
            }
        }

        // handle deleted questions
        for (Question existQuestion : existPractice.getQuestions()) {
            if (deletedQuestionsIdsSet.contains(existQuestion.getId())) {
                questionsToDelete.add(existQuestion);
            }
        }
        existPractice.getQuestions().removeAll(questionsToDelete);


        // handle the group sharing
        Set<PracticeToGroup> existSharedGroups = existPractice.getPracticeToGroups();
        Set<Long> shareGroupIds = new HashSet<>(existSharedGroups.size());
        for (PracticeToGroup practiceToGroup : existSharedGroups) {
            shareGroupIds.add(practiceToGroup.getParentsGroup().getId());
        }

        Set<PracticeToGroup> newGroupToShare = new HashSet<>();
        List<PracticeDto.PracticeShareGroupDto> practiceSharedGroupData = practiceDto.getPracticeSharedGroupData();
        if (practiceSharedGroupData != null) {
            for (PracticeDto.PracticeShareGroupDto shareGroupDto : practiceSharedGroupData) {
                boolean groupExist = false;

                if (shareGroupDto.getIsShare()) {   // the client send all the group include the ones he doesn't want to share, here we handle only the ones he want to share
                    for (PracticeToGroup existSharedGroup : existSharedGroups) {
                        if (existSharedGroup.equals(existPractice.getId(), shareGroupDto.getGroupId())) {
                            shareGroupIds.remove(existSharedGroup.getParentsGroup().getId());
                            groupExist = true;
                            break;  // we already have this group as shared
                        }
                    }

                    if (!groupExist) {  // need to share new group
                        ParentsGroup group = getEm().find(ParentsGroup.class, shareGroupDto.getGroupId());
                        PracticeToGroup practiceToGroup = new PracticeToGroup(existPractice, group);
                        newGroupToShare.add(practiceToGroup);
                    }
                }
            }
        }

        // remove the groups the user doesn't want to share any more
        for (Long shareGroupId : shareGroupIds) {
            Set<PracticeToGroup> practiceToGroups = existPractice.getPracticeToGroups();
            for (PracticeToGroup practiceToGroup : practiceToGroups) {//todo
                if (practiceToGroup.getParentsGroup().getId().equals(shareGroupId)) {
                    practiceToGroups.remove(practiceToGroup);
                    break;
                }
            }
        }

        existPractice.getPracticeToGroups().addAll(newGroupToShare);

        // handle the child sharing
        ParentUser parentUser = getEm().find(ParentUser.class, parentId);
        parentUser.getChilds();     // just load all child in single query to keep it in the session, later we will use it

        Set<ChildUser> existSharedChilds = new HashSet<>(existPractice.getChilds());
        Set<ChildUser> childsToRemove = new HashSet<>(existSharedChilds);

        Set<ChildUser> newChildToShare = new HashSet<>();
        List<PracticeDto.PracticeShareChildDto> practiceSharedChildData = practiceDto.getPracticeSharedChildData();
        if (practiceSharedChildData != null) {
            for (PracticeDto.PracticeShareChildDto shareChildDto : practiceSharedChildData) {
                boolean childExist = false;

                if (shareChildDto.getIsShare()) {   // the client send all the childs include the ones he doesn't want to share, here we handle only the ones he want to share
                    for (ChildUser existSharedChild : existSharedChilds) {
                        if (existSharedChild.getId().equals(shareChildDto.getChildId())) {
                            childExist = true;
                            childsToRemove.remove(existSharedChild);
                            break;  // we already have this child as shared
                        }
                    }

                    if (!childExist) {  // need to share new child
                        ChildUser child = getEm().find(ChildUser.class, shareChildDto.getChildId());
                        newChildToShare.add(child);
                    }
                }
            }
        }

        existPractice.getChilds().removeAll(childsToRemove);
        existPractice.getChilds().addAll(newChildToShare);

        TTSGenerationState ttsGenerationState = TTSGenerationState.DONE;
        if (practiceDto.getUseTTS()) {
            ttsGenerationState = validateTTS(existPractice);
        }

        if (existPractice.getId() == null) {
            getEm().persist(existPractice);
        }

        return new PracticeDto(existPractice, parentId, ttsGenerationState);
    }

    private TTSGenerationState validateTTS(Practice existPractice) {
        QuestionType questionType = existPractice.getQuestionType();
        if ((questionType == QuestionType.TRANSLATION && existPractice.getUseTTS()) || questionType == QuestionType.DICTATION) {
            return ttsRepository.generateTTS(existPractice);
        }
        return TTSGenerationState.DONE;
    }

    public PracticeDto removePractice(Long practiceId) {
        Practice practice = genericDao.find(Practice.class, practiceId);
        PracticeDto practiceDto = new PracticeDto(practice, practiceId);

        practice.getCreator().getCreatedPractices().remove(practice);
        practice.getChilds().clear();
        getEm().remove(practice);
        return practiceDto;
    }

    @SuppressWarnings("unused")
    public boolean isPracticeOwner(Long practiceId) {
        Practice practice = genericDao.find(Practice.class, practiceId);
        if (practice != null) {
            Long parentId = getUserDetails().getId();
            return practice.getCreator().getId().equals(parentId);
        }
        return false;
    }

    @Transactional(readOnly = true)
    public PracticeDto getPracticeDto(Long practiceId, Long parentId) {
        Practice practice = genericDao.find(Practice.class, practiceId);
        if (practice == null) {
            return null;
        }
        return new PracticeDto(practice, parentId);
    }

    public PracticeTtsStateDto getPracticeTtsState(Long practiceId) {
        Practice practice = genericDao.find(Practice.class, practiceId);
        if (practice == null) {
            return null;
        }
        Query query = getEm().createNativeQuery("SELECT TTS_GENERATION_STATE, count(*) FROM question WHERE PRACTICE_ID=:practiceId GROUP BY TTS_GENERATION_STATE");
        query.setParameter("practiceId", practiceId);
        List resultList = query.getResultList();
        Map<TTSGenerationState, Integer> stateToCountMap = new HashMap<>(resultList.size());
        for (Object o : resultList) {
            Object[] row = (Object[]) o;
            TTSGenerationState ttsGenerationState = TTSGenerationState.valueOf(row[0].toString());
            Integer count = Integer.valueOf((row[1].toString()));
            stateToCountMap.put(ttsGenerationState, count);
        }
        PracticeTtsStateDto practiceTtsStateDto = new PracticeTtsStateDto(practiceId, stateToCountMap);
        if (practiceTtsStateDto.getSummaryState() == TTSGenerationState.DONE) {
            query = getEm().createNativeQuery("SELECT ID, TTS_URL FROM question WHERE TTS_URL is not NULL AND PRACTICE_ID=:practiceId");
            query.setParameter("practiceId", practiceId);
            List ttsUrls = query.getResultList();

            Map<Long, String> questionIdToTtsUrl = new HashMap<>(ttsUrls.size());
            for (Object o : ttsUrls) {
                Object[] row = (Object[]) o;
                Long questionId = Long.valueOf(row[0].toString());
                String ttsUrl = row[1].toString();
                questionIdToTtsUrl.put(questionId, ttsUrl);
            }
            practiceTtsStateDto.setQuestionIdToTtsUrl(questionIdToTtsUrl);
        }
        return practiceTtsStateDto;
    }
}
