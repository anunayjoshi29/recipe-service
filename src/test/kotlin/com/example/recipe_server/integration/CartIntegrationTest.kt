package com.example.recipe_server.integration

import com.example.recipe_server.entity.*
import com.example.recipe_server.repository.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // optional, if you have a separate application-test.yml
class CartIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var cartRepository: CartRepository

    @Autowired
    lateinit var cartItemRepository: CartItemRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var recipeRepository: RecipeRepository

    @Autowired
    lateinit var recipeIngredientRepository: RecipeIngredientRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        // Clear all tables before each test
        cartItemRepository.deleteAll()
        cartRepository.deleteAll()
        recipeIngredientRepository.deleteAll()
        recipeRepository.deleteAll()
        productRepository.deleteAll()

        // Insert sample data
        val cart = Cart(id = 1, totalInCents = 0)
        cartRepository.save(cart)

        val tomato = Product(id = 10, name = "Tomato", priceInCents = 250)
        val onion = Product(id = 11, name = "Onion", priceInCents = 150)
        productRepository.saveAll(listOf(tomato, onion))

        val recipe = Recipe(id = 99, name = "Tomato Soup")
        recipeRepository.save(recipe)

        val ing1 = RecipeIngredient(
            id = 1,
            recipeId = recipe.id,
            recipe = recipe,
            productId = tomato.id,
            product = tomato
        )
        val ing2 = RecipeIngredient(
            id = 2,
            recipeId = recipe.id,
            recipe = recipe,
            productId = onion.id,
            product = onion
        )
        recipeIngredientRepository.saveAll(listOf(ing1, ing2))
    }

    @Test
    fun `GET carts_1 should return empty cart initially`() {
        mockMvc.perform(get("/carts/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.cart_id").value(1))
            .andExpect(jsonPath("$.total_in_cents").value(0))
            .andExpect(jsonPath("$.items").isEmpty)
    }

    @Test
    fun `POST carts_1 add_recipe should add products to cart`() {
        // When we add recipe_id 99 to cart 1
        val requestBody = mapOf("recipe_id" to 99)
        mockMvc.perform(
            post("/carts/1/add_recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.totalInCents").value(400)) // 250 + 150
    }

    @Test
    fun `DELETE carts_1 recipes_99 should remove products from cart`() {
        // First, add the recipe
        val requestBody = mapOf("recipe_id" to 99)
        mockMvc.perform(
            post("/carts/1/add_recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalInCents").value(400))

        // Now remove it
        mockMvc.perform(delete("/carts/1/recipes/99"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalInCents").value(0))
            .andExpect(jsonPath("$.id").value(1))

        // Double-check the DB
        val itemsAfterDelete = cartItemRepository.findAll()
        assertTrue(itemsAfterDelete.isEmpty(), "CartItem table should be empty after removal.")
    }
}
