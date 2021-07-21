package com.studyolle.account.validator;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.studyolle.domain.Account;

import lombok.Getter;

@Getter
//스프링 시큐리티가 다루는 유저 정보를 우리가 갖고 있는 유저 정보와 연동
public class UserAccount extends User { 
	
	private Account account;//우리가 갖고 있는 정보
	
	public UserAccount(Account account) {
		super(account.getNickname(),account.getPassword(),List.of(new SimpleGrantedAuthority("ROLE_USER")));
	    this.account=account;
	}

}
