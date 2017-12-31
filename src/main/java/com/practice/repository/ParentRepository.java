package com.practice.repository;

import com.practice.dto.*;
import com.practice.etc.error.LogicalException;
import com.practice.etc.error.UserNotFoundException;
import com.practice.model.*;
import com.practice.property.UserProperties;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

/**
 * User: tomer
 */
@Repository("parentRepository")
@Transactional
public class ParentRepository extends UserRepository {

    public void createParent(ParentUser parent) {
        parent.setPassword(passwordEncoder.encode(parent.getPassword()));
        parent.setUserName(parent.getEmail());
        genericDao.persist(parent);
        genericDao.flush();
    }

    @Transactional(readOnly = true)
    public ParentUser getParent(Long parentId) {
        return genericDao.find(ParentUser.class, parentId);
    }

    public ChildUser addChild(Long parentId, ChildUser childUser) {
        if (exist(childUser)) {
            throw new LogicalException("user name '" + childUser.getUserName() + "' already exist", "username.already.exist");
        }
        String password = childUser.getPassword();
        if (StringUtils.isBlank(password)) {
            throw new LogicalException("fail to create a child with empty password", "child.created.with.empty.password");
        }
        childUser.setPassword(passwordEncoder.encode(password));
        ParentUser parent = getParent(parentId);
        parent.getChilds().add(childUser);
        childUser.getParents().add(parent);

        for (ParentToGroup parentToGroup : parent.getParentToGroups()) {
            ParentsGroup parentsGroup = parentToGroup.getParentsGroup();
            parentsGroup.getChildsToGroup().add(new ChildToGroup(childUser, parentsGroup));
        }
        return childUser;
    }

    @Transactional(readOnly = true)
    public List<ChildUser> getChilds(Long parentId) {
        ParentUser parent = getParent(parentId);
        Hibernate.initialize(parent.getChilds());
        return parent.getChilds();
    }

