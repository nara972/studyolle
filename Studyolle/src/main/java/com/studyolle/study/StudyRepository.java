package com.studyolle.study;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.studyolle.domain.Study;

@Transactional(readOnly=true)
public interface StudyRepository extends JpaRepository<Study, Long> {
	
	boolean existsByPath(String path);

	@EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD)
	Study findByPath(String path);
	
}
