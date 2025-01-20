package com.example.recipe_server.repository

import com.example.recipe_server.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Int>
