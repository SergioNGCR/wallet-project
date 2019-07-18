package com.sergio.wallet.server.data.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BALANCE")
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "BALANCE")
    private long balance;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "MODIFIED")
    private LocalDateTime modified;

    @Column(name = "LAST_TRANSACTION_ID")
    private long lastTransactionId;

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

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public long getLastTransactionId() {
        return lastTransactionId;
    }

    public void setLastTransactionId(long lastTransactionId) {
        this.lastTransactionId = lastTransactionId;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", modified=" + modified +
                ", lastTransactionId=" + lastTransactionId +
                '}';
    }
}
