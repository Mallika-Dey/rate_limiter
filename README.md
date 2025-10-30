## Features

- **License Updates**: Limit enforce service is designed to manage license-based rate limiting for API requests. It integrates with a database, Redis, and Kafka to provide real-time rate limit enforcement and dynamic configuration updates for API Gateway.
Automatically updates database and Redis when a license is updated.  
- **Kafka Integration**: Publishes bucket configurations to Kafka whenever license updates occur.  
- **API Gateway Rate Limiting**:  
  - API Gateway applies rate limits based on bucket configuration stored in memory.  
  - Subscribes to Kafka to receive updated bucket configurations in real-time.  
  - Falls back to Redis if the configuration is not available in memory.

---

## Architecture Overview

![Rate Limit Architecture](https://github.com/Mallika-Dey/images/blob/main/ratelimit.png)

---

## Components

### Limit Enforce Service
- Updates database and Redis with license info.
- Publishes updated bucket configuration to Kafka.

### API Gateway
- Receives incoming API requests.
- Applies rate limiting based on in-memory bucket configuration.
- Falls back to Redis if configuration is missing in memory.
- Subscribes to Kafka topic for live bucket configuration updates.
