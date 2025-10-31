# 🚀 Distributed Rate Limiting System — Powered by Bucket4j, Redis & Kafka

This project is a **distributed rate limiting platform** designed to control API request rates dynamically across services.

---

## 🏗️ Architecture Diagram

![Rate Limit Architecture](https://github.com/Mallika-Dey/images/blob/main/ratelimit.png)

---

## 🧩 Tech Stack

| Layer | Technology |
|-------|-------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.x |
| **Gateway** | Spring Cloud Gateway |
| **Rate Limiting** | **[Bucket4j](https://github.com/vladimir-bukhtoyarov/bucket4j)** |
| **Cache / Storage** | Redis 7.x |
| **Build Tool** | Gradle / Maven |
| **Container** | Docker & Docker Compose |

---

## 🧩 Overview

It leverages **Bucket4j** for token bucket rate limiting, **Redis** for centralized storage and fallback, and **Kafka** for real-time synchronization between components.

The system is built around multiple cooperating services:

- **API Gateway** — Enforces rate limits per client or license, using Bucket4j.  
- **Limit Enforce Service** — A background service that updates and synchronizes license-based rate limits.  
- **Redis** — Stores rate limit buckets and configurations for shared access across services.  
- **Kafka** — Delivers live configuration updates to all subscribers (e.g., API Gateways).  
- **Database** — Persists license information and long-term configuration data.

Together, these components form a scalable, real-time rate limiting ecosystem.

---

## 🚀 Getting Started

**Docker Compose**: Docker compose file for environment.

```yaml

version: "3.8"

services:
  redis:
    image: redis:7
    container_name: leads-redis
    restart: unless-stopped
    ports:
      - "6379:6379"  
    networks:
      - monitoring-net

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"
    networks:
      - monitoring-net

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: kafka
    ports:
      - "9092:9092"
      - "29092:29092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    depends_on:
      - zookeeper 
    networks:
      - monitoring-net 
      
  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: kafka-ui
    ports:
       - "8085:8080"   # host port : container port
    environment:
      KAFKA_CLUSTERS_0_NAME: local
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:9092
    depends_on:
      - kafka  
    networks:
      - monitoring-net    

networks:
  monitoring-net:
    driver: bridge  
