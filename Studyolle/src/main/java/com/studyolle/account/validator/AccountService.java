package com.studyolle.account.validator;

import org.springframework.transaction.annotation.Transactional;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
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

import com.studyolle.account.form.SignUpForm;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.settings.form.Notifications;
import com.studyolle.settings.form.Profile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService  {
	
	private final AccountRepository accountRepository;
	private final JavaMailSender javaMailSender;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;
	//private final AuthenticationManager authenticationManager;
	
	
	public Account processNewAccount(SignUpForm signUpForm) {
		Account newAccount=saveNewAccount(signUpForm);
		sendSignUpConfirmEmail(newAccount);
		return newAccount;
	}
	
	private Account saveNewAccount(@Valid SignUpForm signUpForm) {
		signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
		Account account=modelMapper.map(signUpForm,Account.class);
		account.generateEmailCheckToken();
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

	public void updateProfile(Account account,Profile profile) {
		modelMapper.map(profile, account);
		accountRepository.save(account);
	}

	public void updatePassword(Account account, String newPassword) {
		account.setPassword(passwordEncoder.encode(newPassword));
		accountRepository.save(account);
	}

	public void updateNotifications(Account account, @Valid Notifications notifications) {
		modelMapper.map(notifications, account);
		accountRepository.save(account);
	}

	public void updateNickname(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account);

        // login메소드를 추가해주는 이유는 navigation에서 dropdown에서의 nickname이 바뀌도록 해주기 위함이다.
        login(account);
    }

	public void sendLoginLink(Account account) {
		account.generateEmailCheckToken();
		SimpleMailMessage mailMessage=new SimpleMailMessage();
		mailMessage.setTo(account.getEmail());
		mailMessage.setSubject("스터디 올래, 로그인 링크");
		mailMessage.setText("/login-by-email?token="+account.getEmailCheckToken()+
				 "&email="+account.getEmail());
		javaMailSender.send(mailMessage);
		
	}

	// account엔티티는 Detached 상태 이기때문에 account를 먼저 읽어 온다
	public void addTag(Account account, Tag tag) {
	     Optional<Account> byId = accountRepository.findById(account.getId());
	     byId.ifPresent(a -> a.getTags().add(tag));
	    }
	
	public Set<Tag> getTags(Account account){
		Optional<Account> byId=accountRepository.findById(account.getId());
		return byId.orElseThrow().getTags();
	}

	public void removeTag(Account account, Tag tag) {
		 Optional<Account> byId = accountRepository.findById(account.getId());
	     byId.ifPresent(a -> a.getTags().remove(tag));
	}
	
}
