# Authentication App

This is a Spring Boot app that lets users register, log in, and reset their password via email. It uses a simple in-memory database (H2) and sends emails for password resets. You can test it using Postman. Follow these steps to set it up and run it on a new computer.

## What You Need
- **Java 17**: Download from [Adoptium](https://adoptium.net/).
- **Maven**: Download from [Maven](https://maven.apache.org/download.cgi) or install via your package manager (e.g., `brew install maven` on Mac).
- **Postman**: Download from [Postman](https://www.postman.com/downloads/).
- **IDE**: IntelliJ IDEA (Community Edition) or Eclipse (free).
- **Gmail or Mailtrap Account**: For sending password reset emails.

## Setup Steps
1. **Get the Project**
   - Copy the project folder (`auth-service`) to your computer.
   - Open it in your IDE (e.g., IntelliJ IDEA: `File > Open`, select the folder).

2. **Check Project Files**
   - The project uses this structure:
     ```
     auth-service/
     ├── src/
     │   ├── main/
     │   │   ├── java/com/spring/auth_service/
     │   │   │   ├── AuthServiceApplication.java
     │   │   │   ├── controller/AuthController.java
     │   │   │   ├── dto/ (data files)
     │   │   │   ├── entity/ (database files)
     │   │   │   ├── repository/ (database access)
     │   │   │   ├── service/ (business logic)
     │   │   │   ├── security/ (authentication)
     │   │   │   └── util/ (JWT tools)
     │   │   └── resources/
     │   │       └── application.properties
     ├── pom.xml
     └── README.md
     ```

3. **Configure Email Settings**
   - Open `src/main/resources/application.properties`.
   - For Gmail:
     - Set `spring.mail.username` to your Gmail address (e.g., `example@gmail.com`).
     - Create an App Password:
       - Go to [Google Account](https://myaccount.google.com/security).
       - Enable 2-Step Verification.
       - Go to "Security" > "App passwords", select "Mail" and "Other (Spring Boot)", and generate a 16-character password.
       - Set `spring.mail.password` to this App Password.
   - For Mailtrap (easier for testing):
     - Sign up at [Mailtrap](https://mailtrap.io/).
     - Create an inbox and find SMTP credentials (under "Integrations" > "SMTP").
     - Set:
       ```
       spring.mail.host=smtp.mailtrap.io
       spring.mail.port=587
       spring.mail.username=your-mailtrap-username
       spring.mail.password=your-mailtrap-password
       ```
   - Set `jwt.secret` to a 64-character random string (e.g., `your-secure-jwt-secret-key-1234567890abcdefghijklmnopqrstuvwxyz1234567890abcdefghijk`).

4. **Build the Project**
   - Open a terminal in the project folder (`auth-service`).
   - Run:
     ```bash
     mvn clean install
     ```
   - This downloads needed libraries and builds the app. You should see `BUILD SUCCESS`.

5. **Run the App**
   - In the terminal, run:
     ```bash
     mvn spring-boot:run
     ```
   - Or, in your IDE, right-click `AuthServiceApplication.java` and select "Run".
   - The app starts at `http://localhost:8080`.

## Testing the App
Use Postman to test the app’s features. Create a new collection in Postman called "Auth Service".

1. **Register a User**
   - **Method**: POST
   - **URL**: `http://localhost:8080/api/auth/register`
   - **Body** (raw, JSON):
     ```json
     {
         "name": "John",
         "surname": "Doe",
         "email": "john.doe@example.com",
         "phone": "1234567890",
         "password": "password123"
     }
     ```
   - **Result**: You should see `"User registered successfully"`.

2. **Log In**
   - **Method**: POST
   - **URL**: `http://localhost:8080/api/auth/login`
   - **Body** (raw, JSON):
     ```json
     {
         "username": "john.doe@example.com",
         "password": "password123"
     }
     ```
   - **Result**: You get a long token (JWT) like `eyJhbGciOiJIUzUxMiJ9...`.

3. **Forgot Password**
   - **Method**: POST
   - **URL**: `http://localhost:8080/api/auth/forgot-password`
   - **Body** (raw, JSON):
     ```json
     {
         "email": "john.doe@example.com"
     }
     ```
   - **Result**: You see `"New password sent to email"`. Check your Gmail inbox (or spam) or Mailtrap inbox for the new password.

4. **Test Protected Endpoint**
   - **Method**: GET
   - **URL**: `http://localhost:8080/api/auth/test`
   - **Headers**: Add `Authorization: Bearer <JWT_TOKEN>` (use the token from login).
   - **Result**: You see `"Protected endpoint accessed"`.

## Check the Database
- Open `http://localhost:8080/h2-console`.
- Use:
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (leave blank)
- Run `SELECT * FROM users;` to see registered users.

## Troubleshooting
- **Email Not Sent**: Check `application.properties` for correct SMTP settings. Use Mailtrap if Gmail doesn’t work.
- **Login Fails**: Ensure the email/phone and password match a registered user. Check the H2 database.
- **Errors**: Run `mvn clean install -e -X` for details and ask for help with the error log.
- **Port Conflict**: If `8080` is busy, add `server.port=8081` to `application.properties`.

## Learn More
- Try tutorials on [Spring Boot](https://spring.io/guides/gs/spring-boot/).
- Learn about JWT at [jwt.io](https://jwt.io/).
- Explore Postman for API testing.

Happy coding!