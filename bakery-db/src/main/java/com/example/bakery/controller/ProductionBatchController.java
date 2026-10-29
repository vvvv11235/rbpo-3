package com.example.bakery.controller;

import com.example.bakery.model.ProductionBatch;
import com.example.bakery.repository.ProductionBatchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/production-batches")
public class ProductionBatchController {

    private final ProductionBatchRepository repository;

    public ProductionBatchController(ProductionBatchRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<ProductionBatch>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionBatch> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductionBatch> create(@RequestBody ProductionBatch item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(item));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductionBatch> update(@PathVariable Long id, @RequestBody ProductionBatch item) {
        return repository.findById(id).map(existing -> {
            existing.setRecipe(item.getRecipe());
            existing.setQuantity(item.getQuantity());
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