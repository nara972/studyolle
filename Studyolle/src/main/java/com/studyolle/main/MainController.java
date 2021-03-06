package com.studyolle.main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.studyolle.account.validator.CurrentAccount;
import com.studyolle.domain.Account;

@Controller
public class MainController {
	
	@GetMapping("/")
	public String home(@CurrentAccount Account account,Model model) {
		if(account !=null) { //인증을 한 사용자
			model.addAttribute(account);
		}
		return "index";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}

}
