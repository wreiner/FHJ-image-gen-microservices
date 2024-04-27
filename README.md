# image-gen

Spring Boot microservices for an image generation AI.

## Description

This project is a Spring Boot microservices project that generates images using an AI model.

The project is composed of the following services:

- **ingress**: The entry point for the generation prompt (Spring Boot Web API)
- **orchestration-service**: The service that orchestrates the generation process (Spring Boot)
- **classification-service**: The service that classifies the prompt (Flask Web API)
- **generation-service**: The service that generates the image (Python RabbitMQ Worker)

## Architectural Overview

![docs/architecture-overview.png](docs/architecture-overview.png)
