package com.practice.repository;

import com.google.common.collect.LinkedListMultimap;
import com.practice.dto.ChildPracticesSummaryDto;
import com.practice.dto.ChildPropertiesDto;
import com.practice.etc.error.LogicalException;
import com.practice.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * User: tomer
 */
@Repository("childRepository")
@Transactional
public class ChildRepository extends UserRepository {
    private static final Logger logger = Logger.getLogger(ChildRepository.class);

    @Transactional(readOnly = true)
    public ChildUser getChild(Long childId) {
        return genericDao.find(ChildUser.class, childId);
    }

    public ChildUser updateChild(ChildUser childUser) {
        ChildUser existUser = getEm().find(ChildUser.class, childUser.getId());
        if (existUser == null) {
            throw new LogicalException("child with id: " + childUser.getId() + " is not exist", "child.could.not.be.found");
        }

        if (!existUser.getUserName().equals(childUser.getUserName())) {
            long existUserName = genericDao.count(User.class, "where userName=?", childUser.getUserName());
            if (existUserName > 0) {
                throw new LogicalException("user name already exist", "username.already.exist");
            }
            existUser.setUserName(childUser.getUserName());
        }

        existUser.setFirstName(childUser.getFirstName());
        existUser.setLastName(childUser.getLastName());
        existUser.setGender(childUser.getGender());
        existUser.setDescription(childUser.getDescription());
        existUser.setEmail(StringUtils.trim(childUser.getEmail()));
        existUser.setBirthDate(childUser.getBirthDate());
        return existUser;
    }

    @Transactional(readOnly = true)
    public ChildPracticesSummaryDto getChildPracticesSummaryDto(Long childId) {
        ChildUser child = getChild(childId);

        TypedQuery<Practice> directPracticeQuery = getEm().createQuery("select p from Practice p join p.childs c where c=:child", Practice.class);
        directPracticeQuery.setParameter("child", child);
        List<Practice> directPractices = directPracticeQuery.getResultList();

        String sql = "select ptg from PracticeToGroup ptg join fetch ptg.parentsGroup join ptg.parentsGroup grp join grp.childsToGroup ctg join ctg.childUser c where c=:child";
        TypedQuery<PracticeToGroup> groupPracticesQuery = getEm().createQuery(sql, PracticeToGroup.class);
        groupPracticesQuery.setParameter("child", child);
        List<PracticeToGroup> practiceToGroups = groupPracticesQuery.getResultList();

        LinkedListMultimap<ParentsGroup, Practice> groupToPractices = LinkedListMultimap.create(practiceToGroups.size());
        for (PracticeToGroup practiceToGroup : practiceToGroups) {
            groupToPractices.put(practiceToGroup.getParentsGroup(), practiceToGroup.getPractice());
        }

        return new ChildPracticesSummaryDto(child, directPractices, groupToPractices);
    }

    @Transactional(readOnly = true)
    public ChildPropertiesDto getChildPropertiesDto(Long childId) {
        ChildUser child = getChild(childId);
        return new ChildPropertiesDto(child);
    }

    @Transactional
    public ChildPropertiesDto updateChild(Long childId, ChildPropertiesDto childPropertiesDto) {
        ChildUser childUser = getEm().find(ChildUser.class, childId);
        childUser.setFirstName(childPropertiesDto.getFirstName());
        childUser.setLastName(childPropertiesDto.getLastName());
        childUser.setProfileImageUrl(childPropertiesDto.getUserAvatar());
        childPropertiesDto.setChildId(childId);
        return childPropertiesDto;
    }

    @Transactional(readOnly = true)
    public boolean isChildOf(Long parentId) {
        Long childId = getUserDetails().getId();
        ParentUser parentUser = genericDao.find(ParentUser.class, parentId);
        if (parentUser != null) {
            for (ChildUser child : parentUser.getChilds()) {
                if (child.getId().equals(childId)) {
                    return true;
                }
            }
        }
        return false;
    }
}
