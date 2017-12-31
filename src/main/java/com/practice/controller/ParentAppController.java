package com.practice.controller;

import com.practice.dto.GenericResponseDto;
import com.practice.etc.UserSession;
import com.practice.model.*;
import com.practice.repository.GroupRepository;
import com.practice.repository.ParentRepository;
import com.practice.repository.UserRepository;
import com.practice.security.UserDetails;
import com.practice.util.PracticerEnvConfig;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User: tomer
 */
@Controller
@RequestMapping("/app/parent")
@SessionAttributes("userSession")
public class ParentAppController extends BaseController {

    public static final String DEFAULT_PARENT_APP_PATH = "/app/parent/childs";

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private PracticerEnvConfig practicerEnvConfig;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChildAppController childAppController;

    @PreAuthorize("hasRole('parent')")
    @RequestMapping(value="/**", method= RequestMethod.GET)
    public String getApp(Model model) {
        UserDetails userDetails = getUserDetails();
        model.addAttribute("userName", StringUtils.isNotBlank(userDetails.getFirstName()) ? userDetails.getFirstName() : userDetails.getUsername());
        model.addAttribute("fullName", userDetails.getFirstName() + " " + userDetails.getLastName());
        model.addAttribute("envName", practicerEnvConfig.getEnvName());
        String username = userDetails.getUsername().toLowerCase();
        model.addAttribute("devUser", username.contains("tomerbd0910@gmail.com") || username.contains("dganit.ben.dror@gmail.com") ? "true" : "false");//todo
        return "app/parent-app";
    }

    @PreAuthorize("hasRole('parent')")
    @RequestMapping(value="/importFromFile", method= RequestMethod.GET)
    public String getImportFromFile(Model model) {
        UserDetails userDetails = getUserDetails();
        model.addAttribute("title", "יצירת תרגיל מקובץ");
        model.addAttribute("fullName", userDetails.getFirstName() + " " + userDetails.getLastName());
        return "app/import-practice-from-file";
    }


    @PreAuthorize("@parentRepository.isParentOf(#childId)")
    @RequestMapping(value="/{parentId}/viewAsChild", method= RequestMethod.GET)
    public ResponseEntity viewAsChild(@RequestParam Long childId, @PathVariable Long parentId, UserSession userSession) {
        Long parentIdFromContext = getUserDetails().getId();

        // switch the parent session with the one of his child
        ChildUser child = userRepository.getUserById(childId);
        Authentication loginAuth = new UsernamePasswordAuthenticationToken(userRepository.getUserSecurityDetails(child.getActualUserName()), null, child.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(loginAuth);

        // return redirect
        String redirectUrl = childAppController.getAfterLoginRedirectPath(getUserDetails().getId(), null);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "child_gender=" + getUserDetails().getGender() + "; Path=/");
        userSession.setViewAsChildParentId(parentIdFromContext);
        return new ResponseEntity<>(GenericResponseDto.success(redirectUrl), headers, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('parent')")
    @RequestMapping(value="/connectToGroup", method= RequestMethod.GET)
    @Transactional
    public String connectToGroup(Model model, UserSession userSession,
                                 @RequestParam(value = "key") String invitationKey,
                                 @RequestParam Long groupId) {    // we can know the group id from the invitationKey, but we need to validate the code against the groupId
        UserDetails parentDetails = getUserDetails();
        ParentsGroup group = groupRepository.getGroup(groupId);
        ParentUser parent = parentRepository.getParent(parentDetails.getId());
        boolean parentWasAdded = groupRepository.addParentToGroup(parentDetails.getId(), group.getId(), invitationKey);
        model.addAttribute("groupName", group.getName());
        model.addAttribute("firstName", parentDetails.getFirstName());
        model.addAttribute("parentWasAdded", parentWasAdded);
        model.addAttribute("friendWithGender", parent.getGender() == Gender.FEMALE ? "חברה" : "חבר");//todo i18n
        model.addAttribute("skipUrl", ParentAppController.DEFAULT_PARENT_APP_PATH);

        List<GroupInvitation> unusedInvitations = groupRepository.getUnusedInvitations(parentDetails.getId());
        for (GroupInvitation unusedInvitation : unusedInvitations) {
            Hibernate.initialize(unusedInvitation.getParentsGroup());
            Hibernate.initialize(unusedInvitation.getInviter());
        }
        model.addAttribute("unusedInvitations", unusedInvitations);

        return "/app/user-connected-to-group";
    }

    @RequestMapping(value="/manageGroupInvitations", method=RequestMethod.GET)
    @Transactional
    public String manageGroupInvitations(Model model) {
        Long parentId = getUserDetails().getId();
        ParentUser parent = parentRepository.getParent(parentId);
        model.addAttribute("title", "ניהול הזמנות לקבוצות");    //todo i18n
        model.addAttribute("userDisplayName", parent.getDisplayName());
        model.addAttribute("skipUrl", ParentAppController.DEFAULT_PARENT_APP_PATH);
        List<GroupInvitation> unusedInvitations = groupRepository.getUnusedInvitations(parentId);
        for (GroupInvitation unusedInvitation : unusedInvitations) {
            Hibernate.initialize(unusedInvitation.getParentsGroup());
            Hibernate.initialize(unusedInvitation.getInviter());
        }
        model.addAttribute("unusedInvitations", unusedInvitations);
        return "/app/manage-group-invitations";
    }

    public String getAfterLoginRedirectPath(Long parentId, UserSession userSession) {
        if (userSession != null && userSession.getRedirectUrl() != null && !userSession.getRedirectUrl().trim().toLowerCase().endsWith("/app/parent".toLowerCase())) {
            return userSession.getRedirectUrl();
        }
        ParentUser parent = parentRepository.getParent(parentId);
        if (!groupRepository.getUnusedInvitations(parent.getId()).isEmpty()) {
            return "/app/parent/manageGroupInvitations";
        }
        return DEFAULT_PARENT_APP_PATH;
    }
}
