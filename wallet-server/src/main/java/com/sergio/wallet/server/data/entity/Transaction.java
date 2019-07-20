package com.sergio.wallet.server.data.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACTION")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private long id;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "DEPOSIT")
    private long deposit;

    @Column(name = "WITHDRAW")
    private long withdraw;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "DATE")
    private LocalDateTime date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getDeposit() {
        return deposit;
    }

    public void setDeposit(long deposit) {
        this.deposit = deposit;
    }

    public long getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(long withdraw) {
        this.withdraw = withdraw;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", deposit=" + deposit +
                ", withdraw=" + withdraw +
                ", currency='" + currency + '\'' +
                ", date=" + date +
                '}';
    }
}
