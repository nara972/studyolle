package com.studyolle.tag;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.studyolle.domain.Tag;

@Transactional(readOnly=true)
public interface TagRepository extends JpaRepository<Tag, Long> {
	 Tag findByTitle(String title);
}
