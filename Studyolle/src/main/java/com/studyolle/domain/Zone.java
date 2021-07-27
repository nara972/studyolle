package com.studyolle.domain;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.studyolle.domain.Account.AccountBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Zone {
	
	@Id @GeneratedValue
	private Long id;
	
	@Column(nullable = false)
	private String city;
	
	@Column(nullable = false)
	private String localNameOfCity;
	
	@Column(nullable= false)
	private String province;

}