    @Transactional(readOnly = true)
    public boolean isParentOf(Long childId) {
        Long parentId = getUserDetails().getId();
        ChildUser childUser = genericDao.find(ChildUser.class, childId);
        if (childUser != null) {
            for (ParentUser parentUser : childUser.getParents()) {
                if (parentUser.getId().equals(parentId)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<PracticeDto> getPractices(Long parentId) {
        ParentUser parent = getParent(parentId);
        Map<Long, PracticeDto> idToPracticeMap = new HashMap<>();

        for (ParentToGroup parentToGroup : parent.getParentToGroups()) {
            Set<PracticeToGroup> groupPractices = parentToGroup.getParentsGroup().getPractices();
            for (PracticeToGroup groupPractice : groupPractices) {
                Practice practice = groupPractice.getPractice();
                idToPracticeMap.put(practice.getId(), new PracticeDto(practice, parentId));
            }
        }

        for (Practice practice : parent.getCreatedPractices()) {
            idToPracticeMap.put(practice.getId(), new PracticeDto(practice, parentId));
        }
        return new ArrayList<>(idToPracticeMap.values());
    }

    @Transactional(readOnly = true)
    public List<PracticeSummaryDto> getPracticesSummary(Long parentId) {
        ParentUser parent = getParent(parentId);
        Map<Long, PracticeSummaryDto> idToPracticeMap = new HashMap<>();

        for (ParentToGroup parentToGroup : parent.getParentToGroups()) {
            Set<PracticeToGroup> groupPractices = parentToGroup.getParentsGroup().getPractices();
            for (PracticeToGroup groupPractice : groupPractices) {
                Practice practice = groupPractice.getPractice();
                idToPracticeMap.put(practice.getId(), new PracticeSummaryDto(practice, parent, null));
            }
        }

        for (Practice practice : parent.getCreatedPractices()) {
            idToPracticeMap.put(practice.getId(), new PracticeSummaryDto(practice, parent));
        }
        return new ArrayList<>(idToPracticeMap.values());
    }

    @Transactional(readOnly = true)
    public List<GroupPracticesDto> getPracticesIdByGroups(Long parentId) {
        ParentUser parent = getParent(parentId);
        Set<ParentToGroup> parentToGroups = parent.getParentToGroups();
        List<GroupPracticesDto> groupPracticesDtos = new ArrayList<>(parentToGroups.size());

        for (ParentToGroup parentToGroup : parentToGroups) {
            ParentsGroup group = parentToGroup.getParentsGroup();
            groupPracticesDtos.add(new GroupPracticesDto(group));
        }
        return groupPracticesDtos;
    }

    @Transactional(readOnly = true)
    public List<ChildPracticesDto> getPracticesIdByChilds(Long parentId) {
        ParentUser parent = getParent(parentId);
        List<ChildPracticesDto> childPracticesDtos = new ArrayList<>(parent.getChilds().size());
        for (ChildUser child : parent.getChilds()) {
            TypedQuery<Practice> directPracticeQuery = getEm().createQuery("select p from Practice p join p.childs c where c=:child", Practice.class);
            directPracticeQuery.setParameter("child", child);
            Map<Long, Practice> practiceIdToPracticeMap = new HashMap<>();
            for (Practice practice : directPracticeQuery.getResultList()) {
                practiceIdToPracticeMap.put(practice.getId(), practice);
            }

            String sql = "select ptg from PracticeToGroup ptg join fetch ptg.parentsGroup join ptg.parentsGroup grp join grp.childsToGroup ctg join ctg.childUser c where c=:child";
            TypedQuery<PracticeToGroup> groupPracticesQuery = getEm().createQuery(sql, PracticeToGroup.class);
            groupPracticesQuery.setParameter("child", child);
            List<PracticeToGroup> practiceToGroups = groupPracticesQuery.getResultList();
            for (PracticeToGroup practiceToGroup : practiceToGroups) {
                Practice groupPractice = practiceToGroup.getPractice();
                practiceIdToPracticeMap.put(groupPractice.getId(), groupPractice);
            }

            childPracticesDtos.add(new ChildPracticesDto(child, practiceIdToPracticeMap.values()));
        }
        return childPracticesDtos;
    }

    @Transactional(readOnly = true)
    public ParentProfileDto getParentProfile(Long parentId) {
        ParentUser parent = getParent(parentId);

        ParentProfileDto parentProfileDto = new ParentProfileDto();
        parentProfileDto.setFirstName(parent.getFirstName());
        parentProfileDto.setLastName(parent.getLastName());
        parentProfileDto.setProfileImageUrl(parent.getProfileImageUrlOrDefault());
        parentProfileDto.setChildCount(parent.getChilds().size());
        return new ParentProfileDto(parent);
    }

    public ParentProfileDto updateParentProfile(Long parentId, ParentProfileDto updateProfile) {
        ParentUser parent = getParent(parentId);
        parent.setFirstName(updateProfile.getFirstName());
        parent.setLastName(updateProfile.getLastName());
        parent.setEmail(updateProfile.getEmail());
        parent.setUserName(updateProfile.getEmail());

        parent.setPropertyValue(UserProperties.RECEIVE_DAILY_MAIL_KEY, updateProfile.isReceiveDailyMail());
        parent.setPropertyValue(UserProperties.DAILY_MAIL_HOUR_KEY, updateProfile.getDailyMailLocalHour());
        getEm().flush();
        return new ParentProfileDto(parent);
    }

    @Transactional(readOnly = true)
    public int getPracticesCount(Long parentId) {
        ParentUser parent = getParent(parentId);
        Set<Long> practicesIds = new HashSet<>();

        // practices shared by group the parent is member in
        for (ParentToGroup parentToGroup : parent.getParentToGroups()) {
            Set<PracticeToGroup> groupPractices = parentToGroup.getParentsGroup().getPractices();
            for (PracticeToGroup groupPractice : groupPractices) {
                practicesIds.add(groupPractice.getPractice().getId());
            }
        }

        // practices the parent create (may or may not shared with child/groups)
        for (Practice practice : parent.getCreatedPractices()) {
            practicesIds.add(practice.getId());
        }
        return practicesIds.size();
    }

    public ChildUser removeChild(Long parentId, Long childId) {
        ParentUser parent = getParent(parentId);
        for (ChildUser childUser : parent.getChilds()) {
            if (childUser.getId().equals(childId)) {
                childUser.getParents().remove(parent);
                parent.getChilds().remove(childUser);
                for (Practice practice : childUser.getPractices()) {
                    practice.getChilds().remove(childUser);
                }
                genericDao.getEm().remove(childUser);
                return childUser;
            }
        }
        return null;
    }

    public String resetPassword(ParentUser parent) {
        User existUser = getUserByMail(parent.getEmail());
        if (existUser == null) {
            throw new UserNotFoundException("user with email " + parent.getEmail() + " could not be found");
        }
        String newPasswordOpen = new BigInteger(130, new SecureRandom()).toString(32);
        existUser.setPassword(passwordEncoder.encode(newPasswordOpen));
        return newPasswordOpen;
    }
}
