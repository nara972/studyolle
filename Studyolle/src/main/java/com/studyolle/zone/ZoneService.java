package com.studyolle.zone;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.studyolle.domain.Zone;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

	private final ZoneRepository zoneRepository;

	@PostConstruct // ZoneService 빈이 만들어진 이후에 실행되는 지점
	public void initZoneData() throws IOException {
		if (zoneRepository.count() == 0) {
			Resource resource = new ClassPathResource("zones_kr.csv");
			List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
					.map(line -> {
						String[] split = line.split(",");
						return Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build();
					}).collect(Collectors.toList());

			zoneRepository.saveAll(zoneList);
		}
	}

}
