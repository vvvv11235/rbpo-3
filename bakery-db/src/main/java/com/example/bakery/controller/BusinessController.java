package com.example.bakery.controller;

import com.example.bakery.model.*;
import com.example.bakery.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
public class BusinessController {

    private final IngredientService ingredientService;
    private final BakingRequestService requestService;
    private final ProductionBatchService batchService;

    public BusinessController(IngredientService ingredientService,
                              BakingRequestService requestService,
                              ProductionBatchService batchService) {
        this.ingredientService = ingredientService;
        this.requestService = requestService;
        this.batchService = batchService;
    }

    // 1. Пополнение склада
    @PostMapping("/ingredients/{id}/restock")
    public ResponseEntity<Ingredient> restock(@PathVariable Long id, @RequestParam Double amount) {
        return ResponseEntity.ok(ingredientService.restock(id, amount));
    }

    // 2. Запуск партии
    @PostMapping("/batches/start")
    public ResponseEntity<ProductionBatch> startBatch(@RequestParam Long recipeId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(batchService.startBatch(recipeId, quantity));
    }

    // 3. Создание заявки
    @PostMapping("/requests")
    public ResponseEntity<BakingRequest> createRequest(@RequestBody BakingRequest request) {
        return ResponseEntity.ok(requestService.create(request));
    }

    // 4. Завершение партии
    @PostMapping("/batches/{id}/finish")
    public ResponseEntity<ProductionBatch> finishBatch(@PathVariable Long id) {
        return ResponseEntity.ok(batchService.finishBatch(id));
    }

    // 5. Отмена заявки
    @PostMapping("/requests/{id}/cancel")
    public ResponseEntity<BakingRequest> cancelRequest(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.cancel(id));
    }
    // 6. Отмена партии
    @PostMapping("/batches/{id}/cancel")
    public ResponseEntity<ProductionBatch> cancelBatch(@PathVariable Long id) {
        return ResponseEntity.ok(batchService.cancelBatch(id));
    }
}