package com.studyolle.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;

import com.studyolle.account.validator.UserAccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NamedEntityGraph(name = "Study.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")})
@Entity
@Getter@Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Study {
	
	@Id @GeneratedValue
	private Long id;
	
	@ManyToMany
	private Set<Account> managers = new HashSet<>();
	
	@ManyToMany
	private Set<Account> members = new HashSet<>();
	
	@Column(unique = true)
	private String path;
	
	private String title;
	
	private String shortDescription;

	@Lob @Basic(fetch = FetchType.EAGER)
	private String fullDescription;
	
	@Lob @Basic(fetch = FetchType.EAGER)
	private String image;
	
	@ManyToMany
	private Set<Tag> tags = new HashSet<>();
	
	@ManyToMany
	private Set<Zone> zones = new HashSet<>();
	
	private LocalDateTime publishedDateTime; //스터디 공개 시간
	
	private LocalDateTime closedDateTime; //스터디 종료 시간
	
	private LocalDateTime recruitingUpdatedTime; 
	
	private boolean recruiting; //인원 모집 중인지 여부
	
	private boolean published;
	
	private boolean closed;
	
	private boolean useBanner;

	public void addManager(Account account) {
		this.managers.add(account);	
	}
	
	public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.managers.contains(account);

    }

    public boolean isMember(UserAccount userAccount) {
        return this.members.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
        return this.managers.contains(userAccount.getAccount());
    }
}