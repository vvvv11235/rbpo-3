package com.example.bakery.controller;

import com.example.bakery.model.Ingredient;
import com.example.bakery.repository.IngredientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientRepository repository;

    public IngredientController(IngredientRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<Ingredient>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<Ingredient> create(@RequestBody Ingredient item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(item));
    }
}