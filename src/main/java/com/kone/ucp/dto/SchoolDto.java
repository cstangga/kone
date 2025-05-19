package com.kone.ucp.dto;

import com.kone.ucp.domain.Member;
import com.kone.ucp.domain.School;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolDto {

    private long schoolId;
    private String area;
    private String schoolName;
    private String schoolAddress;
    private String schoolAdminName;
    private String schoolAdminPhoneNumber;
    private Member agent;
    private LocalDateTime testDate;		//시험 날짜

    public School toEntity() {
        return School.builder()
                .area(area)
                .name(schoolName)
                .address(schoolAddress)
                .schoolAdminName(schoolAdminName)
                .schoolAdminPhoneNumber(schoolAdminPhoneNumber)
                .testDate(testDate)
                .build();
    }

    public String getTestDateFormatted() {
        return testDate != null
                ? testDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
                : "";
    }
}
