package com.studyolle.zone;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studyolle.domain.Zone;

public interface ZoneRepository extends JpaRepository<Zone, Long>{
	 Zone findByCityAndProvince(String city, String province);
}
