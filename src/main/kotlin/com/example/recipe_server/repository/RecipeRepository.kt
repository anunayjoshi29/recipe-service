package com.example.recipe_server.repository

import com.example.recipe_server.entity.Recipe
import org.springframework.data.jpa.repository.JpaRepository

interface RecipeRepository : JpaRepository<Recipe, Int>
