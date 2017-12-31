package com.practice.controller;

import com.practice.etc.UserSession;
import com.practice.model.ChildUser;
import com.practice.model.Gender;
import com.practice.model.ParentUser;
import com.practice.repository.ChildRepository;
import com.practice.repository.UserRepository;
import com.practice.util.PracticerEnvConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Created by Tomer
 */
@Controller
@RequestMapping("/app/child")
@SessionAttributes("userSession")
public class ChildAppController extends BaseController {

    public static final String DEFAULT_CHILD_APP_PATH = "/app/child/all-practices";

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParentAppController parentAppController;

    @PreAuthorize("hasRole('child')")
    @RequestMapping(value="/**", method= RequestMethod.GET)
    public String getChildApp(Model model, UserSession userSession) {
        ChildUser child = childRepository.getChild(getUserDetails().getId());
        model.addAttribute("gender", child.getGender().name());
        model.addAttribute("envName", practicerEnvConfig.getEnvName());
        model.addAttribute("childId", child.getId());
        boolean parentViewAsChild = userSession.getViewAsChildParentId() != null;
        model.addAttribute("parentViewAsChild", parentViewAsChild);
        if (parentViewAsChild) {
            if (child.getGender() == Gender.FEMALE) {
                model.addAttribute("switchToParentMsg", "הינך צופה כרגע בממשק הילדה, ע\"מ לחזור לממשק ההורים ");
            } else {
                model.addAttribute("switchToParentMsg", "הינך צופה כרגע בממשק הילד, ע\"מ לחזור לממשק ההורים ");
            }

        }
        return "/childApp/child-app";
    }

    @RequestMapping(value="/{childId}/returnToParentView", method= RequestMethod.GET)
    public String returnToParentView(@PathVariable Long childId, UserSession userSession) {
        Long viewAsChildParentId = userSession.getViewAsChildParentId();
        if (viewAsChildParentId == null || !childRepository.isChildOf(viewAsChildParentId)) {
            return "redirect:/app";
        }
        userSession.setViewAsChildParentId(null);

        // switch the child session with the parent
        ParentUser parent = userRepository.getUserById(viewAsChildParentId);
        Authentication loginAuth = new UsernamePasswordAuthenticationToken(userRepository.getUserSecurityDetails(parent.getActualUserName()), null, parent.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(loginAuth);

        // return redirect
        String redirectUrl = parentAppController.getAfterLoginRedirectPath(getUserDetails().getId(), null);
        return "redirect:" + redirectUrl;
    }

    @Autowired
    private PracticerEnvConfig practicerEnvConfig;

    public String getAfterLoginRedirectPath(Long childId, UserSession userSession) {
        if (userSession != null && userSession.getRedirectUrl() != null) {
            return userSession.getRedirectUrl();
        }
        return DEFAULT_CHILD_APP_PATH;
    }
}
