package com.example.recipe_server.repository

import com.example.recipe_server.entity.Cart
import org.springframework.data.jpa.repository.JpaRepository

// This interface provides CRUD operations for Cart entities
interface CartRepository : JpaRepository<Cart, Int>
