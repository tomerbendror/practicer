package com.practice.controller;

import com.practice.security.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

/**
 * User: tomer
 */
@Controller
public class SecurityController {
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public ModelAndView accessDenied(Principal user) {
        ModelAndView model = new ModelAndView();

        if (user != null) {
            String msg = "שלום " + user.getName() + ", נראה כי אין לך את ההרשאות המתאימות לצפייה בדף זה!";
            model.addObject("msg", msg);//todo i18n
            UserDetails userDetails = (UserDetails) ((UsernamePasswordAuthenticationToken) user).getPrincipal();
            if (userDetails.isParent()) {
                model.addObject("subtitle", "אם ברצונך להגיע לאתר הילדים לחץ ");//todo i18n
            } else {    // child
                model.addObject("subtitle", "אם ברצונך להגיע לאתר ההורים לחץ ");//todo i18n
            }
            model.addObject("homeUrl", "/app");
        } else {
            model.addObject("msg", "נראה כי אין לך את ההרשאות המתאימות לצפייה בדף זה!");//todo i18n
            model.addObject("homeUrl", "/index.html");
        }

        model.setViewName("403");
        return model;

    }
}
