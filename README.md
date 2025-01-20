# Buy Recipes - README

This application extends a simple ecommerce platform\’s \“Cart\” functionality to support recipes. Each recipe is made up of one or more product ingredients, and users can add or remove entire recipes in their carts.

## Table of Contents

- [Overview](#overview)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Local Setup (Docker Compose)](#local-setup-docker-compose)
    - [Manual Local Setup (Without Docker Compose)](#manual-local-setup-without-docker-compose)
- [REST API Endpoints](#rest-api-endpoints)
    - [GET /recipes](#get-recipes)
    - [GET /carts/:id](#get-cartsid)
    - [POST /carts/:id/add_recipe](#post-cartsidadd_recipe)
    - [DELETE /carts/:id/recipes/:id](#delete-cartsidrecipesid)

## Overview

- Recipes consist of a name and a list of products (ingredients).
- A Cart can now hold both individual products (existing feature) and recipes (new feature).
- When you add a recipe to the cart, it automatically adds all the products in that recipe.
- When you remove a recipe from the cart, it removes all the associated product items from the cart.

## Getting Started

### Prerequisites

- Docker & Docker Compose (if using the provided compose setup)
- Java 17+ and Gradle (if running locally without Docker)
- PostgreSQL (or another DB) if running locally

### Local Setup (Docker Compose)

1. Clone this repository:
    ```bash
    git clone git@github.com:anunayjoshi29/recipe-service.git
    cd recipe-service
    ```

2. Run:
   This will start the application and a PostgreSQL database in separate containers. Also, it will create the required tables and insert some sample data from file init.sql.
    ```bash
    docker compose up --build
    ```

3. After the containers start, the app is available at: [http://localhost:8080](http://localhost:8080).

### Manual Local Setup (Without Docker Compose)

1. Create a PostgreSQL (or any other) database called `recipes_db` (or any name).
2. Configure `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` in `application.yml` or via environment variables.

3. Run:
    ```bash
    ./gradlew bootRun
    ```
   Test(Both unit and integration tests) can be run using the below command:
    ```bash
    ./gradlew test
    ```

4. The application starts on [http://localhost:8080](http://localhost:8080).

## REST API Endpoints

Below are the four main endpoints for the \“Buy Recipes\” feature, along with example requests and responses.

### 1. GET /recipes

**Description:**
Retrieves a list of all recipes in the database.

- **URL:** `/recipes`
- **Method:** `GET`
- **Query/Path Params:** None
- **Request Body:** N/A
- **Response:** JSON array of recipes

**Example:**

```bash
curl http://localhost:8080/recipes
```

**Sample Response (JSON):**

```json
[
  {
    "id": 99,
    "name": "Tomato Soup"
  },
  {
    "id": 100,
    "name": "Guacamole"
  }
]
```

### 2. GET /carts/:id

**Description:**
Retrieves the details of a specific Cart by its ID, including its current total and items.

- **URL:** `/carts/{cartId}`
- **Method:** `GET`
- **Path Param:**
    - `cartId` (e.g., 1)
- **Request Body:** N/A
- **Response:** JSON object representing the cart

**Example:**

```bash
curl http://localhost:8080/carts/1
```

**Sample Response (JSON):**

```json
{
  "cart_id": 1,
  "total_in_cents": 400,
  "items": [
    {
      "cart_item_id": 101,
      "product_id": 10,
      "product_name": "Tomato",
      "price_in_cents": 250,
      "recipe_id": 99
    },
    {
      "cart_item_id": 102,
      "product_id": 11,
      "product_name": "Onion",
      "price_in_cents": 150,
      "recipe_id": 99
    }
  ]
}
```
Notice that some items might have a `recipe_id`, indicating they were added as part of that recipe.

### 3. POST /carts/:id/add_recipe

**Description:**
Adds a recipe (and all its ingredient products) to the specified cart.

- **URL:** `/carts/{cartId}/add_recipe`
- **Method:** `POST`
- **Path Param:**
    - `cartId` (e.g., 1)
- **Request Body:** JSON object containing the `recipe_id`
- **Response:** JSON representation of the updated cart

**Request Body Example (JSON):**

```json
{
  "recipe_id": 99
}
```

**Example Curl:**

```bash
curl -X POST -H "Content-Type: application/json" \
  -d '{"recipe_id":99}' \
  http://localhost:8080/carts/1/add_recipe
```

**Sample Response (JSON):**

```json
{
  "id": 1,
  "totalInCents": 400
}
```
This indicates the cart total has now changed based on the ingredients of recipe #99.

### 4. DELETE /carts/:cartId/recipes/:recipeId

**Description:**
Removes a recipe from a cart, deleting all items associated with that recipe. The cart\’s total is also updated.

- **URL:** `/carts/{cartId}/recipes/{recipeId}`
- **Method:** `DELETE`
- **Path Params:**
    - `cartId` (e.g., 1)
    - `recipeId` (e.g., 99)
- **Request Body:** N/A
- **Response:** JSON object representing the updated cart

**Example:**

```bash
curl -X DELETE http://localhost:8080/carts/1/recipes/99
```

**Sample Response (JSON):**

```json
{
  "id": 1,
  "totalInCents": 0
}
```
Here, the items that belonged to recipe #99 have been removed, and `totalInCents` is updated accordingly.

## Usage Summary

- Create or view recipes (via DB scripts or direct DB inserts)
- `GET /recipes` to list them
- `GET /carts/{id}` to view a specific cart\’s current items and total
- `POST /carts/{id}/add_recipe` with `{ "recipe_id": ... }` to add a full recipe
- `DELETE /carts/{cartId}/recipes/{recipeId}` to remove that recipe from the cart

With these steps, you can test the entire \“Buy Recipes\” workflow:

1. List available recipes
2. Check the cart (initially empty)
3. Add a recipe to the cart
4. Remove the recipe from the cart if desired

Enjoy exploring the \“Buy Recipes\” feature!
