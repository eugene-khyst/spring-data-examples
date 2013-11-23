package com.example.backend.repository;

import com.example.backend.model.Payment;
import com.example.backend.model.ShopOrder;
import com.example.util.transaction.Transactional;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PaymentRepository {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRepository.class);
    
    @PersistenceUnit(unitName = "example")
    private EntityManagerFactory emf;

    @Transactional
    public ShopOrder findOrder(String orderId) {
        logger.debug("findOrder {}", orderId);
        
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ShopOrder> cq = cb.createQuery(ShopOrder.class);
            Root<ShopOrder> order = cq.from(ShopOrder.class);
            cq.where(cb.equal(order.get("orderId"), orderId));
            cq.orderBy(cb.desc(order.get("createdTimestamp")));
            TypedQuery<ShopOrder> q = em.createQuery(cq);
            return q.getSingleResult();
        } catch(EntityNotFoundException e) {
            logger.warn(e.getMessage(), e);
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public Payment findPayment(String paymentId) {
        logger.debug("findPayment {}", paymentId);
        
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Payment> cq = cb.createQuery(Payment.class);
            Root<Payment> payment = cq.from(Payment.class);
            cq.where(cb.equal(payment.get("paymentId"), paymentId));
            cq.orderBy(cb.desc(payment.get("createdTimestamp")));
            TypedQuery<Payment> q = em.createQuery(cq);
            return q.getSingleResult();
        } catch(EntityNotFoundException e) {
            logger.warn(e.getMessage(), e);
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    @Transactional
    public void save(ShopOrder order) {
        logger.debug("save {}", order);
        
        EntityManager em = emf.createEntityManager();
        try {
            em.persist(order);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Transactional
    public void save(Payment payment) {
        logger.debug("save {}", payment);
        
        EntityManager em = emf.createEntityManager();
        try {
            em.persist(payment);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    @Transactional
    public Payment update(Payment payment) {
        logger.debug("update {}", payment);
        
        EntityManager em = emf.createEntityManager();
        try {
            return em.merge(payment);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
