package com.kone.ucp.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import com.kone.ucp.domain.EmpType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractDTO {
    private Long id;
    private String name;
    private String phoneNumber;
    private Set<EmpType> empType;
    private String address;
    private String desiredArea;
    private String selectionType;
    private Boolean submitType;
    private String residentNumber;
    private String accountNumber;
    private String accountHolder;
    private String bank;
    private LocalDateTime creationDate;
    private Long pay;
    private LocalDateTime createdTime;
    private List<LocalDateTime> workingDays;
}