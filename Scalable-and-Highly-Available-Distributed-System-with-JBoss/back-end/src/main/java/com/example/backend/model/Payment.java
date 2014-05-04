package com.example.backend.model;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Payment extends PersistentEntity {

    @Column(nullable = false)
    private String paymentId = UUID.randomUUID().toString();
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdTimestamp = new Date();
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.SUBMITED;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private ShopOrder order;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    
    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    
    public ShopOrder getOrder() {
        return order;
    }

    public void setOrder(ShopOrder order) {
        this.order = order;
    }
}
