package com.example.recipe_server.repository

import com.example.recipe_server.entity.RecipeIngredient
import org.springframework.data.jpa.repository.JpaRepository

interface RecipeIngredientRepository : JpaRepository<RecipeIngredient, Int> {

    // Returns all ingredients for a given recipe
    fun findByRecipeId(recipeId: Int): List<RecipeIngredient>
}
