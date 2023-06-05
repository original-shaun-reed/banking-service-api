package za.co.reed.shaun.bankingserviceapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.reed.shaun.bankingserviceapi.entity.TransactionHistory;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
}
