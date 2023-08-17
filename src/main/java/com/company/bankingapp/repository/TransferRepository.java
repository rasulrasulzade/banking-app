package com.company.bankingapp.repository;

import com.company.bankingapp.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    @Query("select t from Transfer t where t.receiverAccountNumber =:accountNumber or t.senderAccountNumber =:accountNumber")
    List<Transfer> findTransfersByAccountNumber(@Param("accountNumber") String accountNumber);
}
