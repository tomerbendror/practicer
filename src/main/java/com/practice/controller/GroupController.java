package com.practice.controller;

import com.practice.dto.GenericResponseDto;
import com.practice.dto.group.ChildInGroupDto;
import com.practice.dto.group.GroupInvitesDto;
import com.practice.dto.group.ParentInGroupDto;
import com.practice.dto.group.ParentsGroupDto;
import com.practice.model.ParentsGroup;
import com.practice.model.User;
import com.practice.model.UserInGroupRole;
import com.practice.repository.GroupRepository;
import com.practice.security.UserDetails;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * User: tomer
 */
@Controller
@RequestMapping("/group")
public class GroupController extends BaseController {
    private static final Logger logger = Logger.getLogger(GroupController.class);

    @Autowired
    private GroupRepository groupRepository;

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<GenericResponseDto<ParentsGroupDto>> createGroup(@Valid @RequestBody ParentsGroup parentsGroup) {
        UserDetails parentDetails = getUserDetails();
        ParentsGroupDto createdGroup = groupRepository.createGroup(parentDetails.getId(), parentsGroup);
        return new ResponseEntity<>(GenericResponseDto.success(createdGroup), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("@groupRepository.isGroupManager(#parentsGroup.id)")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<ParentsGroupDto>> updateGroup(@Valid @RequestBody ParentsGroup parentsGroup) {
        UserDetails parentDetails = getUserDetails();
        ParentsGroupDto updatedGroup = groupRepository.updateGroup(parentDetails.getId(), parentsGroup);
        return new ResponseEntity<>(GenericResponseDto.success(updatedGroup), HttpStatus.OK);
    }

    @RequestMapping(value = "/{groupId}/childInGroup", method = RequestMethod.POST)
    @PreAuthorize("@groupRepository.isGroupMember(#groupId)")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<List<ChildInGroupDto>>> updateChildInGroup(@PathVariable("groupId") Long groupId, @Valid @RequestBody List<ChildInGroupDto> myChilds) {
        UserDetails parentDetails = getUserDetails();
        List<ChildInGroupDto> updatedChilds = groupRepository.updateChildInGroup(groupId, parentDetails.getId(), myChilds);
        return new ResponseEntity<>(GenericResponseDto.success(updatedChilds), HttpStatus.OK);
    }

    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    @PreAuthorize("@groupRepository.isGroupMember(#parentsGroup.id)")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<GroupInvitesDto>> inviteToGroup(@Valid @RequestBody ParentsGroup parentsGroup) {
        UserDetails parentDetails = getUserDetails();
        GroupInvitesDto groupInvitesDto = groupRepository.inviteToGroup(parentDetails.getId(), parentsGroup.getId(), parentsGroup.getGroupInvitations());
        return new ResponseEntity<>(GenericResponseDto.success(groupInvitesDto), HttpStatus.OK);
    }

    @RequestMapping(value = "/{groupId}/reInvite", method = RequestMethod.POST)
    @PreAuthorize("@groupRepository.isGroupManager(#groupId)")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<GroupInvitesDto>> reInviteToGroup(@PathVariable("groupId") Long groupId, @RequestBody List<User> emails) {
        UserDetails parentDetails = getUserDetails();
        GroupInvitesDto groupInvitesDto = groupRepository.reInviteToGroup(parentDetails.getId(), groupId, emails);
        return new ResponseEntity<>(GenericResponseDto.success(groupInvitesDto), HttpStatus.OK);
    }

    @RequestMapping(value = "/{groupId}/setManager", method = RequestMethod.POST)
    @PreAuthorize("@groupRepository.isGroupManager(#groupId)")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<ParentsGroupDto>> setParentAsManager(@PathVariable("groupId") Long groupId, @RequestParam("parentId") long toManagerId) {
        ParentsGroupDto parentsGroupDto = groupRepository.setParentInGroupRole(groupId, toManagerId, UserInGroupRole.MANAGER);
        return new ResponseEntity<>(GenericResponseDto.success(parentsGroupDto), HttpStatus.OK);
    }

    @RequestMapping(value = "/{groupId}/setTeacher", method = RequestMethod.POST)
    @PreAuthorize("@groupRepository.isGroupManager(#groupId)")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<ParentsGroupDto>> setParentAsTeacher(@PathVariable("groupId") Long groupId, @RequestParam("parentId") long toManagerId) {
        ParentsGroupDto parentsGroupDto = groupRepository.setParentInGroupRole(groupId, toManagerId, UserInGroupRole.TEACHER);
        return new ResponseEntity<>(GenericResponseDto.success(parentsGroupDto), HttpStatus.OK);
    }

    @RequestMapping(value = "/{groupId}/removeParents", method = RequestMethod.POST)
    @PreAuthorize("@groupRepository.isGroupManager(#groupId)")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<ParentsGroupDto>> removeParents(@PathVariable("groupId") Long groupId, @RequestBody ParentInGroupDto parentInGroupDto) {
        ParentsGroupDto parentsGroupDto = groupRepository.removeParents(groupId, parentInGroupDto);
        return new ResponseEntity<>(GenericResponseDto.success(parentsGroupDto), HttpStatus.OK);
    }

    @RequestMapping(value="/{groupId}", method = RequestMethod.DELETE)
    @PreAuthorize("@groupRepository.isGroupManager(#groupId)")
    @ResponseBody
    public ResponseEntity<GenericResponseDto<ParentsGroup>> removeGroup(@PathVariable("groupId") Long groupId) {
        ParentsGroup removeGroup = groupRepository.removeGroup(groupId);
        return new ResponseEntity<>(GenericResponseDto.success(removeGroup), HttpStatus.OK);
    }
}
