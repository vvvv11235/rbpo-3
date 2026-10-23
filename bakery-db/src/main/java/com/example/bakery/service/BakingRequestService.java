package com.example.bakery.service;

import com.example.bakery.model.BakingRequest;
import com.example.bakery.repository.BakingRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BakingRequestService {

    private final BakingRequestRepository requestRepository;

    public BakingRequestService(BakingRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<BakingRequest> findAll() {
        return requestRepository.findAll();
    }

    // Бизнес-операция 3: Создание заявки (с установкой времени сервером)
    @Transactional
    public BakingRequest create(BakingRequest request) {
        request.setCreatedAt(LocalDateTime.now()); // Время ставит сервер!
        request.setStatus("PENDING");
        return requestRepository.save(request);
    }

    // Бизнес-операция 5: Отмена заявки
    @Transactional
    public BakingRequest cancel(Long id) {
        BakingRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена"));

        if (!request.getStatus().equals("PENDING")) {
            throw new RuntimeException("Можно отменить только заявку в статусе PENDING");
        }

        request.setStatus("CANCELLED");
        return requestRepository.save(request);
    }
}