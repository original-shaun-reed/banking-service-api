package za.co.reed.shaun.bankingserviceapi.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.reed.shaun.bankingserviceapi.model.request.CurrentAccountRequest;
import za.co.reed.shaun.bankingserviceapi.model.request.SavingsAccountRequest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, unique = true)
    private Long accountID;

    @Column(nullable = false)
    private String accountHolderName;

    @Column(nullable = false)
    private String accountHolderSurname;

    @Column(nullable = false)
    private Integer accountNumber;

    @Column(nullable = false)
    private String accountType;

    @Column(nullable = false)
    private Double accountBalance;

    private Double overdraftBalance;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public Account(SavingsAccountRequest request) {
        this.accountHolderName = request.accountHolderName();
        this.accountHolderSurname = request.accountHolderSurname();
        this.accountNumber = request.accountNumber();
        this.accountType = !Objects.isNull(request.accountType()) ? request.accountType().name() : null;
        this.accountBalance = request.amountToDeposit();
        this.createdAt = new Date();
    }

    public Account(CurrentAccountRequest request) {
        this.accountHolderName = request.accountHolderName();
        this.accountHolderSurname = request.accountHolderSurname();
        this.accountNumber = request.accountNumber();
        this.accountType = !Objects.isNull(request.accountType()) ? request.accountType().name() : null;
        this.accountBalance = !Objects.isNull(request.amountToDeposit()) ? request.amountToDeposit() : BigDecimal.ZERO.doubleValue();
        this.overdraftBalance = BigDecimal.ZERO.doubleValue();
        this.createdAt = new Date();
    }
}
