package com.example.bakery.service;

import com.example.bakery.model.*;
import com.example.bakery.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductionBatchService {

    private final ProductionBatchRepository batchRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final BakingRequestRepository requestRepository;

    public ProductionBatchService(ProductionBatchRepository batchRepository,
                                  RecipeRepository recipeRepository,
                                  IngredientRepository ingredientRepository,
                                  BakingRequestRepository requestRepository) {
        this.batchRepository = batchRepository;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.requestRepository = requestRepository;
    }

    public List<ProductionBatch> findAll() {
        return batchRepository.findAll();
    }

    // Бизнес-операция 2: Запуск партии (списывает ингредиенты)
    @Transactional
    public ProductionBatch startBatch(Long recipeId, Integer quantity) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Рецептура не найдена"));

        // Проверяем, хватает ли ингредиентов
        for (RecipeIngredient ri : recipe.getIngredients()) {
            Double needed = ri.getAmount() * quantity;
            if (ri.getIngredient().getPhysicalStock() < needed) {
                throw new RuntimeException("Недостаточно ингредиента: " + ri.getIngredient().getName());
            }
        }

        // Списываем ингредиенты
        for (RecipeIngredient ri : recipe.getIngredients()) {
            Ingredient ing = ri.getIngredient();
            ing.setPhysicalStock(ing.getPhysicalStock() - (ri.getAmount() * quantity));
            ingredientRepository.save(ing);
        }

        ProductionBatch batch = new ProductionBatch();
        batch.setRecipe(recipe);
        batch.setQuantity(quantity);
        batch.setStatus("IN_PROGRESS");
        return batchRepository.save(batch);
    }

    // Бизнес-операция 4: Завершение партии (распределяет заявки)
    @Transactional
    public ProductionBatch finishBatch(Long batchId) {
        ProductionBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Партия не найдена"));

        if (!batch.getStatus().equals("IN_PROGRESS")) {
            throw new RuntimeException("Можно завершить только партию в статусе IN_PROGRESS");
        }

        batch.setStatus("DONE");

        // Ищем все заявки в статусе PENDING для этой рецептуры
        List<BakingRequest> pendingRequests = requestRepository.findAll().stream()
                .filter(r -> r.getStatus().equals("PENDING")
                        && r.getRecipe().getId().equals(batch.getRecipe().getId()))
                .toList();

        for (BakingRequest request : pendingRequests) {
            request.setStatus("READY");
            request.setBatch(batch);
            requestRepository.save(request);
        }

        return batchRepository.save(batch);
    }

    // Бизнес-операция 6: Отмена партии (возвращает ингредиенты на склад)
    @Transactional
    public ProductionBatch cancelBatch(Long batchId) {
        ProductionBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Партия не найдена"));

        if (!batch.getStatus().equals("IN_PROGRESS")) {
            throw new RuntimeException("Можно отменить только партию в статусе IN_PROGRESS");
        }

        // Возвращаем ингредиенты на склад
        for (RecipeIngredient ri : batch.getRecipe().getIngredients()) {
            Ingredient ing = ri.getIngredient();
            double returnAmount = ri.getAmount() * batch.getQuantity();
            ing.setPhysicalStock(ing.getPhysicalStock() + returnAmount); // Возвращаем остаток
            ingredientRepository.save(ing); // Сохраняем ингредиент
        }

        batch.setStatus("CANCELLED"); // Меняем статус партии
        return batchRepository.save(batch); // Сохраняем и возвращаем партию
    }
}