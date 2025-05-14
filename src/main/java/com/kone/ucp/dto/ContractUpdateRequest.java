package com.kone.ucp.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractUpdateRequest {
    private Long id;
    private Long pay;
    private String residentNumber;
    private String accountNumber;
    private String accountHolder;
}
