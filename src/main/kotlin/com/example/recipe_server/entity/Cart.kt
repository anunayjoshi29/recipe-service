
package com.example.recipe_server.entity

import jakarta.persistence.*

@Entity
@Table(name = "carts")
data class Cart(
    @Id
    val id: Int,

    @Column(name = "total_in_cents", nullable = false)
    var totalInCents: Int
)
