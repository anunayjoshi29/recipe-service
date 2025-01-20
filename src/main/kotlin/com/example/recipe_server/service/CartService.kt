package com.example.recipe_server.service

import com.example.recipe_server.entity.Cart
import com.example.recipe_server.entity.CartItem
import com.example.recipe_server.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
    private val recipeRepository: RecipeRepository,
    private val recipeIngredientRepository: RecipeIngredientRepository
) {
    fun getCart(cartId: Int): Cart {
        return cartRepository.findById(cartId)
            .orElseThrow { IllegalArgumentException("Cart not found for id=$cartId") }
    }

    fun getCartItems(cartId: Int): List<CartItem> {
        return cartItemRepository.findByCartId(cartId)
    }

    @Transactional
    fun addRecipeToCart(cartId: Int, recipeId: Int) {
        val cart = getCart(cartId)
        recipeRepository.findById(recipeId)
            .orElseThrow { IllegalArgumentException("Recipe not found for id=$recipeId") }
        val ingredients = recipeIngredientRepository.findByRecipeId(recipeId)
        if (ingredients.isEmpty()) {
            throw IllegalStateException("Recipe $recipeId has no ingredients")
        }

        // Generate new CartItem records
        val currentMaxId = cartItemRepository.findAll().maxOfOrNull { it.id } ?: 0
        var nextId = currentMaxId + 1

        val newItems = ingredients.map { ingredient ->
            CartItem(
                id = nextId++,
                cartId = cartId,
                cart = cart,
                productId = ingredient.product.id,
                product = ingredient.product,
                recipeId = recipeId
            )
        }

        cartItemRepository.saveAll(newItems)

        // Update total
        val totalAdded = ingredients.sumOf { it.product.priceInCents }
        cart.totalInCents += totalAdded
        cartRepository.save(cart)
    }

    @Transactional
    fun removeRecipeFromCart(cartId: Int, recipeId: Int) {
        val cart = getCart(cartId)
        val itemsToRemove = cartItemRepository.findByCartId(cartId)
            .filter { it.recipeId == recipeId }
        if (itemsToRemove.isEmpty()) return

        val totalToRemove = itemsToRemove.sumOf { it.product.priceInCents }
        cartItemRepository.deleteAll(itemsToRemove)

        // Update total
        cart.totalInCents -= totalToRemove
        cartRepository.save(cart)
    }
}
