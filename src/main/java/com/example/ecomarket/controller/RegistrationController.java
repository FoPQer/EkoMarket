package com.example.ecomarket.controller;

import com.example.ecomarket.domain.Role;
import com.example.ecomarket.domain.User;
import com.example.ecomarket.repos.UserRepos;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {
    private final UserRepos userRepos;

    public RegistrationController(UserRepos userRepos) {
        this.userRepos = userRepos;
    }

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Map<String, Object> model){
        User userFromDB = userRepos.findByUsername(user.getUsername());

        if(userFromDB != null) {
            model.put("message", "User exist!");
            return "registration";
        }

        user.setRoles(Collections.singleton(Role.USER));
        userRepos.save(user);

        return "redirect:/login";
    }
}
