package io.security.corespringsecurity.aopsecurity;

import io.security.corespringsecurity.service.dto.UserDto;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class AopSecurityController {

    @Setter(onMethod_ = @Autowired)
    private AopMethodService aopMethodService;

    @GetMapping("/preAuthorize")
    @PreAuthorize("hasRole('ROLE_USER') and #user.username == principal.username")
    public String preAuthorize(UserDto user, Model model, Principal principal) {
        model.addAttribute("method", "Success @PreAuthorize");

        return "aop/method";
    }

    @GetMapping("/methodSecured")
    public String methodSecured(Model model) {
        aopMethodService.methodSecured();
        model.addAttribute("method", "Success MethodSecured");
        return "aop/method";
    }
}
