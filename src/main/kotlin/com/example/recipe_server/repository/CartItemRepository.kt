package com.example.recipe_server.repository

import com.example.recipe_server.entity.CartItem
import org.springframework.data.jpa.repository.JpaRepository

interface CartItemRepository : JpaRepository<CartItem, Int> {

    // A custom finder that retrieves all CartItem records
    // belonging to a specific cart.
    fun findByCartId(cartId: Int): List<CartItem>

    // A custom delete method to remove items associated with
    // a specific cart and recipe.
    fun deleteByCartIdAndRecipeId(cartId: Int, recipeId: Int)
}
