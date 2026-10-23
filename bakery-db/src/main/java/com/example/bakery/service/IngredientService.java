package com.example.bakery.service;

import com.example.bakery.model.Ingredient;
import com.example.bakery.repository.IngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> findAll() {
        return ingredientRepository.findAll();
    }

    public Ingredient findById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ингредиент не найден"));
    }

    public Ingredient save(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public void delete(Long id) {
        ingredientRepository.deleteById(id);
    }

    // Бизнес-операция 1: Пополнение склада
    @Transactional
    public Ingredient restock(Long id, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Количество для пополнения должно быть больше нуля");
        }
        Ingredient ingredient = findById(id);
        ingredient.setPhysicalStock(ingredient.getPhysicalStock() + amount);
        return ingredientRepository.save(ingredient);
    }
}