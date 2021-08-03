package com.studyolle.study.form;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudyDescriptionForm {

	@NotBlank
	@Length(max=100)
	private String shortDescription;
	
	@NotBlank
	private String fullDescription;
	
}
