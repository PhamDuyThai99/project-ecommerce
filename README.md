# E-Commerce Backend System

# **Description**
This is an e-commerce backend system using Spring Boot, build in microservice pattern with multiple functions: Authentication and Authorization, User Management, Product Inventory, Cart Management, Payment and Balance Processing.

# **Services**:

- **UserManagementService**:
  + Manage user's basic profile and role's information
  + Role: Allows authorized user to create, get the role
  + User: Allows authorized user to register, validation user's data, get all or specific user's information, update user's data, delete.
- **AuthService**:
  + Secured the system by jwt token-based authentication
  + Expose apis that support the log-in, log-out, and validate the token
- **ApiGatewayService**:
  + Centralize endpoints for routing to different services
  + Based on user's current jwt token to validate user's authentication and authorization status
- **ProductManagementService**:
  + Manage product's inventory data
  + Allows specific user to add new, get, update or delete product from inventory
  + Expose apis for internally inventory status check
- **CartMangementService**
  + Manage cart and its product of each users
  + Validate user and product's status
- **PaymentManagementService**
  + Manage user balance and inventory 
  + Allows user to transfer money from a user to another user (peer-to-peer transaction)
  + Allows user to check-out a cart transaction
 

