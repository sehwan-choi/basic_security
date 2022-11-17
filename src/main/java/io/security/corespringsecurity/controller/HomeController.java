package io.security.corespringsecurity.controller;


import io.security.corespringsecurity.domain.User;
import io.security.corespringsecurity.service.UserService;
import io.security.corespringsecurity.service.dto.UserDto;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
	public String login() {
		return "user/login/login";
	}
}
