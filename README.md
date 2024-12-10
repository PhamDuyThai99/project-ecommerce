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
  + Centralizes endpoints to route requests to appropriate services.
  + Validates user authentication and authorization based on the JWT token.

- **ProductManagementService**:
  + Manage product inventory data.
  + Enables specific user to add, retrieve, update or delete products in the inventory.
  + Exposes APIs for internally inventory status checks.

- **CartManagementService**
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
4. Set up the database (MySQL) and configure the application properties
    - username: root
    - password: root
5. Run the application:
    - <updating ...>

# ** Main Business Logic ***
1. User Management Service
    - Users can create a new account with a unique username, password, and role.
    - Passwords are encrypted using BCrypt before storage.
    - New accounts are stored in the User table.

2. AuthService Flow
   - Login:
     - Users log in with a username and password.
     - The system calls the User Service API to fetch the user details by username.
     - If the user exists, the encrypted password from the User Service is compared with the encrypted version of the provided password. If validation fails, an exception is thrown.
     - Upon successful validation:
       - A JWT token is generated containing the username, user ID, role, creation timestamp, and expiry timestamp.
     - This token is stored in the Auth table.
   
   - Logout:
     - When a user logs out, the system validates the JWT token. If valid, the token's expiry timestamp is updated to the current timestamp in the Auth table.
   
   - Token Validation:
     - Provides an API to validate tokens from request headers by verifying:
     - The user exists.
     - The token exists in the Auth table.
     - The token has not expired.

3. Product Service
   - Add New Product: Users can add a product, and their user ID is stored as the product owner.
   - Get Product by ID: Retrieve product details using its ID.
   - Update Product: Modify product details using its ID.
   - Delete Product: Remove a product by its ID.

4. Cart Service
   - Create New Cart:
     - Users can create an empty cart. A user ID is associated with the cart, ensuring each user can only have one active cart at a time.
   
   - Add Product to Cart:
     - Users can add one product (by ID) to the cart at a time.
     - The system calls the Product Service API to verify the product ID and checks stock availability.
     - A product is successfully added once its ID and quantity are validated. Each product is stored in the ProductCart table only once per cart.
     
   - View Cart:
     - Retrieve a cart's product list by querying the ProductCart table with the cart ID. Product details are fetched from the Product Service.

   - Delete Cart:
     - Remove a cart using its ID.
   
5. Payment Service
   - Create Balance:
     - Users must create a balance entity to perform transactions. A new balance defaults to zero.
   
   - Update Balance:
     - Users can update their balance as required.
   
   - Payment Orders:
     - Orders can have three statuses: PENDING, FAILED, or SUCCESS.
     - Four transaction types are supported: WITHDRAW, RELOAD, P2P, and CART.
   - Withdraw Order:
     - Validate the user by calling the User Service.
     - Check if the user has a sufficient balance.
     - If valid, create an order with a PENDING status and return the order ID.
     - Users execute the order by validating the order ID and balance. On success, the status updates to SUCCESS and the balance is debited. Otherwise, the status becomes FAILED.
     
   - Reload Order:
     - Similar to Withdraw Order, but the user balance is credited instead of debited.
     
   - P2P Order:
     - Validate both payer and payee using the User Service.
     - Check their balances and ensure sufficient funds.
     - If valid, create a PENDING order and return the order ID.
     - The payer executes the order. On success, the status updates to SUCCESS, the payer’s balance is debited, and the payee’s balance is credited. Otherwise, the status becomes FAILED.
     
   - Cart Order:
     - The current user creates a cart order by providing the cart ID.
     - Validate the cart ID by calling the Cart Management Service.
     - Verify the user's balance against the cart's total amount.
     - If valid, create P2P transactions for each product owner (retrieved from the Product Service). Each transaction starts with a PENDING status.
     - Upon execution, all related P2P transactions are processed. If successful, their statuses are updated to SUCCESS, and balances for both payer and payees are updated.