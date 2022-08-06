package com.example.security.jwtstudy.web.index;

import com.example.security.jwtstudy.annotations.AuthMember;
import com.example.security.jwtstudy.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class IndexController {

    @GetMapping("/")
    public String indexPage(@AuthMember User user, Model model) {
        log.info("start index controller");
        if (user != null) {
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
