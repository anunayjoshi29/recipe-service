package com.example.recipe_server.entity

import jakarta.persistence.*

@Entity
@Table(name = "recipe_ingredients")
data class RecipeIngredient(
    @Id
    val id: Int,

    @Column(name = "recipe_id", insertable = false, updatable = false)
    val recipeId: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    val recipe: Recipe,

    @Column(name = "product_id", insertable = false, updatable = false)
    val productId: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product
)
