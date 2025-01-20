package com.example.recipe_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RecipeServerApplication

fun main(args: Array<String>) {
	runApplication<RecipeServerApplication>(*args)
}
