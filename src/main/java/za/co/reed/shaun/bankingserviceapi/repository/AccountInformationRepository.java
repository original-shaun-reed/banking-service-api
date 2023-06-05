package za.co.reed.shaun.bankingserviceapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import za.co.reed.shaun.bankingserviceapi.entity.AccountInformation;

public interface AccountInformationRepository extends JpaRepository<AccountInformation, Long> {
    AccountInformation getAccountInformationByAccountNumber(@Param("accountNumber") Integer accountNumber);
}
