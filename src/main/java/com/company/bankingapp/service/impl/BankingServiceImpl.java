package com.company.bankingapp.service.impl;

import com.company.bankingapp.dto.*;
import com.company.bankingapp.entity.BankAccount;
import com.company.bankingapp.entity.Customer;
import com.company.bankingapp.entity.Transfer;
import com.company.bankingapp.exception.CommonException;
import com.company.bankingapp.repository.BankAccountRepository;
import com.company.bankingapp.repository.CustomerRepository;
import com.company.bankingapp.repository.TransferRepository;
import com.company.bankingapp.service.BankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankingServiceImpl implements BankingService {
    private final BankAccountRepository bankAccountRepository;
    private final TransferRepository transferRepository;
    private final CustomerRepository customerRepository;

    @Override
    public CreateBankAccountResponse createBankAccount(Long customerId, CreateBankAccountRequest request) {
        BankAccount bankAccountEnt = BankAccount.builder()
                .balance(request.getAmount())
                .currency(request.getCurrency())
                .accountNumber(generateBankAccount())
                .customer(findCustomer(customerId))
                .build();

        BankAccount res = bankAccountRepository.save(bankAccountEnt);
        return CreateBankAccountResponse.builder()
                .accountNumber(res.getAccountNumber())
                .amount(res.getBalance())
                .currency(res.getCurrency())
                .customerId(res.getCustomer().getId())
                .build();
    }

    @Override
    public BankAccountsResponse getBankAccountsByCustomerId(Long customerId) {
        List<BankAccountDTO> bankAccountDTOS = bankAccountRepository.findByCustomerId(customerId).stream()
                .map(ent -> BankAccountDTO.builder()
                        .id(ent.getId())
                        .accountNumber(ent.getAccountNumber())
                        .currency(ent.getCurrency())
                        .balance(ent.getBalance())
                        .customerId(ent.getCustomer().getId())
                        .build())
                .toList();
        return BankAccountsResponse.builder()
                .bankAccounts(bankAccountDTOS)
                .build();
    }

    @Override
    public BankAccountDTO getBankAccountBalance(String accountNumber) {
        BankAccount bankAccountEnt = findBankAccount(accountNumber);
        return BankAccountDTO.builder()
                .id(bankAccountEnt.getId())
                .customerId(bankAccountEnt.getCustomer().getId())
                .balance(bankAccountEnt.getBalance())
                .accountNumber(bankAccountEnt.getAccountNumber())
                .currency(bankAccountEnt.getCurrency())
                .build();
    }

    @Override
    @Transactional
    public void transfer(TransferDTO dto) {
        final BigDecimal amount = dto.getAmount();
        BankAccount sender = findBankAccount(dto.getSenderAccountNumber());
        sender.subtractAmount(amount);
        bankAccountRepository.save(sender);
        BankAccount receiver = findBankAccount(dto.getReceiverAccountNumber());
        receiver.addBalance(amount);
        bankAccountRepository.save(receiver);
        Transfer transfer = Transfer.builder()
                .amount(amount)
                .receiverAccountNumber(dto.getReceiverAccountNumber())
                .senderAccountNumber(dto.getSenderAccountNumber())
                .build();
        transferRepository.save(transfer);
    }

    @Override
    public TransferHistoryDTO getTransferHistoryDTO(String accountNumber) {
        List<TransferDTO> transferDTOS = transferRepository.findTransfersByAccountNumber(accountNumber).stream()
                .map(t -> TransferDTO.builder()
                        .amount(t.getAmount())
                        .receiverAccountNumber(t.getReceiverAccountNumber())
                        .senderAccountNumber(t.getSenderAccountNumber())
                        .build()).toList();

        return TransferHistoryDTO.builder()
                .transfers(transferDTOS)
                .build();
    }

    private BankAccount findBankAccount(String accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new CommonException(String.format("Bank Account with number %s not found!", accountNumber), HttpStatus.NOT_FOUND));
    }

    private Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CommonException(String.format("Customer with ID %s not found!", customerId), HttpStatus.NOT_FOUND));
    }


    public String generateBankAccount() {
        return "ABC" + System.currentTimeMillis();
    }
}
