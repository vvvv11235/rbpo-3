package com.example.bakery.controller;

import com.example.bakery.model.BakingRequest;
import com.example.bakery.repository.BakingRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/baking-requests")
public class BakingRequestController {

    private final BakingRequestRepository repository;

    public BakingRequestController(BakingRequestRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<BakingRequest>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BakingRequest> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BakingRequest> create(@RequestBody BakingRequest item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(item));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BakingRequest> update(@PathVariable Long id, @RequestBody BakingRequest item) {
        return repository.findById(id).map(existing -> {
            existing.setUser(item.getUser());
            existing.setRecipe(item.getRecipe());
            existing.setQuantity(item.getQuantity());
            existing.setBatch(item.getBatch());
            existing.setStatus(item.getStatus());
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}