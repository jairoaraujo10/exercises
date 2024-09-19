# Authentication, User Management, and Exercise Management API Documentation

This document provides comprehensive information about the Authentication, User Management, and Exercise Management API endpoints, including their functionalities, request and response structures, and possible status codes.

## Table of Contents
1. [Authentication API Overview](#authentication-api-overview)
2. [User Management API Overview](#user-management-api-overview)
3. [Exercise Management API Overview](#exercise-management-api-overview)
4. [Endpoints](#endpoints)
    - [Authentication Endpoints](#authentication-endpoints)
        - [1. User Login](#1-user-login)
        - [2. Request Password Reset](#2-request-password-reset)
        - [3. Reset Password](#3-reset-password)
    - [User Management Endpoints](#user-management-endpoints)
        - [4. Get User by ID](#4-get-user-by-id)
        - [5. Create User](#5-create-user)
        - [6. Delete User](#6-delete-user)
        - [7. Search Users](#7-search-users)
    - [Exercise Management Endpoints](#exercise-management-endpoints)
        - [8. Create Exercise](#8-create-exercise)
        - [9. Get Exercise by ID](#9-get-exercise-by-id)
        - [10. Update Exercise](#10-update-exercise)
        - [11. Delete Exercise](#11-delete-exercise)
        - [12. Search Exercises](#12-search-exercises)
5. [Error Responses](#error-responses)
6. [Authentication](#authentication)
7. [Notes](#notes)

## Authentication API Overview
The Authentication API handles user authentication processes, including logging in, requesting password resets, and resetting passwords. It ensures secure access to protected resources by issuing and validating tokens.

## User Management API Overview
The User Management API allows for managing user data within the system. It provides functionalities to retrieve user information, create new users, delete existing users, and search for users based on specific criteria.

## Exercise Management API Overview
The Exercise Management API facilitates the creation, retrieval, updating, deletion, and searching of exercises within the system. It enables authorized users to manage exercise content efficiently, ensuring that exercises are organized, accessible, and up-to-date.

## Endpoints

### Authentication Endpoints

#### 1. User Login
**Endpoint:** `/auth/login`
**Method:** `POST`
**Description:** Authenticates a user using their email and password. Upon successful authentication, returns a token for authorized access.

**Request:**
- **Headers:**
    - `Content-Type: application/json`
- **Body:**
    ```json
    {
        "email": "string",
        "password": "string"
    }
    ```

**Responses:**
- **200 OK:**
    - **Body:**
    ```json
    {
        "token": "string"
    }
    ```
- **400 Bad Request:**
    - **Body:**
    ```json
    {
        "message": "string"
    }
    ```
- **401 Unauthorized:**
    - **Body:**
    ```json
    {
        "message": "Invalid credentials"
    }
    ```

#### 2. Request Password Reset
**Endpoint:** `/auth/reset-password/request`
**Method:** `POST`
**Description:** Initiates a password reset process for the user by sending a reset link or code to the provided email.

**Request:**
- **Headers:**
    - `Content-Type: application/json`
- **Body:**
    ```json
    {
        "email": "string"
    }
    ```

**Responses:**
- **204 No Content**

- **400 Bad Request:**
    - **Body:**
    ```json
    {
        "message": "Missing email"
    }
    ```

#### 3. Reset Password
**Endpoint:** `/auth/reset-password`
**Method:** `POST`
**Description:** Resets the user's password using a valid token.

**Request:**
- **Headers:**
    - `Content-Type: application/json`
    - `Authorization: Bearer <token>`
- **Body:**
    ```json
    {
        "password": "string"
    }
    ```

**Responses:**
- **200 OK:**
    - **Body:**
    ```json
    {
        "message": "Password reset successfully"
    }
    ```

- **400 Bad Request:**
- **401 Unauthorized**
- **404 Not Found**

### User Management Endpoints

#### 4. Get User by ID
**Endpoint:** `/user/{id}`
**Method:** `GET`
**Description:** Retrieves a user by their ID.

**Request:**
- **Headers:**
    - `Authorization: Bearer <token>`

**Responses:**
- **200 OK:** Returns the user details.
- **401 Unauthorized**
- **404 Not Found**

#### 5. Create User
**Endpoint:** `/user`
**Method:** `POST`
**Description:** Creates a new user.

**Request:**
- **Headers:**
    - `Content-Type: application/json`
    - `Authorization: Bearer <token>`
- **Body:**
    ```json
    {
        "name": "string",
        "email": "string"
    }
    ```

**Responses:**
- **201 Created**
- **400 Bad Request**
- **401 Unauthorized**

#### 6. Delete User
**Endpoint:** `/user/{id}`
**Method:** `DELETE`
**Description:** Deletes a user by ID.

**Request:**
- **Headers:**
    - `Authorization: Bearer <token>`

**Responses:**
- **204 No Content**
- **401 Unauthorized**
- **404 Not Found**

#### 7. Search Users
**Endpoint:** `/user/search`
**Method:** `POST`
**Description:** Searches users based on provided criteria.

**Request:**
- **Headers:**
    - `Content-Type: application/json`
    - `Authorization: Bearer <token>`

**Responses:**
- **200 OK**

### Exercise Management Endpoints

#### 8. Create Exercise
**Endpoint:** `/exercise`
**Method:** `POST`
**Description:** Creates a new exercise.

**Request:**
- **Headers:**
    - `Content-Type: application/json`
    - `Authorization: Bearer <token>`
- **Body:**
    ```json
    {
        "title": "string",
        "description": "string",
        "tags": [{"value": "string"}],
        "possibleAnswers": ["string"],
        "correctAnswerIndex": "integer"
    }
    ```

**Responses:**
- **201 Created**
- **400 Bad Request**
- **401 Unauthorized**

#### 9. Get Exercise by ID
**Endpoint:** `/exercise/{id}`
**Method:** `GET`
**Description:** Retrieves an exercise by its ID.

**Request:**
- **Headers:**
    - `Authorization: Bearer <token>`

**Responses:**
- **200 OK**
- **401 Unauthorized**
- **404 Not Found**

#### 10. Update Exercise
**Endpoint:** `/exercise/{id}`
**Method:** `PUT`
**Description:** Updates an exercise by its ID.

**Request:**
- **Headers:**
    - `Content-Type: application/json`
    - `Authorization: Bearer <token>`

**Responses:**
- **204 No Content**
- **400 Bad Request**
- **401 Unauthorized**
- **404 Not Found**

#### 11. Delete Exercise
**Endpoint:** `/exercise/{id}`
**Method:** `DELETE`
**Description:** Deletes an exercise by ID.

**Request:**
- **Headers:**
    - `Authorization: Bearer <token>`

**Responses:**
- **204 No Content**
- **401 Unauthorized**
- **404 Not Found**

#### 12. Search Exercises
**Endpoint:** `/exercise/search`
**Method:** `POST`
**Description:** Searches exercises based on provided criteria.

**Request:**
- **Headers:**
    - `Content-Type: application/json`
    - `Authorization: Bearer <token>`

**Responses:**
- **200 OK**

## Error Responses
The API uses standard HTTP status codes:
- **400 Bad Request**: Missing or invalid parameters.
- **401 Unauthorized**: Invalid token.
- **404 Not Found**: Resource not found.
- **204 No Content**: Successful request but no content.

## Authentication
To access protected endpoints, include the following header:

```makefile
Authorization: Bearer <token>

