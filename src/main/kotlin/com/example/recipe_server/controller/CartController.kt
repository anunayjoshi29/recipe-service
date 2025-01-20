package com.example.recipe_server.controller

import com.example.recipe_server.entity.Cart
import com.example.recipe_server.entity.CartItem
import com.example.recipe_server.service.CartService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/carts")
class CartController(
    private val cartService: CartService
) {

    @GetMapping("/{id}")
    fun getCart(@PathVariable id: Int): Map<String, Any> {
        val cart = cartService.getCart(id)
        val items = cartService.getCartItems(id)
        return mapOf(
            "cart_id" to cart.id,
            "total_in_cents" to cart.totalInCents,
            "items" to items.map { it.toDTO() }
        )
    }

    // Post -> /carts/{id}/add_recipe
    @PostMapping("/{id}/add_recipe")
    fun addRecipeToCart(
        @PathVariable("id") cartId: Int,
        @RequestBody body: Map<String, Int>
    ): Cart {
        val recipeId = body["recipe_id"]
            ?: throw IllegalArgumentException("Field 'recipe_id' is required")
        cartService.addRecipeToCart(cartId, recipeId)
        return cartService.getCart(cartId)
    }

    // DELETE -> /carts/{cartId}/recipes/{recipeId}
    @DeleteMapping("/{cartId}/recipes/{recipeId}")
    fun removeRecipeFromCart(
        @PathVariable cartId: Int,
        @PathVariable recipeId: Int
    ): Cart {
        cartService.removeRecipeFromCart(cartId, recipeId)
        return cartService.getCart(cartId)
    }

    private fun CartItem.toDTO(): Map<String, Any?> {
        return mapOf(
            "cart_item_id" to this.id,
            "product_id" to this.product.id,
            "product_name" to this.product.name,
            "price_in_cents" to this.product.priceInCents,
            "recipe_id" to this.recipeId
        )
    }
}
