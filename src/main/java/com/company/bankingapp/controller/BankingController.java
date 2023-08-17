package com.company.bankingapp.controller;

import com.company.bankingapp.dto.*;
import com.company.bankingapp.service.BankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
public class BankingController {
    private final BankingService bankingService;

    @PostMapping("/{customerId}/bankAccounts")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateBankAccountResponse createBankAccount(@PathVariable Long customerId, @RequestBody @Valid CreateBankAccountRequest request) {
        return bankingService.createBankAccount(customerId, request);
    }

    @GetMapping("/{customerId}/bankAccounts")
    public BankAccountsResponse getBankAccountsByCustomerId(@PathVariable Long customerId) {
        return bankingService.getBankAccountsByCustomerId(customerId);
    }

    @GetMapping("/bankAccounts/{accountNumber}")
    public BankAccountDTO getBankAccountBalance(@PathVariable String accountNumber) {
        return bankingService.getBankAccountBalance(accountNumber);
    }

    @PostMapping("/transfers")
    public void transfer(@RequestBody @Valid TransferDTO dto) {
        bankingService.transfer(dto);
    }

    @GetMapping("/transfers/{accountNumber}")
    public TransferHistoryDTO getTransferHistory(@PathVariable String accountNumber) {
        return bankingService.getTransferHistoryDTO(accountNumber);
    }
}
