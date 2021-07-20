package com.studyolle.account;

import org.springframework.transaction.annotation.Transactional;
import javax.validation.Valid;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.studyolle.domain.Account;
import com.studyolle.settings.Notifications;
import com.studyolle.settings.Profile;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService  {
	
	private final AccountRepository accountRepository;
	private final JavaMailSender javaMailSender;
	private final PasswordEncoder passwordEncoder;
	//private final AuthenticationManager authenticationManager;
	
	
	public Account processNewAccount(SignUpForm signUpForm) {
		Account newAccount=saveNewAccount(signUpForm);
		newAccount.generateEmailCheckToken();
		sendSignUpConfirmEmail(newAccount);
		return newAccount;
	}
	
	private Account saveNewAccount(@Valid SignUpForm signUpForm) {
		
		Account account=Account.builder()
				.email(signUpForm.getEmail())
				.nickname(signUpForm.getNickname())
				.password(passwordEncoder.encode(signUpForm.getPassword()))
				.studyCreatedByWeb(true)
				.studyEnrollmentResultByWeb(true)
				.studyUpdatedByWeb(true)
				.build();	
		return accountRepository.save(account);
		
	}
	
	public void sendSignUpConfirmEmail(Account newAccount) {
		SimpleMailMessage mailMessage=new SimpleMailMessage();
		mailMessage.setTo(newAccount.getEmail());
		mailMessage.setSubject("스터디올래, 회원 가입 인증");
		mailMessage.setText("/check-email-token?token="+newAccount.getEmailCheckToken()+
				"&email="+newAccount.getEmail());
		javaMailSender.send(mailMessage);
	}

	public void login(Account account) {
		UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(
				new UserAccount(account),
				account.getPassword(),
				List.of(new SimpleGrantedAuthority("ROLE_USER")));
		SecurityContextHolder.getContext().setAuthentication(token);
		
		//UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(
		//		username, password);
		//Authentication authentication=authenticationManager.authenticate(token);
		//SecurityContext context=SecurityContextHolder.getContext();
		//context.setAuthentication(token);
		
	}
	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
		Account account=accountRepository.findByEmail(emailOrNickname);
		if(account==null) {
			account=accountRepository.findByNickname(emailOrNickname);
		}
		if(account==null) {
			throw new UsernameNotFoundException(emailOrNickname);
		}
		return new UserAccount(account);
	}

	public void completeSignUp(Account account) {
		account.completeSignUp();
		login(account);		
	}

	public void updateProfile(Account account, @Valid Profile profile) {
		account.setUrl(profile.getUrl());
		account.setOccupation(profile.getOccupation());
		account.setLocation(profile.getLocation());
		account.setBio(profile.getBio());
		account.setProfileImage(profile.getProfileImage());
		accountRepository.save(account);
	}

	public void updatePassword(Account account, String newPassword) {
		account.setPassword(passwordEncoder.encode(newPassword));
		accountRepository.save(account);
	}

	public void updateNotifications(Account account, @Valid Notifications notifications) {
		account.setStudyCreatedByEmail(notifications.isStudyCreatedByEmail());
		account.setStudyCreatedByWeb(notifications.isStudyCreatedByWeb());
		account.setStudyEnrollmentResultByEmail(notifications.isStudyEnrollmentResultByEmail());
		account.setStudyEnrollmentResultByWeb(notifications.isStudyEnrollmentResultByWeb());
		account.setStudyUpdatedByEmail(notifications.isStudyUpdatedByEmail());
		account.setStudyUpdatedByWeb(notifications.isStudyUpdatedByWeb());
		accountRepository.save(account);
	}
	

}
