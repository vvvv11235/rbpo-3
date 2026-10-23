package com.example.bakery.repository;

import com.example.bakery.model.BakingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BakingRequestRepository extends JpaRepository<BakingRequest, Long> {
}