package com.company.bankingapp;


import com.company.bankingapp.dto.*;
import com.company.bankingapp.entity.BankAccount;
import com.company.bankingapp.entity.Customer;
import com.company.bankingapp.repository.BankAccountRepository;
import com.company.bankingapp.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class BankingControllerTests extends DatabaseTestConfig{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void contextLoads() {
    }

    @DisplayName("Create customer")
    @Test
    public void testCreateCustomer() throws Exception {
        CreateCustomerRequest createCustomerRequest =CreateCustomerRequest.builder()
                .name("Anton Chigurh")
                .build();
        ResultActions resultActions = mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCustomerRequest)));

        resultActions.andDo(print())
                .andExpect(status().isCreated());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        CustomerDTO customerDTO = objectMapper.readValue(contentAsString, CustomerDTO.class);
        assertEquals(createCustomerRequest.getName(), customerDTO.name());
    }

    @DisplayName("Create bank account")
    @Test
    public void testBankAccountPositiveScenario() throws Exception {

        CreateBankAccountRequest bankAccountRequest = CreateBankAccountRequest.builder()
                .currency("USD")
                .amount(BigDecimal.valueOf(100))
                .build();

        Long customerId = 4L;
        ResultActions resultActions = mockMvc.perform(post("/{customerId}/bankAccounts", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bankAccountRequest)));

        resultActions.andDo(print())
                .andExpect(status().isCreated());
        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        CreateBankAccountResponse response = objectMapper.readValue(contentAsString, CreateBankAccountResponse.class);

        assertEquals(bankAccountRequest.getAmount(), response.getAmount());
        assertEquals(bankAccountRequest.getCurrency(), response.getCurrency());
        assertEquals(customerId, response.getCustomerId());
        assertNotNull(response.getAccountNumber());
    }


    @DisplayName("Create bank account negative")
    @Test
    public void testBankAccountNegativeScenarioWrongCustomer() throws Exception {
        Long customerId = 99L;
        CreateBankAccountRequest bankAccountRequest = CreateBankAccountRequest.builder()
                .currency("USD")
                .amount(BigDecimal.valueOf(100))
                .build();

        ResultActions resultActions = mockMvc.perform(post("/{customerId}/bankAccounts", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bankAccountRequest)));

        resultActions.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(String.format("Customer with ID %s not found!", customerId))));
    }

    @DisplayName("Create bank account negative")
    @Test
    public void testBankAccountNegativeScenarioNotValidAmount() throws Exception {
        Long customerId = 4L;
        CreateBankAccountRequest bankAccountRequest = CreateBankAccountRequest.builder()
                .currency("USD")
                .amount(BigDecimal.ZERO)
                .build();

        ResultActions resultActions = mockMvc.perform(post("/{customerId}/bankAccounts", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bankAccountRequest)));

        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Amount may not be zero or negative")));
    }

    @DisplayName("Get bank accounts Positive")
    @Test
    public void testGetBankAccountsPositive() throws Exception {
        Customer customer = findCustomer(1L);
        Long customerId = customer.getId();

        BankAccount bankAccount1 = BankAccount.builder()
                .customer(customer)
                .accountNumber("ABC1692177596088")
                .balance(BigDecimal.valueOf(100))
                .currency("USD")
                .build();
        BankAccount bankAccount2 = BankAccount.builder()
                .customer(customer)
                .accountNumber("ABC1692177596089")
                .balance(BigDecimal.valueOf(200))
                .currency("EUR")
                .build();
        List<BankAccount> bankAccounts = List.of(bankAccount1, bankAccount2);
        bankAccountRepository.saveAll(bankAccounts);


        ResultActions resultActions = mockMvc.perform(get("/{customerId}/bankAccounts", customerId));
        resultActions.andDo(print())
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        BankAccountsResponse response = objectMapper.readValue(contentAsString, BankAccountsResponse.class);
        assertEquals(bankAccounts.size(), response.getBankAccounts().size());
        assertBankAccount(bankAccount1, response.getBankAccounts().get(0));
        assertBankAccount(bankAccount2, response.getBankAccounts().get(1));
    }

    @DisplayName("Get bank accounts negative")
    @Test
    public void testGetBankAccountsNegative() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/{customerId}/bankAccounts", 6L));
        resultActions.andDo(print())
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        BankAccountsResponse response = objectMapper.readValue(contentAsString, BankAccountsResponse.class);
        assertEquals(0, response.getBankAccounts().size());
    }

    @DisplayName("Get bank account Positive")
    @Test
    public void testGetBankAccountPositive() throws Exception {
        Customer customer = findCustomer(2L);
        BankAccount bankAccount = BankAccount.builder()
                .accountNumber(generateBankAccount())
                .balance(BigDecimal.valueOf(100))
                .currency("USD")
                .build();
        bankAccount.setCustomer(customer);
        bankAccountRepository.save(bankAccount);

        ResultActions resultActions = mockMvc.perform(get("/bankAccounts/{accountNumber}", bankAccount.getAccountNumber()));
        resultActions.andDo(print())
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        BankAccountDTO response = objectMapper.readValue(contentAsString, BankAccountDTO.class);
        assertBankAccount(bankAccount, response);
    }

    @DisplayName("Get bank account Negative")
    @Test
    public void testGetBankAccountNegative() throws Exception {
        String accountNumber = "ABC1692177614611";
        ResultActions resultActions = mockMvc.perform(get("/bankAccounts/{accountNumber}", accountNumber));
        resultActions.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(String.format("Bank Account with number %s not found!", accountNumber))));
    }

    @DisplayName("Transfer")
    @Test
    public void testTransfer() throws Exception {
        Customer customer1 = findCustomer(3L);

        BankAccount bankAccount1 = BankAccount.builder()
                .customer(customer1)
                .accountNumber(generateBankAccount())
                .balance(BigDecimal.valueOf(100))
                .currency("USD")
                .build();

        Customer customer2 = findCustomer(2L);
        BankAccount bankAccount2 = BankAccount.builder()
                .customer(customer2)
                .accountNumber(generateBankAccount())
                .balance(BigDecimal.valueOf(200))
                .currency("USD")
                .build();
        bankAccountRepository.saveAll(List.of(bankAccount1, bankAccount2));

        TransferDTO transferDTO = TransferDTO.builder()
                .senderAccountNumber(bankAccount1.getAccountNumber())
                .receiverAccountNumber(bankAccount2.getAccountNumber())
                .amount(BigDecimal.valueOf(20))
                .build();

        ResultActions resultActions = mockMvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferDTO)));

        resultActions.andDo(print())
                .andExpect(status().isOk());

        BankAccount updatedBankAccount1 = bankAccountRepository.findByAccountNumber(bankAccount1.getAccountNumber())
                .orElse(null);
        assertNotNull(updatedBankAccount1);
        assertEquals(0, BigDecimal.valueOf(80).compareTo(updatedBankAccount1.getBalance()));

        BankAccount updatedBankAccount2 = bankAccountRepository.findByAccountNumber(bankAccount2.getAccountNumber())
                .orElse(null);
        assertNotNull(updatedBankAccount2);
        assertEquals(0, BigDecimal.valueOf(220).compareTo(updatedBankAccount2.getBalance()));

    }

    private void assertBankAccount(BankAccount expected, BankAccountDTO actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCustomer().getId(), actual.getCustomerId());
        assertEquals(0, expected.getBalance().compareTo(actual.getBalance()));
        assertEquals(expected.getAccountNumber(), actual.getAccountNumber());
    }

    private Customer findCustomer(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    public String generateBankAccount() {
        return "ABC" + System.currentTimeMillis();
    }
}
