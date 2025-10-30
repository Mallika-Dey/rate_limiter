# 🚀 Distributed Rate Limiting System — Powered by Bucket4j, Redis & Kafka

This project is a **distributed rate limiting platform** designed to control API request rates dynamically across services.

---

## 🏗️ Architecture Diagram

![Rate Limit Architecture](https://github.com/Mallika-Dey/images/blob/main/ratelimit.png)

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

## ⚙️ Core Features

### 🧠 Dynamic Rate Limiting
- Rate limits are applied using **Bucket4j** (token bucket algorithm).
- Supports in-memory, Redis-backed, or hybrid (distributed + local) buckets.

### 🔄 Real-Time Configuration Sync
- License or bucket changes trigger updates via **Kafka**.
- All API Gateway instances automatically receive new configurations.

### 🗃️ Redis & Database Integration
- Redis stores active bucket configurations for fast lookup and fallback.
- Database persists user license data and rate plan definitions.

### 🧩 Limit Enforce Service
- A sub-service responsible for:
  - Updating **DB** and **Redis** when a license changes.
  - Publishing new bucket configurations to **Kafka**.
- Ensures that all API Gateways stay in sync with the latest license rules.

### 🌐 API Gateway
- Enforces per-license rate limits using **Bucket4j**.
- Maintains bucket configurations in memory for fast access.
- If a configuration is missing, fetches from **Redis**.
- Subscribes to Kafka updates to refresh configuration in real-time.

---

## 🧠 How It Works

### 1. License Update Flow
1. A user’s license is created or updated.  
2. **Limit Enforce Service** updates:
   - The database (for persistence).
   - Redis (for shared configuration).
   - Kafka (to notify API Gateways).  

### 2. API Gateway Enforcement
1. **API Gateway** receives the Kafka event.  
2. It updates the in-memory rate limit configuration.  
3. Incoming API requests are checked against **Bucket4j** buckets.  
4. If configuration is missing in memory:
   - It retrieves the bucket configuration from **Redis**.  
   - If found, applies rate limiting immediately.

---

## 🧰 Components Summary

| Component | Description | Technology |
|------------|--------------|-------------|
| **API Gateway** | Enforces rate limits using Bucket4j | Java, Spring Boot |
| **Limit Enforce Service** | Syncs license changes to DB, Redis, Kafka | Java, Spring Boot |
| **Redis** | Stores bucket configurations & counters | Redis |
| **Kafka** | Propagates config updates across services | Apache Kafka |
| **Database** | Stores license data and rate plan definitions | PostgreSQL / MySQL |

---

## 🛠️ Tech Stack

- **Language**: Java  
- **Framework**: Spring Boot  
- **Rate Limiter**: [Bucket4j](https://github.com/vladimir-bukhtoyarov/bucket4j)  
- **Message Broker**: Apache Kafka  
- **Cache/Store**: Redis  
- **Database**: PostgreSQL or MySQL  
- **Build Tool**: Gradle or Maven  

---

## 🚀 Getting Started

### Prerequisites
- Java 11+ or higher  
- Kafka cluster  
- Redis instance  
- Database (PostgreSQL/MySQL)

### Setup Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/rate-limiting-system.git
   cd rate-limiting-system
