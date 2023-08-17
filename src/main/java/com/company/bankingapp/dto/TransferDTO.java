package com.company.bankingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {
    @NotBlank(message = "Sender's account number may not be blank")
    private String senderAccountNumber;
    @NotBlank(message = "Receiver's account number may not be blank")
    private String receiverAccountNumber;
    @Positive(message = "Amount may not be zero or negative")
    private BigDecimal amount;
}
