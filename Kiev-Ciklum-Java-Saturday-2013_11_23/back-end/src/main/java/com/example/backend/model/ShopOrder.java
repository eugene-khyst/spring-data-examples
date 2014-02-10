package com.example.backend.model;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class ShopOrder extends PersistentEntity {

    @Column(nullable = false)
    private String orderId = UUID.randomUUID().toString();
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdTimestamp = new Date();
    
    @Column(nullable = false)
    private Double amount;
    
    @OneToMany(mappedBy = "order")
    private Collection<Payment> payments;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Collection<Payment> getPayments() {
        return payments;
    }

    public void setPayments(Collection<Payment> payments) {
        this.payments = payments;
    }
}
