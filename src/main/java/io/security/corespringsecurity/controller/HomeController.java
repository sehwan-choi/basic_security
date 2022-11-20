package io.security.corespringsecurity.controller;


import io.security.corespringsecurity.domain.User;
import io.security.corespringsecurity.security.SecurityUser;
import io.security.corespringsecurity.service.UserService;
import io.security.corespringsecurity.service.dto.UserDto;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class HomeController {

	@Setter(onMethod_ = @Autowired)
	private UserService userService;

	@Setter(onMethod_ = @Autowired)
	private PasswordEncoder passwordEncoder;

	@GetMapping(value="/")
	public String home() throws Exception {
		return "home";
	}

	@PostMapping(value="/")
	public String postHome() throws Exception {
		return "home";
	}

	@GetMapping(value="/users")
	public String createUser() throws Exception {

		return "user/login/register";
	}

	@PostMapping("/users")
	public String createUser(UserDto userDto) {

		ModelMapper mapper = new ModelMapper();
		User user = mapper.map(userDto, User.class);
		user.encodePassword(passwordEncoder);

		userService.createUser(user);
		return "redirect:/";
	}

	@GetMapping("/login")
	public String login(@RequestParam(value = "error", required = false) String error,
						@RequestParam(value = "exception", required = false) String exception,
						Model model) {
		model.addAttribute("error", error);
		model.addAttribute("exception", exception);

		return "user/login/login";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {
			new SecurityContextLogoutHandler().logout(request, response, authentication);
		}

		return "redirect:/";
	}

	@GetMapping("/denied")
	public String accessDenied(@RequestParam(value = "exception", required = false) String exception, Model model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if ( !principal.equals("anonymousUser")) {
			SecurityUser securityUser = (SecurityUser) principal;
			model.addAttribute("username", securityUser.getUsername());
		}
		model.addAttribute("exception" , exception);
		return "user/login/denied";
	}

	@GetMapping("/messages")
	public String message() {
		return "user/messages";
	}

	@GetMapping("/api/messages")
	@ResponseBody
	public String apiMessage() {
		return "message ok";
	}
}
