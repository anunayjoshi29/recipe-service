package com.example.recipe_server.entity

import jakarta.persistence.*

@Entity
@Table(name = "cart_items")
data class CartItem(
    @Id
    val id: Int,

    @Column(name = "cart_id", insertable = false, updatable = false)
    val cartId: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    val cart: Cart,

    @Column(name = "product_id", insertable = false, updatable = false)
    val productId: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,

    @Column(name = "recipe_id")
    val recipeId: Int? = null
)
