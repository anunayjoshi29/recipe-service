package com.example.recipe_server.entity

import jakarta.persistence.*

@Entity
@Table(name = "recipes")
data class Recipe(
    @Id
    val id: Int,

    val name: String
)
