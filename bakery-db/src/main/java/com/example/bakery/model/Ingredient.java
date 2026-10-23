package com.example.bakery.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // название

    @Column(nullable = false)
    private String unit; // единица измерения (кг, л, шт)

    @Column(nullable = false)
    private Double physicalStock; // физический остаток

    public Ingredient() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Double getPhysicalStock() { return physicalStock; }
    public void setPhysicalStock(Double physicalStock) { this.physicalStock = physicalStock; }
}