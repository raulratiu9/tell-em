package com.tellem.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.tellem.model.Donation;
import com.tellem.service.DonationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    private final DonationService donationService;
    @Value("${stripe.api.key}")
    private String stripeKey;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping
    public List<Donation> getAllDonations() {
        return donationService.getAllDonations();
    }

    @PostMapping
    public Map<String, Object> createDonation(@RequestBody Map<String, Object> request) throws StripeException {
        Stripe.apiKey = stripeKey;

        int amount = (int) request.get("amount");
        String currency = (String) request.get("currency");

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("payment_method_types", List.of("card"));

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        Map<String, Object> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        return response;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, stripeKey);

            if ("payment_intent.succeeded".equals(event.getType())) {
                PaymentIntent intent = (PaymentIntent) event.getData().getObject();
                System.out.println("Donation received: " + intent.getAmount());

                    try {
                        Donation donation = new Donation();
                    donation.setAmount(intent.getAmount().toString());
                    donation.setClientSecret(payload);
                    donation.setCreatedAt(LocalDateTime.now()); // Set the author using the new method

                        donationService.saveDonation(donation);

                        return new ResponseEntity<>("Donation added successfully", HttpStatus.CREATED);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>("Error adding donation", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
            }
            return ResponseEntity.ok("Webhook received!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error");
        }
    }

}
