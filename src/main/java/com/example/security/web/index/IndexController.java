package com.example.security.web.index;

import com.example.security.config.CustomUser;
import com.example.security.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Slf4j
@Controller
public class IndexController {

    @GetMapping("/")
    public String indexPage(@AuthenticationPrincipal CustomUser customUser, Model model) {
        log.info("start index controller");
        if (customUser != null) {
            User user = customUser.getUser();
            log.info("user id : {}", user.getId());
            log.info("user email : {}", user.getEmail());
            log.info("user password : {}", user.getPassword());
            log.info("user name : {}", user.getName());
            model.addAttribute(user);
        }
        log.info("end index controller");
        return "index";
    }
}
