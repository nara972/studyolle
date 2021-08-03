package com.studyolle.account.validator;

import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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
import com.studyolle.config.AppProperties;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.mail.EmailMessage;
import com.studyolle.mail.EmailService;
import com.studyolle.settings.form.Notifications;
import com.studyolle.settings.form.Profile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

	private final AccountRepository accountRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;
	private final TemplateEngine templateEngine;
	private final AppProperties appProperties;
	

	public Account processNewAccount(SignUpForm signUpForm) {
		Account newAccount = saveNewAccount(signUpForm);
		sendSignUpConfirmEmail(newAccount);
		return newAccount;
	}

	private Account saveNewAccount(@Valid SignUpForm signUpForm) {
		signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
		Account account = modelMapper.map(signUpForm, Account.class);
		account.generateEmailCheckToken();
		return accountRepository.save(account);
	}

	public void sendSignUpConfirmEmail(Account newAccount) {
		Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" +
                newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail());
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "스터디올래 서비스를 사용하려면 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());

        // mail/simple-link.html을 String타입으로 생성
        String message = templateEngine.process("mail/simple-link", context);
		
		EmailMessage emailMessage = EmailMessage.builder()
				.to(newAccount.getEmail())
				.subject("스터디올래, 회원 가입 성공")
				.message(message)
				.build();
		
		emailService.sendEmail(emailMessage);
	}

	public void login(Account account) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new UserAccount(account),
				account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
		SecurityContextHolder.getContext().setAuthentication(token);

		// UsernamePasswordAuthenticationToken token=new
		// UsernamePasswordAuthenticationToken(
		// username, password);
		// Authentication authentication=authenticationManager.authenticate(token);
		// SecurityContext context=SecurityContextHolder.getContext();
		// context.setAuthentication(token);

	}

	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
		Account account = accountRepository.findByEmail(emailOrNickname);
		if (account == null) {
			account = accountRepository.findByNickname(emailOrNickname);
		}
		if (account == null) {
			throw new UsernameNotFoundException(emailOrNickname);
		}
		return new UserAccount(account);
	}

	public void completeSignUp(Account account) {
		account.completeSignUp();
		login(account);
	}

	public void updateProfile(Account account, Profile profile) {
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
		Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" +
        		account.getEmailCheckToken() + "&email=" + account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "이메일로 로그인하기");
        context.setVariable("message", "로그인 하려면 아래 링크를 클릭하세요");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);
		
		EmailMessage emailMessage = EmailMessage.builder()
				.to(account.getEmail())
				.subject("스터디올래, 로그인 링크")
				.message(message)
				.build();
		emailService.sendEmail(emailMessage);
	}

	// account엔티티는 Detached 상태 이기때문에 account를 먼저 읽어 온다
	public void addTag(Account account, Tag tag) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		byId.ifPresent(a -> a.getTags().add(tag));
	}

	public Set<Tag> getTags(Account account) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		return byId.orElseThrow().getTags();
	}

	public void removeTag(Account account, Tag tag) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		byId.ifPresent(a -> a.getTags().remove(tag));
	}

	public Set<Zone> getZones(Account account) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		return byId.orElseThrow().getZones();
	}

	public void addZone(Account account, Zone zone) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		byId.ifPresent(a -> a.getZones().add(zone));
	}

	public void removeZone(Account account, Zone zone) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		byId.ifPresent(a -> a.getZones().remove(zone));
	}
	
	public Account getAccount(String nickname) {
		Account account=accountRepository.findByNickname(nickname);
		if(account==null) {
			throw new IllegalArgumentException(nickname+"에 해당하는 사용자가 없습니다.");
		}
		return account;
	}

}
