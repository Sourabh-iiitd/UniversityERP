# University ERP System

A robust, secure, and comprehensive ERP Project built in Java to streamline university management.

## Key Features & Security

* **Role-Based Access Control (RBAC):** Dedicated interfaces for `Admin`, `Student`, and `Instructor`. 
* **Secure Unified Login:** A single entry point for all users. The system dynamically routes users to their respective dashboards based on their internal database `userType`. This completely eliminates the risk of users manually forcing their way into restricted views.
* **Cryptographic Security:** Zero plaintext passwords. All user credentials are encrypted using the `password4j` library's `bcrypt2` algorithm before being stored in the database.

## Architecture & Package Structure

The project is built with a clean, modular architecture separating the UI, business logic, and database layers:

* **`edu.univ.erp.ui`**: Contains all graphical user interfaces, neatly divided by user roles.
* **`edu.univ.erp.service`**: The brains of the application. Contains the backend logic and methods for executing database transactions.
* **`edu.univ.erp.data`**: Core data models and structuring classes.
* **`edu.univ.erp.domain`**: Handles application state, primarily the `User` class for session management.
* **`edu.univ.erp.util`**: Core utilities including `DBConnection` (manages the application's database lifecycle) and `DatabaseInit` (first-run setup).
* **`resources/`**: Houses `db.properties` (database credentials) and `db/migrations` (three essential SQL files for automated schema generation).

## Getting Started

This project uses Maven for dependency management and was built using IntelliJ IDEA. Running it through IntelliJ is highly recommended.

### Prerequisites
* Java Development Kit (JDK)
* MariaDB Server
* IntelliJ IDEA 

### Installation & Setup

1. **Configure the Database:** Ensure your MariaDB server is running. Open `src/main/resources/db.properties` and update it with your local database URL and credentials.
2. **Initialize Schema:** Navigate to `src/main/java/edu/univ/erp/util/DatabaseInit.java` and run the file. This script will automatically set up the required tables and populate them with dummy data.
3. **Verify:** Check your terminal/output console to ensure the database initialization ran without any errors.
4. **Launch:** Open `src/main/java/edu/univ/erp/Main.java` and run it to start the application.

---
### Developers:
**Sourabh Kashyap** (2024563)  
*With collaboration from* **Pratyaksh Kumar** (2024431)
