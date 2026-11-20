# Implementation Plan: Show Password Toggle and Forgot Password Feature

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a password visibility toggle ("eye" icon) on the login screen and implement a complete "Forgot Password" feature with a dedicated reset password page and database integration.

**Architecture:** 
1. Database/DAO/Service layer: Add password update capability in `UserDAO` and `UserService` using PBKDF2 hashing via `PasswordUtil`.
2. UI - Show Password Toggle: Embed `PasswordField` and `TextField` inside a `StackPane` with a visibility toggle button in `login.fxml` managed by `LoginController`.
3. UI - Forgot Password: Create `forgot-password.fxml` and `ForgotPasswordController`, with a link in `login.fxml` that uses `SceneNavigator` to switch between screens.

**Tech Stack:** JavaFX 27, MariaDB/MySQL JDBC, Java 21+

**Spec:** Option A from brainstorming: Show/Hide toggle on login screen, and a dedicated Forgot Password scene (`forgot-password.fxml`) to verify email, enter a new password with confirmation, and update the database.

## Global Constraints

- Keep the warm CoffeeHub color palette (`#F7F1E8` background, `#56301F` / `#70452F` brown buttons, `#E2D3C5` borders).
- Use PBKDF2 password hashing via `PasswordUtil.hashPassword()`.
- Keep controller-FXML method naming consistent with existing conventions (`handleLogin`, `handleRegister`, etc.).
- Ensure both `PasswordField` and plain `TextField` stay synchronized when typing or toggling visibility.

## Review Focus

1. Show/hide toggle synchronization: typing in visible mode must update hidden field value and vice versa.
2. Email not found during reset: proper error message shown on UI without crashing.
3. Password length (< 8) and confirmation mismatch validation on reset.
4. Navigation between Login and Forgot Password pages works seamlessly with `SceneNavigator`.
5. Resetting password updates database record and allows immediate login with the new password.

---

### Task 1: Add Password Update in Repository and Service Layer

**Files:**
- Modify: `src/main/java/com/voidsyntax/coffeehub/repository/UserDAO.java`
- Modify: `src/main/java/com/voidsyntax/coffeehub/service/UserService.java`

- [x] Add `perbaruiPassword(String email, String passwordHash)` in `UserDAO.java`.
- [x] Add `resetPassword(String email, String newPassword, String confirmPassword)` in `UserService.java` with validation:
  - Check non-empty email, newPassword, confirmPassword.
  - Check password match (`newPassword.equals(confirmPassword)`).
  - Check password length (`newPassword.length() >= 8`).
  - Check if user exists (`cariBerdasarkanEmail(email) != null`).
  - Hash password with `PasswordUtil.hashPassword(newPassword)`.
  - Execute `userDAO.perbaruiPassword(email, passwordHash)`.

---

### Task 2: Implement Show/Hide Password Toggle and Forgot Password Link in Login Screen

**Files:**
- Modify: `src/main/resources/fxml/login.fxml`
- Modify: `src/main/java/com/voidsyntax/coffeehub/controller/LoginController.java`

- [x] In `login.fxml`:
  - Wrap password field in a `StackPane` containing:
    - `PasswordField fx:id="passwordField"`
    - `TextField fx:id="passwordTextField"` (managed=false, visible=false)
    - `Button fx:id="togglePasswordButton"` with text "👁" positioned at `Pos.CENTER_RIGHT`
  - Add a "Forgot Password?" button or hyperlink below password field calling `onAction="#handleForgotPassword"`.
- [x] In `LoginController.java`:
  - Bind `passwordField.textProperty()` bidirectionally or sync listeners with `passwordTextField.textProperty()`.
  - Add `handleTogglePassword()` to flip visibility between `passwordField` and `passwordTextField` and change icon ("👁" / "🙈").
  - Update `handleLogin(ActionEvent event)` to read password from `passwordField.getText()` or `passwordTextField.getText()`.
  - Add `handleForgotPassword(ActionEvent event)` calling `SceneNavigator.pindahHalaman(event, "/fxml/forgot-password.fxml")`.

---

### Task 3: Create Forgot Password View and Controller

**Files:**
- Create: `src/main/resources/fxml/forgot-password.fxml`
- Create: `src/main/java/com/voidsyntax/coffeehub/controller/ForgotPasswordController.java`

- [x] Create `forgot-password.fxml`:
  - Header: Logo, title "CoffeeHub", subtitle "Reset Your Password".
  - Form card:
    - Email `TextField fx:id="emailField"`
    - New Password `PasswordField fx:id="newPasswordField"` + `TextField fx:id="newPasswordTextField"` + toggle button
    - Confirm Password `PasswordField fx:id="confirmPasswordField"` + `TextField fx:id="confirmPasswordTextField"` + toggle button
    - Reset Button calling `handleResetPassword`
    - Feedback label `fx:id="messageLabel"`
  - Footer: "Remember your password? Back to Login" button calling `handleBackToLogin`.
- [x] Create `ForgotPasswordController.java`:
  - Implement validation, call `userService.resetPassword(...)`.
  - Show green message on success, red message on error.
  - Implement `handleBackToLogin(ActionEvent event)` using `SceneNavigator.pindahHalaman(event, "/fxml/login.fxml")`.

---

### Task 4: Fix Java Compatibility Issue in CustomerController

**Files:**
- Modify: `src/main/java/com/voidsyntax/coffeehub/controller/CustomerController.java:46`

- [x] Change `dashboardContent = contentArea.getChildren().getFirst();` to `get(0)` for consistency with `AdminController` and better compatibility.
