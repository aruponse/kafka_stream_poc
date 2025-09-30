# Kafka Streams Proof of Concept (PoC) with JSON Serialization

Este proyecto es una prueba de concepto completa que demuestra el uso de **Kafka Streams** con **Java 21**, **Spring Boot 3**, **Apache Kafka** y una base de datos **H2** en memoria, utilizando **JSON** como formato de serialización.

## ✨ Arquitectura con JSON Serialization

Este proyecto utiliza **JSON** como formato de serialización, ofreciendo las siguientes ventajas:

- **Facilidad de desarrollo**: Formato legible y fácil de debuggear
- **Flexibilidad**: Ideal para prototipos y desarrollo rápido
- **Compatibilidad universal**: Compatible con cualquier cliente HTTP
- **Menor complejidad**: Sin necesidad de Schema Registry
- **Testing simplificado**: Fácil creación de datos de prueba

## 🎯 Casos de Uso Implementados

### 1. Transformación de Contenido (Content Transformation)
- **Input**: `SimpleEvent` en el tópico `input-topic`
- **Procesamiento**: Transforma el campo `payload` añadiendo prefijo "TRANSFORMED:"
- **Output**: Evento transformado en el tópico `output-topic-transformed`

### 2. Conversión de Esquema JSON (JSON Schema Conversion)
- **Input**: `LegacyEvent` en el tópico `legacy-events-topic`
- **Procesamiento**: Convierte de formato legacy a `NewFormatEvent`
- **Output**: Evento convertido en el tópico `output-topic-json-converted`

### 3. Enrutamiento y División (Routing and Division)
- **Input**: `GenericAction` en el tópico `actions-topic`
- **Procesamiento**: Enruta según `actionType` (A o B) con transformaciones específicas
- **Output**: Eventos procesados en `output-topic-action-a` y `output-topic-action-b`

### 4. Procesamiento de Mensajes Entrantes (Inbound Message Processing) 🆕
- **Input**: `InboundMessageEvent` en el tópico `inbound-message-topic`
- **Procesamiento**: Transforma un mensaje entrante complejo en dos eventos diferentes
- **Output**: 
  - `CreateChatEvent` en el tópico `create-chat-topic`
  - `CreateMessageEvent` en el tópico `create-message-topic`

## 🏗️ Arquitectura del Proyecto

```
src/main/java/com/example/kafkastream/
├── KafkaStreamPocApplication.java          # Aplicación principal
├── config/
│   ├── KafkaStreamsConfig.java            # Configuración de Kafka Streams con JSON
│   └── KafkaConsumerConfig.java           # Configuración de consumidores Kafka
├── controller/
│   └── EventController.java               # REST API endpoints
├── dto/                                   # Data Transfer Objects
│   ├── SimpleEvent.java                   # DTO para Caso 1
│   ├── LegacyEvent.java                   # DTO para Caso 2 (input)
│   ├── NewFormatEvent.java                # DTO para Caso 2 (output)
│   ├── GenericAction.java                 # DTO para Caso 3
│   ├── InboundMessageEvent.java           # DTO para Caso 4 (input) 🆕
│   ├── CreateChatEvent.java               # DTO para Caso 4 (output) 🆕
│   └── CreateMessageEvent.java            # DTO para Caso 4 (output) 🆕
├── model/
│   ├── ProcessedEvent.java                # Entidad JPA para persistencia
│   └── OriginalEvent.java                 # Entidad JPA para tracking completo
├── repository/
│   ├── ProcessedEventRepository.java      # Repositorio JPA para eventos procesados
│   └── OriginalEventRepository.java       # Repositorio JPA para eventos originales
├── serde/
│   └── JsonSerde.java                     # Serializador/Deserializador JSON personalizado
└── service/
    ├── ProcessedEventService.java         # Servicio de eventos procesados
    ├── OriginalEventService.java          # Servicio de eventos originales
    └── KafkaPersistenceService.java       # Consumidor para persistencia
```

### 📁 Ejemplos de Datos de Prueba

```
examples/
├── simple-event.json                      # Ejemplo para Caso 1
├── legacy-event.json                      # Ejemplo para Caso 2
├── action-a.json                          # Ejemplo para Caso 3A
├── action-b.json                          # Ejemplo para Caso 3B
└── inbound-message-event.json             # Ejemplo para Caso 4 🆕
```

## 🚀 Requisitos Previos

1. **Java 21** instalado
2. **Maven 3.8+** instalado
3. **Apache Kafka** ejecutándose en `localhost:9092`
4. **Docker** (opcional, para usar docker-compose)

