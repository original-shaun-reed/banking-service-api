package za.co.reed.shaun.bankingserviceapi.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, unique = true)
    private Long transactionID;
    private String accountType;
    private Integer fromAccountNumber;
    private Integer toAccountNumber;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    private Double previousAccountBalance;
    private Double updatedAccountBalance;

    private Double previousOverdraftBalance;
    private Double updatedOverdraftBalance;

    private String transactionType;

    public TransactionHistory(String accountType, Integer fromAccountNumber, Integer toAccountNumber,
                              Double previousAccountBalance, Double updatedAccountBalance,
                              Double previousOverdraftBalance, Double updatedOverdraftBalance, String transactionType) {
        this.accountType = accountType;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.createdAt = new Date();
        this.previousAccountBalance = previousAccountBalance;
        this.updatedAccountBalance = updatedAccountBalance;
        this.previousOverdraftBalance = previousOverdraftBalance;
        this.updatedOverdraftBalance = updatedOverdraftBalance;
        this.transactionType = transactionType;
    }
}
