# SpringEcom

A lightweight Spring Boot example e-commerce backend demonstrating a layered architecture (controllers, services, repositories, entities, DTOs). This repository is intended as a learning starter or small demo backend that you can extend with a frontend.

Badges: (add CI/coverage badges as needed)

---

## Quick overview

- Language: Java (Maven)
- Framework: Spring Boot
- Purpose: Demonstrate REST API design, domain modeling, and simple order/product flows.
- Main concepts:
  - Controllers: REST endpoints
  - Services: Business logic
  - Repositories: Data persistence (in-memory / JPA)
  - DTOs: API request/response models separate from entities

---

## Quick start (Windows)

1. Run the app:
   - `.\mvnw.cmd spring-boot:run`
2. Build a runnable jar:
   - `.\mvnw.cmd clean package`
   - `java -jar target\*.jar`
3. Run tests:
   - `.\mvnw.cmd test`

Configuration:
- Application settings: `src/main/resources/application.properties`

---

## Project structure

Top-level:
- `pom.xml` — Maven build file
- `mvnw`, `mvnw.cmd` — Maven wrappers
- `README.md` — this file
- `HELP.md` — project notes

Source:
- `src/main/java/com/felfel/springecom/`
  - `SpringEcomApplication.java` — application entry point
  - `controller/`
    - `HelloController.java` — health/hello endpoint
    - `ProductController.java` — product endpoints
    - `OrderController.java` — order endpoints
  - `service/` — business logic: `ProductService.java`, `OrderService.java`
  - `repositry/` — data access: `ProductRepo.java`, `OrderRepo.java`
  - `entity/` — domain models: `Product.java`, `Order.java`, `OrderItem.java`
  - `DTO/` — API models: `OrderRequest.java`, `OrderResponse.java`, `OrderItemRequest.java`, `OrderItemResponse.java`
- `src/main/resources/static/` — optional frontend assets (place a simple UI here)
- `src/main/resources/templates/` — server-side templates (optional)

Tests:
- `src/test/java/com/felfel/springecom/SpringEcomApplicationTests.java`

---

## How the project works (request flow)

1. Client calls controller endpoints (`/api/products`, `/api/orders`, etc.).
2. Controller validates and maps request DTOs, then calls Service layer.
3. Service contains business logic (product price, inventory checks, order totals) and uses Repositories to persist or fetch Entities.
4. Repositories interact with the database (or in-memory store) and return entities.
5. Controller maps service responses to response DTOs and returns JSON.

---

## API Reference (examples)

Base path: `/api` (adjust if controllers use another mapping)

1. Health
   - GET `/api/hello`
   - Response: `200 OK`
     ```json
     { "message": "Hello from SpringEcom" }
     ```

2. Products
   - GET `/api/products`
     - Returns list of products.
     - Response example:
       ```json
       [
         { "id": 1, "name": "Widget", "price": 19.99, "description": "Small widget" },
         { "id": 2, "name": "Gadget", "price": 29.99, "description": "Useful gadget" }
       ]
       ```
   - GET `/api/products/{id}`
     - Returns a single product or `404`.
   - (Optional) POST `/api/products` — create product (if controller exposes it)

3. Orders
   - POST `/api/orders`
     - Create a new order.
     - Request example:
       ```json
       {
         "customerName": "Jane Doe",
         "items": [
           { "productId": 1, "quantity": 2 },
           { "productId": 2, "quantity": 1 }
         ]
       }
       ```
     - Response example (`201 Created`):
       ```json
       {
         "orderId": 1001,
         "status": "CREATED",
         "total": 69.97,
         "items": [
           { "productId": 1, "name": "Widget", "quantity": 2, "lineTotal": 39.98 },
           { "productId": 2, "name": "Gadget", "quantity": 1, "lineTotal": 29.99 }
         ]
       }
       ```
   - GET `/api/orders/{id}` — retrieve order by id

Notes:
- Adjust field names to match DTOs in `src/main/java/com/felfel/springecom/entity` and `DTO`.
- Use `400 Bad Request` for invalid payloads, `404 Not Found` for missing resources.

---

## Example PowerShell `curl` / `Invoke-RestMethod` (Windows)

Get products:
- PowerShell:
  ```powershell
  Invoke-RestMethod -Method GET$body = @{
    customerName = "Jane Doe"
    items = @(@{ productId=1; quantity=2 }, @{ productId=2; quantity=1 })
  } | ConvertTo-Json
  Invoke-RestMethod -Method POST -Uri http://localhost:8080/api/orders -Body $body -ContentType 'application/json'curl -X POST http://localhost:8080/api/orders -H "Content-Type: application/json" -d "{\"customerName\":\"Jane\",\"items\":[{\"productId\":1,\"quantity\":2}]}"<!doctype html>
  <html>
  <head>
    <meta charset="utf-8" />
    <title>SpringEcom Demo UI</title>
    <style>
      body{font-family:Segoe UI,Roboto,Arial;margin:24px}
      .product{border:1px solid #ddd;padding:12px;margin-bottom:8px;border-radius:6px}
      button{background:#0066ff;color:#fff;border:none;padding:8px 12px;border-radius:4px}
    </style>
  </head>
  <body>
    <h1>SpringEcom - Demo UI</h1>
    <div id="products"></div>
    <script>
      async function loadProducts(){
        const res = await fetch('/api/products');
        const items = await res.json();
        const root = document.getElementById('products');
        root.innerHTML = '';
        items.forEach(p=>{
          const el = document.createElement('div');
          el.className = 'product';
          el.innerHTML = `<strong>${p.name}</strong> — \$${p.price.toFixed(2)}<p>${p.description||''}</p>`;
          const btn = document.createElement('button');
          btn.textContent = 'Buy 1';
          btn.onclick = ()=> createOrder(p.id);
          el.appendChild(btn);
          root.appendChild(el);
        });
      }
      async function createOrder(productId){
        const body = { customerName: 'Demo buyer', items: [{ productId, quantity: 1 }] };
        const res = await fetch('/api/orders', { method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(body) });
        if(res.ok) alert('Order created');
        else alert('Order failed: ' + res.status);
      }
      loadProducts();
    </script>
  </body>
  </html> -Uri http://localhost:8080/api/products
  Place this file at src/main/resources/static/index.html to serve a simple UI from the Spring Boot app.
<hr></hr>
Where to look next
Inspect controllers for exact request mappings: src/main/java/com/felfel/springecom/controller/
Inspect DTOs for exact field names: src/main/java/com/felfel/springecom/entity/DTO/
Add OpenAPI/Swagger for automatic API docs (springdoc-openapi)
<hr></hr>