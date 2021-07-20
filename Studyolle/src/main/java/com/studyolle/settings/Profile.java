package com.studyolle.settings;

import org.hibernate.validator.constraints.Length;

import com.studyolle.domain.Account;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Profile {
	
	

	@Length(max=35)
	private String bio;
	
	@Length(max=50)
	private String url;
	
	@Length(max=50)
	private String occupation;
	
	@Length(max=50)
	private String location;
	
	private String profileImage;

}
