package com.company.bankingapp.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBankAccountRequest {
    @Size(min = 3, max = 3)
    private String currency;
    @Positive(message = "Amount may not be zero or negative")
    private BigDecimal amount;
}
