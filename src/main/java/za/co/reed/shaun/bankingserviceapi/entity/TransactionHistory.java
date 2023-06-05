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
    private Integer accountNumber;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    private Double previousAmount;
    private Double updatedAmount;
    private String transactionType;

    public TransactionHistory(String accountType, Integer accountNumber, Double previousAmount,
                              Double updatedAmount, String transactionType) {
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.createdAt = new Date();
        this.previousAmount = previousAmount;
        this.updatedAmount = updatedAmount;
        this.transactionType = transactionType;
    }
}
