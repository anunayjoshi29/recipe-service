package com.example.recipe_server.service

import com.example.recipe_server.entity.Recipe
import com.example.recipe_server.repository.RecipeRepository
import org.springframework.stereotype.Service

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository
) {
    fun findAll(): List<Recipe> {
        return recipeRepository.findAll()
    }

    fun findById(id: Int): Recipe {
        return recipeRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Recipe not found for id=$id") }
    }
}