### Iniciar Kafka

#### Opción 1: Docker Compose (Recomendado)
```bash
# Usar el docker-compose.yml incluido en el proyecto
docker-compose up -d
```

#### Opción 2: Kafka Local
```bash
# Iniciar Zookeeper
bin/zookeeper-server-start config/zookeeper.properties

# Iniciar Kafka Server
bin/kafka-server-start config/server.properties
```

### Crear Tópicos
```bash
# Usar el script incluido
./create-topics.sh
```

## 📦 Compilación y Ejecución

### 1. Compilar el proyecto
```bash
cd kafka_stream_poc
mvn clean compile
```

### 2. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

### 3. Verificar que está ejecutándose
La aplicación estará disponible en: `http://localhost:8082`

### 4. Ejecutar pruebas automatizadas
```bash
# Usar el script de testing incluido
./test-api.sh
```

## 🧪 Testing de los Casos de Uso

### Caso 1: Transformación de Contenido

**Enviar SimpleEvent:**
```bash
curl -X POST http://localhost:8082/api/events/simple \
  -H "Content-Type: application/json" \
  -d '{
    "id": "simple-001",
    "payload": "hello world",
    "timestamp": 1640995200000
  }'
```

### Caso 2: Conversión de Esquema (Legacy a New Format)

**Enviar LegacyEvent:**
```bash
curl -X POST http://localhost:8082/api/events/legacy \
  -H "Content-Type: application/json" \
  -d '{
    "old_field_name": "legacy_field",
    "value": "legacy data to be converted"
  }'
```

### Caso 3: Enrutamiento y División

**Enviar GenericAction Tipo A:**
```bash
curl -X POST http://localhost:8082/api/events/action \
  -H "Content-Type: application/json" \
  -d '{
    "actionType": "A",
    "details": "Process action type A"
  }'
```

**Enviar GenericAction Tipo B:**
```bash
curl -X POST http://localhost:8082/api/events/action \
  -H "Content-Type: application/json" \
  -d '{
    "actionType": "B",
    "details": "Process action type B"
  }'
```

### Caso 4: Procesamiento de Mensajes Entrantes 🆕

**Enviar InboundMessageEvent:**
```bash
curl -X POST http://localhost:8082/api/events/inbound-message \
  -H "Content-Type: application/json" \
  -d '{
    "app": "TestApp",
    "timestamp": 1747854609182,
    "version": 2,
    "type": "message",
    "payload": {
      "id": "wamid.HBgMNTkzxhgfjg3Nxc5NzU3FZCqEazxcE1ODg0QUIzQTg4NjUa4NUR1BQzYB",
      "source": "123456789011",
      "type": "text",
      "payload": { "text": "Hola desde WhatsApp" },
      "sender": { 
        "phone": "123456789011",
        "name": "Online UserName",
        "country_code": "1",
        "dial_code": "23456789011"
      }
    }
  }'
```

### Consultar Eventos Procesados

**Ver todos los eventos procesados:**
```bash
curl -X GET http://localhost:8082/api/events/processed
```

**Ver estadísticas:**
```bash
curl -X GET http://localhost:8082/api/events/processed/stats
```

**Filtrar por tipo de evento:**
```bash
curl -X GET "http://localhost:8082/api/events/processed?eventType=SIMPLE_EVENT_TRANSFORMED"
```

## 💾 Base de Datos H2

### Acceder a la Consola H2
- URL: `http://localhost:8082/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (vacío)

### Consultar la Tabla de Eventos Procesados
```sql
SELECT * FROM processed_events ORDER BY processed_at DESC;
```

## 📋 Endpoints de la API REST

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/events/simple` | Publicar SimpleEvent |
| POST | `/api/events/legacy` | Publicar LegacyEvent |
| POST | `/api/events/action` | Publicar GenericAction |
| POST | `/api/events/inbound-message` | Publicar InboundMessageEvent 🆕 |
| GET | `/api/events/processed` | Obtener todos los eventos procesados |
| GET | `/api/events/processed/stats` | Obtener estadísticas de procesamiento |
| GET | `/api/events/original` | Obtener todos los eventos originales |
| DELETE | `/api/events/processed` | Eliminar todos los eventos procesados |
| DELETE | `/api/events/original` | Eliminar todos los eventos originales |

## 🔧 Detalles Técnicos de JSON

### Estructura de DTOs Principales

