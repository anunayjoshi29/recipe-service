package com.example.recipe_server.service

import com.example.recipe_server.entity.Cart
import com.example.recipe_server.entity.CartItem
import com.example.recipe_server.entity.Product
import com.example.recipe_server.entity.Recipe
import com.example.recipe_server.entity.RecipeIngredient
import com.example.recipe_server.repository.*
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional

class CartServiceTest {

    private val cartRepository = mockk<CartRepository>()
    private val cartItemRepository = mockk<CartItemRepository>()
    private val productRepository = mockk<ProductRepository>()
    private val recipeRepository = mockk<RecipeRepository>()
    private val recipeIngredientRepository = mockk<RecipeIngredientRepository>()

    private lateinit var cartService: CartService

    @BeforeEach
    fun setup() {
        cartService = CartService(
            cartRepository,
            cartItemRepository,
            productRepository,
            recipeRepository,
            recipeIngredientRepository
        )
    }

    @Test
    fun `addRecipeToCart should add items and update cart total`() {
        // Given
        val cartId = 1
        val recipeId = 99

        val cart = Cart(id = cartId, totalInCents = 1000)
        val recipe = Recipe(id = recipeId, name = "Tomato Soup")
        val productTomato = Product(id = 10, name = "Tomato", priceInCents = 250)
        val productOnion = Product(id = 11, name = "Onion", priceInCents = 150)

        val recipeIngredientTomato = RecipeIngredient(
            id = 1,
            recipeId = recipeId,
            recipe = recipe,
            productId = productTomato.id,
            product = productTomato
        )
        val recipeIngredientOnion = RecipeIngredient(
            id = 2,
            recipeId = recipeId,
            recipe = recipe,
            productId = productOnion.id,
            product = productOnion
        )
        val ingredients = listOf(recipeIngredientTomato, recipeIngredientOnion)

        // Mock repository calls
        every { cartRepository.findById(cartId) } returns Optional.of(cart)
        every { recipeRepository.findById(recipeId) } returns Optional.of(recipe)
        every { recipeIngredientRepository.findByRecipeId(recipeId) } returns ingredients

        // When cartItemRepository.findAll() is called to find the max ID:
        every { cartItemRepository.findAll() } returns emptyList()

        // For saving cart items
        every { cartItemRepository.saveAll(any<List<CartItem>>()) } returns emptyList()

        // For saving the cart
        every { cartRepository.save(any<Cart>()) } returns cart

        // When
        cartService.addRecipeToCart(cartId, recipeId)

        // Then
        // Cart should have total = 1000 + 250 + 150 = 1400
        assertEquals(1400, cart.totalInCents)

        // Verify that cartItemRepository.saveAll was called with 2 items
        verify(exactly = 1) {
            cartItemRepository.saveAll(withArg<List<CartItem>> { items ->
                assertEquals(2, items.size)
                assertTrue(items.any { c -> c.productId == 10 })
                assertTrue(items.any { c -> c.productId == 11 })
            })
        }

        // Verify the cart was saved
        verify(exactly = 1) { cartRepository.save(cart) }
    }

    @Test
    fun `removeRecipeFromCart should remove items and update total`() {
        // Given
        val cartId = 1
        val recipeId = 99

        val cart = Cart(id = cartId, totalInCents = 1400)
        every { cartRepository.findById(cartId) } returns Optional.of(cart)

        val productTomato = Product(id = 10, name = "Tomato", priceInCents = 250)
        val productOnion = Product(id = 11, name = "Onion", priceInCents = 150)

        // Items associated with the recipe
        val cartItem1 = CartItem(
            id = 1, cartId = cartId, cart = cart,
            productId = 10, product = productTomato, recipeId = recipeId
        )
        val cartItem2 = CartItem(
            id = 2, cartId = cartId, cart = cart,
            productId = 11, product = productOnion, recipeId = recipeId
        )
        // Suppose cart has 2 items from the recipe
        every { cartItemRepository.findByCartId(cartId) } returns listOf(cartItem1, cartItem2)

        // Deletion mocks
        every { cartItemRepository.deleteAll(any<List<CartItem>>()) } just Runs
        every { cartRepository.save(any<Cart>()) } returns cart

        // When
        cartService.removeRecipeFromCart(cartId, recipeId)

        // Then
        // totalInCents should be reduced by 400
        assertEquals(1000, cart.totalInCents)  // 1400 - (250+150) = 1000

        // Ensure we called the repositories as expected
        verify(exactly = 1) { cartItemRepository.deleteAll(listOf(cartItem1, cartItem2)) }
        verify(exactly = 1) { cartRepository.save(cart) }
    }
}
