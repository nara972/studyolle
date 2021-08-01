package com.studyolle.study;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyolle.domain.Account;
import com.studyolle.domain.Study;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {
	
	private final StudyRepository repository;

	public Study createNewStudy(Study study, Account account) {
		Study newStudy=repository.save(study);
		newStudy.addManager(account);
		return newStudy;
	}

}
