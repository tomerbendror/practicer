package com.practice.controller;

import com.practice.dto.*;
import com.practice.dto.group.ParentsGroupDto;
import com.practice.model.ChildUser;
import com.practice.model.ParentUser;
import com.practice.repository.ChildRepository;
import com.practice.repository.GroupRepository;
import com.practice.repository.ParentRepository;
import com.practice.security.UserDetails;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * User: tomer
 */
@Controller
@RequestMapping("/parent")
public class ParentController extends BaseController {
    private static final Logger logger = Logger.getLogger(ParentController.class);

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ChildRepository childRepository;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('parent')")
    @ResponseBody
    @Transactional(readOnly = true)
    public ParentUser getParent() {
        ParentUser parent = parentRepository.getParent(getUserDetails().getId());
        Hibernate.initialize(parent.getChilds());
        Hibernate.initialize(parent.getParentToGroups());
        Hibernate.initialize(parent.getCreatedPractices());
        return parent;
    }

    @RequestMapping(value="/summary", method = RequestMethod.GET)
    @PreAuthorize("hasRole('parent')")
    @ResponseBody
    @Transactional(readOnly = true)
    public ParentSummaryDto getParentSummary() {
        ParentUser parent = parentRepository.getParent(getUserDetails().getId());
        int practicesCount = parentRepository.getPracticesCount(parent.getId());
        String profileImageUrl = parent.getProfileImageUrlOrDefault();
        return new ParentSummaryDto(parent, practicesCount, profileImageUrl);
    }

    @RequestMapping(value="/addChild", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('parent')")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<ChildUser>> addChild(@Valid @RequestBody ChildUser childUser) {
        UserDetails parentDetails = getUserDetails();
        ChildUser createdChild = parentRepository.addChild(parentDetails.getId(), childUser);
        return new ResponseEntity<>(GenericResponseDto.success(createdChild), HttpStatus.OK);
    }

    @RequestMapping(value="/acceptInvitations", method = RequestMethod.POST)
    @PreAuthorize("hasRole('parent')")
    public String acceptInvitations(@Valid @RequestParam(required = false) List<Long> invitationsIds) {
        UserDetails parentDetails = getUserDetails();
        Long parentId = parentDetails.getId();
        if (invitationsIds != null) {
            groupRepository.acceptInvitations(parentId, invitationsIds);
        }
        return "redirect:" + ParentAppController.DEFAULT_PARENT_APP_PATH;
    }

    @RequestMapping(value="/updateChild", method = RequestMethod.POST)
    @PreAuthorize("@parentRepository.isParentOf(#childUser.id)")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<ChildUser>> updateChild(@Valid @RequestBody ChildUser childUser) {
        ChildUser updatedChild = childRepository.updateChild(childUser);
        return new ResponseEntity<>(GenericResponseDto.success(updatedChild), HttpStatus.OK);
    }

    @RequestMapping(value="/{parentId}/changeChildPw", method = RequestMethod.POST)
    @PreAuthorize("@parentRepository.isParentOf(#childUser.id)")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<ChildUser>> changeChildPassword(@RequestBody ChildUser childUser) {
        ChildUser updatedChild = childRepository.updatePassword(childUser);
        return new ResponseEntity<>(GenericResponseDto.success(updatedChild), HttpStatus.OK);
    }

    @RequestMapping(value="/removeChild/{childId}", method = RequestMethod.DELETE)
    @PreAuthorize("@parentRepository.isParentOf(#childId)")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<ChildUser>> removeChild(@PathVariable("childId") Long childId) {
        UserDetails parentDetails = getUserDetails();
        ChildUser removeChild = parentRepository.removeChild(parentDetails.getId(), childId);
        return new ResponseEntity<>(GenericResponseDto.success(removeChild), HttpStatus.OK);
    }

    @RequestMapping(value="/childs", method = RequestMethod.GET)
    @PreAuthorize("hasRole('parent')")
    @ResponseBody
    // todo add postAuthorize to verify that all child is the child of the specify parent
    public List<ChildUser> getChilds() {
        return parentRepository.getChilds(getUserDetails().getId());
    }

    @RequestMapping(value="/groups", method = RequestMethod.GET)
    @PreAuthorize("hasRole('parent')")
    @ResponseBody
    @Transactional(readOnly = true)
    // todo add postAuthorize to verify that all child are the child of the specify parent
    public List<ParentsGroupDto> getGroups() {
        Long userId = getUserDetails().getId();
        return groupRepository.getGroupDtos(userId);
    }

    @RequestMapping(value="/practices", method = RequestMethod.GET)
    @PreAuthorize("hasRole('parent')")
    @ResponseBody
    @Transactional(readOnly = true)
    public List<PracticeDto> getPractices() {
        Long parentId = getUserDetails().getId();
        return parentRepository.getPractices(parentId);
    }

    @RequestMapping(value="/practicesSummary", method = RequestMethod.GET)
    @PreAuthorize("hasRole('parent')")
    @ResponseBody
    @Transactional(readOnly = true)
    public List<PracticeSummaryDto> getPracticesSummary() {
        Long parentId = getUserDetails().getId();
        return parentRepository.getPracticesSummary(parentId);
    }

    @RequestMapping(value="/{parentId}/practicesByGroups", method = RequestMethod.GET)
    @PreAuthorize("@userRepository.isCurrentLoggedInUser(#parentId)")
    @ResponseBody
    @Transactional(readOnly = true)
    public List<GroupPracticesDto> getPracticesByGroups(@PathVariable("parentId") Long parentId) {
        return parentRepository.getPracticesIdByGroups(parentId);
    }

    @RequestMapping(value="/{parentId}/practicesByChilds", method = RequestMethod.GET)
    @PreAuthorize("@userRepository.isCurrentLoggedInUser(#parentId)")
    @ResponseBody
    @Transactional(readOnly = true)
    public List<ChildPracticesDto> getPracticesByChilds(@PathVariable("parentId") Long parentId) {
        return parentRepository.getPracticesIdByChilds(parentId);
    }

    @RequestMapping(value="/profile", method = RequestMethod.GET)
    @PreAuthorize("hasRole('parent')")
    @ResponseBody
    @Transactional(readOnly = true)
    public ParentProfileDto getParentProfile() {
        Long parentId = getUserDetails().getId();
        return parentRepository.getParentProfile(parentId);
    }

    @RequestMapping(value="/{parentId}/profile", method = RequestMethod.POST)
    @PreAuthorize("@userRepository.isCurrentLoggedInUser(#parentId)")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<ParentProfileDto>> updateParentProfile(@PathVariable("parentId") Long parentId,
                                                                                    @Valid @RequestBody ParentProfileDto parentProfileDto) {
        ParentUser parent = parentRepository.getParent(parentId);
        if (!parent.getEmail().equalsIgnoreCase(parentProfileDto.getEmail())) {
            if (parentRepository.getUserByMail(parentProfileDto.getEmail()) != null) {
                return new ResponseEntity<>(GenericResponseDto.<ParentProfileDto>failure("כתובת הדוא''ל כבר קיימת במערכת"), HttpStatus.INTERNAL_SERVER_ERROR);//todo i18n
            }
        }
        ParentProfileDto updatedProfile = parentRepository.updateParentProfile(parentId, parentProfileDto);
        return new ResponseEntity<>(GenericResponseDto.success(updatedProfile), HttpStatus.OK);
    }
}
