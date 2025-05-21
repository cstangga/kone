package com.kone.ucp.service;

import com.kone.ucp.domain.School;
import com.kone.ucp.dto.SchoolDto;
import com.kone.ucp.repo.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolService {
    private final SchoolRepository schoolRepository;


    public List<SchoolDto> findAllWithMember() {
        log.info("SchoolService / findAll");
        return schoolRepository.findAllWithMember().stream().map(School::toDto).toList();
    }

    public void save(SchoolDto schoolDto) {
        log.info("SchoolService / save");

        School school=schoolDto.toEntity();
        school=schoolRepository.save(school);

        log.info("SchoolService / school = {}",school);
    }

    public SchoolDto findById(Long id) {
        log.info("SchoolService / findById = {}",id);
        SchoolDto schoolDto= schoolRepository.findById(id).stream().map(School::toDto).findFirst().orElseThrow();
        log.info("SchoolService / schoolDto = {}",schoolDto);
        return schoolDto;
    }

    public void delete(Long id) {
        log.info("SchoolService / delete = {}",id);
        schoolRepository.deleteById(id);
    }

    public void update(SchoolDto schoolDto) {
        log.info("SchoolService / update = {}",schoolDto);
        School school=schoolRepository.findById(schoolDto.getSchoolId()).orElseThrow();
        log.info("school = {} ",school);
        school.update(schoolDto);
        schoolRepository.save(school);
    }


    public List<SchoolDto> findByArea(String area) {
        log.info("SchoolService / findByArea = {}",area);
        return schoolRepository.findByAreaWithMember(area).stream().map(School::toDto).toList();
    }

    public List<SchoolDto> searchByKeyword(String keyword) {
        return schoolRepository.searchAllFields(keyword)
                .stream()
                .map(School::toDto)
                .collect(Collectors.toList());
    }
}
