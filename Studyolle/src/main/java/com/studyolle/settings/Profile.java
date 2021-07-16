package com.studyolle.settings;

import com.studyolle.domain.Account;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Profile {
	
	public Profile(Account account) {
		this.bio=account.getBio();
		this.url=account.getUrl();
		this.occupation=account.getOccupation();
		this.location=account.getLocation();
	}

	private String bio;
	
	private String url;
	
	private String occupation;
	
	private String location;

}
