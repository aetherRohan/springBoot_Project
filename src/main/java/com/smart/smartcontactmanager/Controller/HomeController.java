package com.smart.smartcontactmanager.Controller;

import com.smart.smartcontactmanager.Dao.UserRepository;
import com.smart.smartcontactmanager.Entities.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home-Smart Contact Manager");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About-Smart Contact Manager");
        return "about";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Registration- Smart Contact Manager");
        model.addAttribute("user",new User());
        return "signup";
    }

    @PostMapping("/do_signup")
    public String do_SignUp(@Valid @ModelAttribute("user") User user,BindingResult result,
                             @RequestParam(value = "checkbox",
            defaultValue = "false") boolean checkbox, Model model) {

        if (result.hasErrors()) {
            return "signup";
        }
        if (!checkbox){
            model.addAttribute("error","You must Accept terms and Conditions");
            return "signup";
        }
        user.setRole("ROLE_USER");
        user.setEnabled(true);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        this.userRepository.save(user);
        model.addAttribute("user",user);

        return "success";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("title","Smart Contact Manager-Login");
        return "login";
    }
}