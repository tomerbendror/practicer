package com.practice.controller;

import com.practice.model.ChildUser;
import com.practice.model.ParentUser;
import com.practice.model.User;
import com.practice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User: tomer
 */
@Controller
@RequestMapping("/app")
public class AppController extends BaseController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method= RequestMethod.GET)
    public String getChildApp() {
        Long userId = getUserDetails().getId();
        User user = userRepository.getUserById(userId);
        if (user instanceof ParentUser) {
            return "redirect:" + ParentAppController.DEFAULT_PARENT_APP_PATH;
        } else if (user instanceof ChildUser) {
            return "redirect:" + ChildAppController.DEFAULT_CHILD_APP_PATH;
        }
        return "redirect:/";
    }
}
