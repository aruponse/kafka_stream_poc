# Kafka Streams Proof of Concept (PoC)

Este proyecto es una prueba de concepto completa que demuestra el uso de **Kafka Streams** con **Java 21**, **Spring Boot 3**, **Apache Kafka** y una base de datos **H2** en memoria.

## 🎯 Casos de Uso Implementados

### 1. Transformación de Contenido (Content Transformation)
- **Input**: `SimpleEvent` en el tópico `input-topic`
- **Procesamiento**: Transforma el campo `payload` a mayúsculas y añade prefijo "TRANSFORMED:"
- **Output**: Evento transformado en el tópico `output-topic-transformed`

### 2. Conversión de Esquema JSON (JSON Schema Conversion)
- **Input**: `LegacyEvent` en el tópico `input-topic`
- **Procesamiento**: Convierte de formato legacy a `NewFormatEvent`
- **Output**: Evento convertido en el tópico `output-topic-json-converted`

### 3. Enrutamiento y División (Routing and Division)
- **Input**: `GenericAction` en el tópico `actions-topic`
- **Procesamiento**: Enruta según `actionType` (A o B) con transformaciones específicas
- **Output**: Eventos procesados en `output-topic-action-a` y `output-topic-action-b`

## 🏗️ Arquitectura del Proyecto

```
src/main/java/com/example/kafkastream/
├── KafkaStreamPocApplication.java          # Aplicación principal
├── config/
│   └── KafkaStreamsConfig.java            # Configuración de Kafka Streams
├── controller/
│   └── EventController.java               # REST API endpoints
├── dto/
│   ├── SimpleEvent.java                   # DTO para Caso 1
│   ├── LegacyEvent.java                   # DTO para Caso 2 (input)
│   ├── NewFormatEvent.java                # DTO para Caso 2 (output)
│   └── GenericAction.java                 # DTO para Caso 3
├── model/
│   └── ProcessedEvent.java                # Entidad JPA para persistencia
├── repository/
│   └── ProcessedEventRepository.java      # Repositorio JPA
└── service/
    ├── ProcessedEventService.java         # Servicio de negocio
    └── KafkaPersistenceService.java       # Consumidor para persistencia
```

## 🚀 Requisitos Previos

1. **Java 21** instalado
2. **Maven 3.8+** instalado
3. **Apache Kafka** ejecutándose en `localhost:9092`

### Iniciar Kafka (usando Confluent Platform o Apache Kafka)

```bash
# Iniciar Zookeeper
bin/zookeeper-server-start etc/kafka/zookeeper.properties

# Iniciar Kafka Server
bin/kafka-server-start etc/kafka/server.properties
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
La aplicación estará disponible en: `http://localhost:8081`

## 🧪 Testing de los Casos de Uso

### Caso 1: Transformación de Contenido

**Enviar SimpleEvent:**
```bash
curl -X POST http://localhost:8081/api/events/simple/typed \
  -H "Content-Type: application/json" \
  -d '{
    "id": "simple-001",
    "payload": "hello world",
    "timestamp": 1640995200000
  }'
```

### Caso 2: Conversión de Esquema JSON

**Enviar LegacyEvent:**
```bash
curl -X POST http://localhost:8081/api/events/legacy \
  -H "Content-Type: application/json" \
  -d '{
    "old_field_name": "legacy_field",
    "value": "legacy data"
  }'
```

### Caso 3: Enrutamiento y División

**Enviar GenericAction Tipo A:**
```bash
curl -X POST http://localhost:8081/api/events/action \
  -H "Content-Type: application/json" \
  -d '{
    "actionType": "A",
    "details": "Process action type A"
  }'
```

**Enviar GenericAction Tipo B:**
```bash
curl -X POST http://localhost:8081/api/events/action \
  -H "Content-Type: application/json" \
  -d '{
    "actionType": "B",
    "details": "Process action type B"
  }'
```

### Consultar Eventos Procesados

**Ver todos los eventos procesados:**
```bash
curl -X GET http://localhost:8081/api/events/processed
```

**Ver estadísticas:**
```bash
curl -X GET http://localhost:8081/api/events/processed/stats
```

**Filtrar por tipo de evento:**
```bash
curl -X GET "http://localhost:8081/api/events/processed?eventType=SIMPLE_EVENT_TRANSFORMED"
```

## 💾 Base de Datos H2

### Acceder a la Consola H2
- URL: `http://localhost:8081/h2-console`
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
| POST | `/api/events/simple` | Publicar evento genérico (SimpleEvent o LegacyEvent) |
| POST | `/api/events/simple/typed` | Publicar SimpleEvent tipado |
| POST | `/api/events/legacy` | Publicar LegacyEvent tipado |
| POST | `/api/events/action` | Publicar GenericAction |
| GET | `/api/events/processed` | Obtener todos los eventos procesados |
| GET | `/api/events/processed/stats` | Obtener estadísticas de procesamiento |
| DELETE | `/api/events/processed` | Eliminar todos los eventos procesados |

## 🔧 Configuración

### Tópicos de Kafka (configurables en `application.yml`)
- `input-topic`: Entrada para casos 1 y 2
- `actions-topic`: Entrada para caso 3
- `output-topic-transformed`: Salida para caso 1
- `output-topic-json-converted`: Salida para caso 2
- `output-topic-action-a`: Salida para acciones tipo A
- `output-topic-action-b`: Salida para acciones tipo B

### Variables de Configuración Principales
```yaml
app:
  kafka:
    topics:
      input-topic: input-topic
      actions-topic: actions-topic
      output-topic-transformed: output-topic-transformed
      output-topic-json-converted: output-topic-json-converted
      output-topic-action-a: output-topic-action-a
      output-topic-action-b: output-topic-action-b
```

## 🐛 Troubleshooting

### Problema: Kafka no está ejecutándose
**Error**: `Connection to node -1 could not be established`
**Solución**: Verificar que Kafka esté ejecutándose en `localhost:9092`

### Problema: Tópicos no se crean automáticamente
**Solución**: Crear tópicos manualmente:
```bash
kafka-topics --create --topic input-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic actions-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic output-topic-transformed --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic output-topic-json-converted --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic output-topic-action-a --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic output-topic-action-b --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
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
2. **Procesar** evento con Kafka Streams (transformación/conversión/enrutamiento)
3. **Persistir** resultado en base de datos H2 via @KafkaListener
4. **Consultar** eventos procesados via REST API

Este PoC demuestra un pipeline completo de procesamiento de eventos con Kafka Streams, desde la ingesta hasta la persistencia, pasando por transformaciones complejas de datos.