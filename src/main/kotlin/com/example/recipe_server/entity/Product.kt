package com.example.recipe_server.entity

import jakarta.persistence.*

@Entity
@Table(name = "products")
data class Product(
    @Id
    val id: Int,

    val name: String,

    @Column(name = "price_in_cents", nullable = false)
    val priceInCents: Int
)