#### InboundMessageEvent (Caso 4) 🆕
Representa un mensaje entrante complejo de WhatsApp con estructura anidada:
```json
{
  "app": "TestApp",
  "timestamp": 1747854609182,
  "version": 2,
  "type": "message",
  "payload": {
    "id": "wamid.HBgMNTkz...",
    "source": "123456789011",
    "type": "text",
    "payload": { "text": "Mensaje del usuario" },
    "sender": { 
      "phone": "123456789011",
      "name": "Online UserName",
      "country_code": "1",
      "dial_code": "23456789011"
    }
  }
}
```

#### CreateChatEvent (Salida del Caso 4)
```json
{
  "chat_id": "chat_123456789011_1747854609182",
  "user_name": "Online UserName",
  "user_phone": "123456789011",
  "country_code": "1",
  "dial_code": "23456789011",
  "created_at": 1747854609182
}
```

#### CreateMessageEvent (Salida del Caso 4)
```json
{
  "message_id": "wamid.HBgMNTkz...",
  "sender_phone": "123456789011",
  "chat_id": "chat_123456789011_1747854609182",
  "message_type": "text",
  "content": "Mensaje del usuario",
  "timestamp": 1747854609182
}
```

### Configuración de JSON Serdes

La aplicación está configurada para usar JSON nativo de Spring Kafka:

```yaml
spring:
  kafka:
    producer:
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:  
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        "[spring.json.trusted.packages]": "com.example.kafkastream.dto"
    streams:
      properties:
        "[default.value.serde]": org.apache.kafka.common.serialization.Serdes$StringSerde
```

## 🔧 Configuración

### Tópicos de Kafka (configurables en `application.yml`)
- `input-topic`: Entrada para caso 1
- `legacy-events-topic`: Entrada para caso 2
- `actions-topic`: Entrada para caso 3
- `inbound-message-topic`: Entrada para caso 4 🆕
- `output-topic-transformed`: Salida para caso 1
- `output-topic-json-converted`: Salida para caso 2
- `output-topic-action-a`: Salida para acciones tipo A
- `output-topic-action-b`: Salida para acciones tipo B
- `create-chat-topic`: Salida para crear chat (caso 4) 🆕
- `create-message-topic`: Salida para crear mensaje (caso 4) 🆕

### Variables de Configuración Principales
```yaml
app:
  kafka:
    topics:
      input-topic: input-topic
      legacy-events-topic: legacy-events-topic
      actions-topic: actions-topic
      inbound-message-topic: inbound-message-topic
      output-topic-transformed: output-topic-transformed
      output-topic-json-converted: output-topic-json-converted
      output-topic-action-a: output-topic-action-a
      output-topic-action-b: output-topic-action-b
      create-chat-topic: create-chat-topic
      create-message-topic: create-message-topic
```

## 🐛 Troubleshooting

### Problema: Kafka no está ejecutándose
**Error**: `Connection to node -1 could not be established`
**Solución**: Verificar que Kafka esté ejecutándose en `localhost:9092`

### Problema: Tópicos no se crean automáticamente
**Solución**: Crear tópicos manualmente:
```bash
# O usar el script automatizado incluido
./create-topics.sh

# Tópicos creados automáticamente:
# - input-topic
# - legacy-events-topic  
# - actions-topic
# - inbound-message-topic
# - output-topic-transformed
# - output-topic-json-converted
# - output-topic-action-a
# - output-topic-action-b
# - create-chat-topic
# - create-message-topic
```

## 📊 Monitoreo y Logs

Los logs de la aplicación mostrarán el procesamiento de eventos en tiempo real:
- Procesamiento de Kafka Streams
- Persistencia de eventos en H2
- Actividad de la API REST

### Nivel de Log para Desarrollo
```yaml
logging:
  level:
    com.example.kafkastream: DEBUG
    org.springframework.kafka: DEBUG
```

## 🎉 Flujo Completo de Trabajo

1. **Publicar** evento via REST API
2. **Procesar** evento con Kafka Streams (transformación/conversión/enrutamiento/división)
3. **Persistir** resultado en base de datos H2 via @KafkaListener
4. **Consultar** eventos procesados via REST API

Este PoC demuestra un pipeline completo de procesamiento de eventos con Kafka Streams, desde la ingesta hasta la persistencia, pasando por transformaciones complejas de datos que incluyen:

- **Transformación simple** de contenido
- **Conversión de esquemas** legacy a nuevos formatos
- **Enrutamiento condicional** basado en tipos de acción
- **División de eventos complejos** en múltiples eventos de salida 🆕