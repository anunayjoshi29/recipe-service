package com.example.recipe_server.controller

import com.example.recipe_server.entity.Recipe
import com.example.recipe_server.service.RecipeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/recipes")
class RecipeController(
    private val recipeService: RecipeService
) {
    @GetMapping
    fun listRecipes(): List<Recipe> {
        return recipeService.findAll()
    }
}
