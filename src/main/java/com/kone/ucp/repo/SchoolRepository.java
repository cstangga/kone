package com.kone.ucp.repo;

import java.util.Collection;
import java.util.List;

import com.kone.ucp.dto.SchoolDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kone.ucp.domain.School;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SchoolRepository extends JpaRepository<School, Long> {
    @Query("SELECT s FROM School s LEFT JOIN FETCH s.member")
    List<School> findAllWithMember();

    @Query("""
SELECT s FROM School s
LEFT JOIN FETCH s.member
WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))
""")
    List<School> findByNameContainingWithMember(@Param("name") String name);



    @Query("""
SELECT s FROM School s
LEFT JOIN FETCH s.member
WHERE s.area = :area
""")
    List<School> findByAreaWithMember(@Param("area") String area);

    @Query("""
SELECT s FROM School s
LEFT JOIN FETCH s.member m
WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
   OR LOWER(s.schoolAdminName) LIKE LOWER(CONCAT('%', :keyword, '%'))
   OR LOWER(s.schoolAdminPhoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
   OR (m IS NOT NULL AND LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
   OR (m IS NOT NULL AND LOWER(m.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
    List<School> searchAllFields(@Param("keyword") String keyword);
}
