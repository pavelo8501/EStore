# Backend API for Online Store Platform

## Overview

This repository contains the Backend API for the Online Store Platform, developed in Kotlin using the Spring Boot framework.
The API serves as the backbone of the platform, handling business logic, database interactions, and providing secure access to the platform's data for both the Storefront and Administration Panel.
**This branch was created to implement the Docker version of the project.**

## Features

- **Web Socket API** for data access and manipulation
- **User Authentication** using JSON Web Tokens (JWT)
- **Supplier data Management** using JSON Web Tokens (JWT)
- **Product Management** including CRUD operations for products, categories, and inventory
- **Order Processing** including order creation, payment processing, and order fulfillment
- **Customer Management** including user profiles, order history, and customer support
- **Role-Based Access Control** ensuring secure access to different parts of the platform

## Technologies Used

- **Language:** Kotlin
- **Framework:** Ktor
- **Database:** MySQL
- **Authentication:** JWT (JSON Web Tokens)
- **Build Tool:** Gradle
- **Version Control:** Git
- **Deployment:** Docker, CI/CD pipelines
- **Testing:** JUnit, Mockito

### Prerequisites

- Node.js & npm
- PHP & Composer
- Java (for Kotlin)
- MySQL
- Docker (for containerized deployment)