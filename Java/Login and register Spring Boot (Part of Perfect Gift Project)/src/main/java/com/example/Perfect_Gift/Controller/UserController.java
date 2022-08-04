package com.example.Perfect_Gift.Controller;

import com.example.Perfect_Gift.ServiceLayer.UsersService;
import com.example.Perfect_Gift.UserModel.UserModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UsersService usersService;

    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registerRequest", new UserModel());
        return "register_page.html";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        model.addAttribute("loginRequest", new UserModel());
        return "login_page.html";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserModel userModel) {
        System.out.println("reg req: " + userModel);
        UserModel registeredUser = usersService.registerUser(userModel.getLogin(),userModel.getPassword(),userModel.getEmail(),userModel.getNip(),userModel.getPhone_number());
        return registeredUser==null?"error_page":"redirect:/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute UserModel userModel, Model model) {
        System.out.println("log req: " + userModel);
        UserModel authenticatedUser = usersService.authenticateUser(userModel.getLogin(),userModel.getPassword());
        return authenticatedUser==null?"error_page":"/personal_account_page";
    }

}
//tokeny jwt lub ciasteczka - dla tokenow czas trwania i wygasniecie - ciasteczka bezpieczniejsze