package com.company.bankingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBankAccountResponse {
    private String currency;
    private BigDecimal amount;
    private Long customerId;
    private String accountNumber;
}
