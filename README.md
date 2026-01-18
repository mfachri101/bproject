# Flight Search API

A high-performance, asynchronous flight search engine built with a distributed microservices architecture. This project leverages **Kafka** for event-driven communication and **MongoDB** for flexible data storage.

## 🏗 Architecture

The system is designed to handle high-concurrency search requests by decoupling the user-facing API from the heavy lifting of 3rd party integrations.



### Core Components
* **Aggregator Service:**
    * Acts as the primary entry point for user search requests.
    * Initiates asynchronous search flows by producing messages to Kafka.
    * Merges inventory results and returns them to the user.
* **Integrator Service:**
    * Consumes search requests from Kafka topics.
    * Handles 3rd party integration with flight providers.
    * Normalizes external data and persists it into MongoDB.
* **Data & Messaging:**
    * **Kafka:** Facilitates resilient, decoupled communication between the services.
    * **MongoDB:** Stores flight itineraries, airport/airline metadata, and routing information.

---

## 🚀 Getting Started

### Prerequisites
* **Java**
* **Docker & Docker Compose**
* **Gradle**

### Build and Deployment
1.  **Build Images:**
    Build the project and create Docker images using the Jib plugin.
    ```bash
    ./gradlew clean build jibDockerBuild
    ```
2.  **Start Services:**
    Launch the services and required infrastructure.
    ```bash
    docker compose up -d
    ```

---

## 🤖 AI-Assisted Development

This project utilizes an "AI-First" development workflow, where tools like **Gemini** and **GitHub Copilot** are used for rapid prototyping, code refinement, and complex debugging.

### Context-Aware Development
I maximize the relevance of AI suggestions by providing specific IDE context to **GitHub Copilot**. 
* **Implementation:** By keeping relevant files like `Itinerary.java` or `FlightSearchController.java` open, the AI understands the project's data structures and suggests code that fits existing patterns.

### Iterative Coding & Design
* **Initial Generation:** I use prompts such as *"Generate typical flight search data model on Java"* to build the foundation.
* **Refinement:** The generated code is reviewed and improved with follow-up prompts, such as *"Improve these classes using Lombok annotations"*.

### Advanced Debugging & Log Analysis
I leverage AI (Gemini) to translate low-level system logs into actionable fixes.
* **Example:** When diagnosing a `Node -1 disconnected` error in the `aggregator-service` logs, I provided the error log to the AI.
* **Insight:** The AI identified that the Kafka consumer lost its connection to the broker and suggested checking the advertised listeners in the `compose.yaml`.

---

## 🛣 Roadmap

* **Redis Caching:** Implementation of a caching layer to store frequent search results and reduce 3rd-party API overhead.
* **Dynamic Routing Config:** Moving hardcoded routing logic into a MongoDB collection to allow real-time provider management without redeployment.
