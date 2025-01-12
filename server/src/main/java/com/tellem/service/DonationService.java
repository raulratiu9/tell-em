package com.tellem.service;

import com.tellem.model.Donation;
import com.tellem.repository.DonationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationService {
    private final DonationRepository donationRepository;

    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    public Donation saveDonation(Donation donation) {
        return donationRepository.save(donation);
    }
}

