package com.practice.controller;

import com.practice.dto.GenericResponseDto;
import com.practice.etc.OAuthSession;
import com.practice.etc.UserSession;
import com.practice.model.Gender;
import com.practice.model.ParentUser;
import com.practice.model.User;
import com.practice.repository.ParentRepository;
import com.practice.repository.UserRepository;
import com.practice.social.FacebookOauth;
import com.practice.social.GoogleOauth;
import com.practice.social.GoogleOauth.FlowType;
import com.practice.util.PracticerEnvConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.util.SocialAuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;

/**
 * Created by Tomer
 */
@Controller
@RequestMapping("/social")
@SessionAttributes({"OAuthSession", "userSession"})
public class SocialController extends BaseController {

    private static final Logger logger = Logger.getLogger(SocialController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParentAppController parentAppController;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private GoogleOauth googleOauth;

    @Autowired
    private FacebookOauth facebookOauth;

    @Autowired
    private PracticerEnvConfig practicerEnvConfig;

    @Autowired
    @Qualifier("authenticationManager")
    protected AuthenticationManager authenticationManager;

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String login(@RequestParam("id") String providerId, OAuthSession oAuthSession) throws Exception {
        String url;
        String successUrl = practicerEnvConfig.getBaseUrl() + "/social/authSuccess";
        oAuthSession.setRedirectUri(successUrl);
        oAuthSession.setProviderId(providerId);
        if ("google".equalsIgnoreCase(providerId)) {
            url = googleOauth.getAuthUrl(FlowType.EMAIL, oAuthSession);
        } else if ("facebook".equalsIgnoreCase(providerId)) {
            url = facebookOauth.getFBAuthUrl(oAuthSession);
        } else {
            SocialAuthConfig config = SocialAuthConfig.getDefault();
            config.load();
            SocialAuthManager manager = new SocialAuthManager();
            manager.setSocialAuthConfig(config);
            url = manager.getAuthenticationUrl(providerId, successUrl);
            oAuthSession.setAuthManager(manager);
        }
        return "redirect:" + url;
    }

    @RequestMapping(value="/authSuccess")
    @Transactional
    public ResponseEntity loginSuccess(@RequestParam(value = "code", required = false) String code, OAuthSession oAuthSession, UserSession userSession,
                               HttpServletRequest request, RedirectAttributes redirectAttributes, HttpServletResponse response) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        ParentUser parent;
        if ("google".equalsIgnoreCase(oAuthSession.getProviderId())) {
            Map<String, String> googleProfileData = googleOauth.getUserProfilePropMap(code, oAuthSession);
            parent = fromPropertiesMap(googleProfileData);
            parent.setProfileImageUrl(googleProfileData.get("picture"));

//        List<Contact> contactList = googleOauth.getContactList(code, oAuthSession);
//        for (Contact contact : contactList) {
//            System.out.println(contact.getDisplayName());
//        }
        } else if ("facebook".equalsIgnoreCase(oAuthSession.getProviderId())) {
            Map<String, String> fbProfileData = facebookOauth.getUserProfilePropMap(code, oAuthSession);
            parent = fromPropertiesMap(fbProfileData);
            parent.setProfileImageUrl(fbProfileData.get("profile_image_url"));
        } else {
            SocialAuthManager authManager = oAuthSession.getAuthManager();
            Map<String, String> params = SocialAuthUtil.getRequestParametersMap(request);
            if (params.get("error_code") != null) {
                logger.debug("Fail to connect to oauth provider: " + oAuthSession.getProviderId() + ", error_code: " + params.get("error_code") + ", error_message: " + params.get("error_message"));
                headers.add("Location", "/403");
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }
            parent = new ParentUser();
            AuthProvider provider = authManager.connect(params);
//            Profile userProfile = provider.getUserProfile();
//            parent.setEmail(userProfile.getEmail());
//            parent.setProfileImageUrl(userProfile.getProfileImageURL());
        }

        Long userId;
        if (StringUtils.isNotBlank(parent.getEmail())) {
            User user = userRepository.getUserByMail(parent.getEmail());
            if (user != null) {
                userId = user.getId();
                Authentication token = new UsernamePasswordAuthenticationToken(userRepository.getUserSecurityDetails(user.getActualUserName()), null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(token);

                if (StringUtils.isNotBlank(parent.getProfileImageUrl()) && StringUtils.isBlank(user.getProfileImageUrl())) {
                    userRepository.updateUserData(user, parent.getProfileImageUrl());
                }
            } else {
                parent.setPassword("socialLogin_" + new BigInteger(130, new SecureRandom()).toString(16));
                parentRepository.createParent(parent);
                userId = parent.getId();
                Authentication token = new UsernamePasswordAuthenticationToken(userRepository.getUserSecurityDetails(parent.getActualUserName()), null, parent.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(token);
                headers.add("Set-Cookie", "PARENT_SAW_WELCOME_MSG_COOKIE=false; Path=/");
            }
            String redirectUrl = parentAppController.getAfterLoginRedirectPath(getUserDetails().getId(), userSession);
            headers.add("Set-Cookie", "userId=" + userId + "; Path=/");
            headers.add("Location", redirectUrl);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "לא ניתן היה לקבל את כתובת המייל מספק השירות, יש להזין את הפרטים ידנית");
        }
        headers.add("Location", "/user/login");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    private ParentUser fromPropertiesMap(Map<String, String> propertiesMap) {
        ParentUser parent = new ParentUser();
        parent.setEmail(propertiesMap.get("email"));
        parent.setFirstName(propertiesMap.get("first_name"));
        parent.setLastName(propertiesMap.get("last_name"));
        parent.setGender(Gender.fromString(propertiesMap.get("gender")));
        return parent;
    }
}
