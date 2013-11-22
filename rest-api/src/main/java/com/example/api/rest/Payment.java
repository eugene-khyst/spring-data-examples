package com.example.api.rest;

import com.example.api.service.PaymentService;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.inject.Inject;

@Path("/payment")
public class Payment {

    @Inject
    private PaymentService paymentService;

    @POST
    @Path("/order")
    public String createOrder(@QueryParam("amount") double amount) {
        return paymentService.createOrder(amount);
    }
    
    @POST
    @Path("/order/{orderId}")
    public String pay(@PathParam("orderId") String orderId) {
        return paymentService.pay(orderId);
    }

    @GET
    @Path("/{paymentId}/status")
    public String getPaymentStatus(@PathParam("paymentId") String paymentId) {
        return paymentService.getPaymentStatus(paymentId);
    }
}
