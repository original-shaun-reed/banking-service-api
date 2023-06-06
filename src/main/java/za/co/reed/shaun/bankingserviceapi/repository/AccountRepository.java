package za.co.reed.shaun.bankingserviceapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import za.co.reed.shaun.bankingserviceapi.entity.Account;

public interface AccountInformationRepository extends JpaRepository<Account, Long> {
    Account getAccountInformationByAccountNumber(@Param("accountNumber") Integer accountNumber);
}
