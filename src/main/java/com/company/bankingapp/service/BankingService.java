package com.company.bankingapp.service;

import com.company.bankingapp.dto.*;

public interface BankingService {
    CreateBankAccountResponse createBankAccount(Long customerId, CreateBankAccountRequest request);

    BankAccountsResponse getBankAccountsByCustomerId(Long customerId);

    BankAccountDTO getBankAccountBalance(String accountNumber);

    void transfer(TransferDTO dto);

    TransferHistoryDTO getTransferHistoryDTO(String accountNumber);
}
