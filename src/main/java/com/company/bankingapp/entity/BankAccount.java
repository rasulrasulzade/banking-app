package com.company.bankingapp.entity;

import com.company.bankingapp.exception.CommonException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "bank_account")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String currency;
    private String accountNumber;
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public void subtractAmount(BigDecimal amount) {
        if (amount.compareTo(this.getBalance()) > 0) {
            throw new CommonException(String.format("%s could not be more than balance", amount), HttpStatus.BAD_REQUEST);
        }
        this.subtractBalance(amount);
    }

    public void addBalance(BigDecimal amount) {
        this.setBalance(this.getBalance().add(amount));
    }

    public void subtractBalance(BigDecimal amount) {
        this.setBalance(this.getBalance().subtract(amount));
    }
}
