package com.kone.ucp.service;

import com.kone.ucp.domain.School;
import com.kone.ucp.dto.SchoolDto;
import com.kone.ucp.repo.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolService {
    private final SchoolRepository schoolRepository;


    public List<SchoolDto> findAll() {
        log.info("SchoolService / findAll");
        return schoolRepository.findAll().stream().map(School::toDto).toList();
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

    }


}
