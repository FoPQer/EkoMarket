package com.example.ecomarket.controller;

import com.example.ecomarket.domain.Role;
import com.example.ecomarket.domain.User;
import com.example.ecomarket.repos.UserRepos;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    private final UserRepos userRepos;
    private User actionUser;

    public UserController(UserRepos userRepos) {
        this.userRepos = userRepos;
    }

    @GetMapping
    public String userList(Map<String, Object> model){
        actionUser = userRepos.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        model.put("users", userRepos.findAll());
        model.put("username", actionUser.getUsername());
        model.put("admin", actionUser.getRoles().contains(Role.ADMIN));

        return "userList";
    }

    @GetMapping("{user}")
    public String edit(@PathVariable User user, Map<String, Object> model){
        actionUser = userRepos.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        model.put("user", user);
        model.put("roles", Role.values());
        model.put("admin", actionUser.getRoles().contains(Role.ADMIN));
        model.put("username", actionUser.getUsername());

        return "userEdit";
    }

    @PostMapping
    public String saveEdit(@RequestParam String username,
                    @RequestParam Map<String, String> form,
                    @RequestParam("userId") User user){
        user.setUsername(username);

        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key : form.keySet()){
            if (roles.contains(key))
                user.getRoles().add(Role.valueOf(key));
        }

        userRepos.save(user);

        return "redirect:/user";
    }
}
