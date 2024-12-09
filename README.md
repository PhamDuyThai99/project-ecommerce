# E-Commerce Backend System

# **Description**
This is an e-commerce backend system using Spring Boot, build in microservice pattern with multiple functions: Authentication and Authorization, User Management, Product Inventory, Cart Management, Payment and Balance Processing.

# **Services**

- **UserManagementService**:
  + Manages user's basic profile and role's information.
  + Role Management: Enables authorized user to create, and retrieve roles.
  + User Management: Allows authorized user to register new accounts, validation user data, get specific or all user information, update profile, delete users.

- **AuthService**:
  + Secures the system using jwt token-based authentication.
  + Provides APIs that supports the log-in, log-out, and token validation.

- **ApiGatewayService**:
  + Centralizes endpoints to route requests to approriate services.
  + Validates user authentication and authorization based on the JWT token.

- **ProductManagementService**:
  + Manage product inventory data.
  + Enables specific user to add, retrieve, update or delete products in the inventory.
  + Exposes APIs for internally inventory status checks.

- **CartMangementService**
  + Manages user carts and associated products
  + Validates the status of users and products in the cart

- **PaymentManagementService**
  + Manage user balances and related financial transactions
  + Enables peer-to-peer money between system users.
  + Enables users to check-out and complete cart transaction, payment is transfered to product's owner's balance.

# **Technology Used**
- **Spring Boot** (for building microservices)
- **Spring Cloud Gatewa**y (for API gateway routing and filtering)
- **Spring Security** (for authentication and authorization)
- **Spring Data JPA** (for database access)
- **JWT** (for token-based authentication)
- **MySQL** (for the database)
- **JUnit**, **Mockito** (for unit testing)
- **Swagger** (for API documentation)

# **Installation**
1. Clone the repository
    - git clone PhamDuyThai99/project-ecommerce
2. Navigate to the project directory
3. Install dependencies and build the project
    - ./mvnw clean install
4. Setup the database (MySQL) and configure the application properties
    - username: root
    - password: root
5. Run the application:
    - ./mvnw spring-boot:run

