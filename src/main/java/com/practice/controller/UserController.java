package com.practice.controller;

import com.practice.dto.GenericResponseDto;
import com.practice.etc.UserSession;
import com.practice.etc.error.UserNotFoundException;
import com.practice.model.ParentUser;
import com.practice.model.User;
import com.practice.repository.ParentRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: tomer
 */
@Controller
@RequestMapping("/user")
@SessionAttributes("userSession")
public class UserController extends BaseController {

    private static final Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ParentAppController parentAppController;

    @Autowired
    private ChildAppController childAppController;

    @Autowired
    private EmailController emailController;

    @Autowired
    @Qualifier("authenticationManager")
    protected AuthenticationManager authenticationManager;

    @RequestMapping(value="/signup", method=RequestMethod.GET)
    public String signup(Model model) {
        return loginSignUp(model, "signUpActive", "רישום הורה", null);
    }

    @RequestMapping(value="/login", method=RequestMethod.GET)
    public String login(HttpServletRequest request, Model model, UserSession userSession) {
        String requestUrl = getRedirectUrl(request);
        userSession.setRedirectUrl(requestUrl);

        // if we have the mail of the invited parent from the invitation url, we help him and put it in the signup form
        String email = null;
        if (StringUtils.isNotBlank(requestUrl)) {
            Pattern p = Pattern.compile("[?&]email=(.+)[?&]*");
            Matcher m = p.matcher(requestUrl);
            if (m.find()) {
                email = m.group(1);
            }

        }
        return loginSignUp(model, "signInActive", "כניסה", email);
    }

    protected String getRedirectUrl(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            SavedRequest savedRequest = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
            return savedRequest != null ? savedRequest.getRedirectUrl() : null;
        }
        return null;
    }

    @RequestMapping(value="/resetPassword", method=RequestMethod.GET)
    public String resetPassword(Model model) {
        ParentUser user = new ParentUser();
        model.addAttribute("user", user);
        model.addAttribute("title", "איפוס סיסמה");
        model.addAttribute("afterSend", false);
        return "resetPassword";
    }

    @RequestMapping(value="/resetPassword", method=RequestMethod.POST)
    public String resetPassword(@ModelAttribute("user") ParentUser parent , Model model) {
        try {
            String newPasswordOpen = parentRepository.resetPassword(parent);
            emailController.setResetPasswordEmail(parent, newPasswordOpen);
            model.addAttribute("title", "הסיסמה אופסה");
            model.addAttribute("afterSend", true);
        } catch (UserNotFoundException e) {
            model.addAttribute("title", "משתמש לא קיים");
            model.addAttribute("userNotFound", true);
        }
        return "resetPassword";
    }

    @RequestMapping(value="/contactUs", method=RequestMethod.POST)
    public ResponseEntity contactUs(@RequestParam(required = false) String name, @RequestParam(required = false) String email,
                                    @RequestParam(required = false) String message) {
        emailController.sendDebugMail("fullName: " + name + ", email: " + email + ", message: " + message);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    private String loginSignUp(Model model, String activeTab, String title, String email) {
        User user = new User();
        if (StringUtils.isNotBlank(email)) {
            user.setEmail(email);
        }
        model.addAttribute("user", user);
        model.addAttribute(activeTab, "active");
        model.addAttribute("title", title);

        Object errorMsg = model.asMap().get("errorMsg");
        model.addAttribute("errorPanelDisplay", (errorMsg == null ? "none" : "block"));

        return "loginSignup";
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity login(@RequestParam String userName, @RequestParam String password, UserSession userSession) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
        try {
            Authentication auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", "userId=" + getUserDetails().getId() + "; Path=/");
            if (getUserDetails().isParent()) {
                headers.add("Set-Cookie", "PARENT_SAW_WELCOME_MSG_COOKIE=false; Path=/");
                String redirectUrl = parentAppController.getAfterLoginRedirectPath(getUserDetails().getId(), userSession);
                return new ResponseEntity<>(GenericResponseDto.success(redirectUrl), headers, HttpStatus.OK);
            } else if (getUserDetails().isChild()) {
                String redirectUrl = childAppController.getAfterLoginRedirectPath(getUserDetails().getId(), userSession);
                headers.add("Set-Cookie", "child_gender=" + getUserDetails().getGender() + "; Path=/");
                return new ResponseEntity<>(GenericResponseDto.success(redirectUrl), headers, HttpStatus.OK);
            }
            return new ResponseEntity<>(GenericResponseDto.success("/403"), HttpStatus.OK);
        } catch (AuthenticationException ex) {
            logger.error("login fail", ex);
            return new ResponseEntity<>(GenericResponseDto.failure("פרטי הכניסה שגויים, נא ודאו פרטי התחברות"), HttpStatus.OK);
        }
    }

    @RequestMapping(value="/signup", method=RequestMethod.POST)
    @ResponseBody
    public GenericResponseDto<String> signup(@Valid @ModelAttribute("user") ParentUser parent, UserSession userSession, BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            return  GenericResponseDto.failure("היתה בעיה ברישום, אנא נסו שנית");//todo i18n
        } else if (parentRepository.exist(parent.getEmail(), parent.getUserName())) {
            return GenericResponseDto.failure("שם המשתמש או האימייל כבר קיימים במערכת");//todo i18n
        } else {
            String openPassword = parent.getPassword();
            parentRepository.createParent(parent);
            authenticateUserAndSetSession(parent.getEmail(), openPassword, request);     // auto login after signup
            String redirectUrl = parentAppController.getAfterLoginRedirectPath(getUserDetails().getId(), userSession);
            return GenericResponseDto.success(redirectUrl);
        }
    }

    void authenticateUserAndSetSession(String username, String password, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

        // generate session if one doesn't exist
        request.getSession();

        token.setDetails(new WebAuthenticationDetails(request));
        Authentication authenticatedUser = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
    }
}
