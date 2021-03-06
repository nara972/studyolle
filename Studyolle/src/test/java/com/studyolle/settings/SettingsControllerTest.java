package com.studyolle.settings;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.WithAccount;
import com.studyolle.account.validator.AccountRepository;
import com.studyolle.account.validator.AccountService;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.settings.form.TagForm;
import com.studyolle.tag.TagRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SettingsControllerTest {
	/*
	@Autowired MockMvc mockMvc;
	@Autowired AccountRepository accountRepository;
	@Autowired AccountService accountService;
	@Autowired PasswordEncoder passwordEncoder;
	@Autowired TagRepository tagRepository;
	@Autowired ObjectMapper objectMapper;
	
	@AfterEach
	void afterEach() {
		accountRepository.deleteAll();
	}
	
	  @WithAccount("keesun")
	  @DisplayName("프로필 수정 폼")
	  @Test
	  void updateProfileForm() throws Exception {
	       mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
	              .andExpect(status().isOk())
	              .andExpect(model().attributeExists("account"))
	              .andExpect(model().attributeExists("profile"));
	    }
	
	@WithAccount("keesun")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account keesun = accountRepository.findByNickname("keesun");
        assertEquals(bio, keesun.getBio());
        
    }
	
	 @WithAccount("keesun")
	 @DisplayName("프로필 수정하기 - 입력값 에러")
	 @Test
	 void updateProfile_error() throws Exception {
	        String bio = "길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 너무나도 길게 소개를 수정하는 경우. ";
	        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
	                .param("bio", bio)
	                .with(csrf()))
	                .andExpect(status().isOk())
	                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
	                .andExpect(model().attributeExists("account"))
	                .andExpect(model().attributeExists("profile"))
	                .andExpect(model().hasErrors());

	        Account keesun = accountRepository.findByNickname("keesun");
	        assertNull(keesun.getBio());
	    }
	 
	 @WithAccount("keesun")
	    @DisplayName("패스워드 수정 폼")
	    @Test
	    void updatePassword_form() throws Exception {
	        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
	                .andExpect(status().isOk())
	                .andExpect(model().attributeExists("account"))
	                .andExpect(model().attributeExists("passwordForm"));
	    }

	    @WithAccount("keesun")
	    @DisplayName("패스워드 수정 - 입력값 정상")
	    @Test
	    void updatePassword_success() throws Exception {
	        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
	                .param("newPassword", "12345678")
	                .param("newPasswordConfirm", "12345678")
	                .with(csrf()))
	                .andExpect(status().is3xxRedirection())
	                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
	                .andExpect(flash().attributeExists("message"));

	        Account keesun = accountRepository.findByNickname("keesun");
	        assertTrue(passwordEncoder.matches("12345678", keesun.getPassword()));
	    }

	    @WithAccount("keesun")
	    @DisplayName("패스워드 수정 - 입력값 에러 - 패스워드 불일치")
	    @Test
	    void updatePassword_fail() throws Exception {
	        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
	                .param("newPassword", "12345678")
	                .param("newPasswordConfirm", "11111111")
	                .with(csrf()))
	                .andExpect(status().isOk())
	                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
	                .andExpect(model().hasErrors())
	                .andExpect(model().attributeExists("passwordForm"))
	                .andExpect(model().attributeExists("account"));
	    }
	    
	    @WithAccount("keesun")
	    @DisplayName("닉네임 수정 폼")
	    @Test
	    public void updateAccountForm() throws Exception {
	        mockMvc.perform(get("/settings/account"))
	                .andExpect(status().isOk())
	                .andExpect(model().attributeExists("account"))
	                .andExpect(model().attributeExists("nicknameForm"));

	    }

	    @WithAccount("keesun")
	    @DisplayName("닉네임 수정하기 - 입력값 정상")
	    @Test
	    public void updateAccount_success() throws Exception {
	        String newNickname = "weekbelt";
	        mockMvc.perform(post("/settings/account")
	                .param("nickname", newNickname)
	                .with(csrf()))
	                .andExpect(status().is3xxRedirection())
	                .andExpect(redirectedUrl("/settings/account"))
	                .andExpect(flash().attributeExists("message"));

	        assertThat(accountRepository.findByNickname(newNickname)).isNotNull();

	    }
	    @WithAccount("keesun")
	    @DisplayName("계정의 태그 수정 폼")
	    @Test
	    public void updateTagForm() throws Exception {
	        mockMvc.perform(get("/settings/tags"))
	                .andExpect(view().name("settings/tags"))
	                .andExpect(model().attributeExists("account"))
	                .andExpect(model().attributeExists("whitelist"))
	                .andExpect(model().attributeExists("tags"));
	    }

	    @WithAccount("keesun")
	    @DisplayName("계정에 태그 추가")
	    @Test
	    public void addTag() throws Exception {
	        TagForm tagForm = new TagForm();
	        tagForm.setTagTitle("newTag");

	        mockMvc.perform(post("/settings/tags/add")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(tagForm))
	                .with(csrf()))
	                .andExpect(status().isOk());

	        Tag newTag = tagRepository.findByTitle("newTag");
	        assertThat(newTag).isNotNull();
	        Account joohyuk = accountRepository.findByNickname("keesun");
	        // 만약 @Transactional이 없다면 account는 detached 상태
	        assertThat(joohyuk.getTags().contains(newTag))
	                .isTrue();
	    }

	    @WithAccount("keesun")
	    @DisplayName("계정에 태그 삭제")
	    @Test
	    public void removeTag() throws Exception {
	        Account joohyuk = accountRepository.findByNickname("keesun");
	        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
	        accountService.addTag(joohyuk, newTag);

	        assertThat(joohyuk.getTags().contains(newTag)).isTrue();

	        TagForm tagForm = new TagForm();
	        tagForm.setTagTitle("newTag");

	        mockMvc.perform(post("/settings/tags/remove")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(tagForm))
	                .with(csrf()))
	                .andExpect(status().isOk());

	        assertThat(joohyuk.getTags().contains(newTag)).isFalse();
	    }*/

}
