package com.studyolle.settings;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;

@Controller
public class SettingsController {
	
	@GetMapping("/settings/profile")
	public String profileUpdateForm(@CurrentUser Account account,Model model) {
		model.addAttribute(account);
		model.addAttribute(new Profile(account));
		return "settings/profile";
	}

}