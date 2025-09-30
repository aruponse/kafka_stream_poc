# Kafka Streams Proof of Concept (PoC) with Apache Avro

Este proyecto es una prueba de concepto completa que demuestra el uso de **Kafka Streams** con **Java 21**, **Spring Boot 3**, **Apache Kafka**, **Apache Avro** y una base de datos **H2** en memoria.

## ✨ Nueva Arquitectura con Apache Avro

Este proyecto ha sido refactorizado para utilizar **Apache Avro** como formato de serialización en lugar de JSON, ofreciendo las siguientes ventajas:

- **Esquema evolutivo**: Compatibilidad hacia adelante y hacia atrás
- **Mejor rendimiento**: Serialización binaria más eficiente que JSON
- **Validación de datos**: Esquemas estrictos que garantizan la integridad de los datos  
- **Generación automática de código**: Clases Java generadas desde esquemas Avro
- **Integración con Schema Registry**: Gestión centralizada de schemas

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
│   └── KafkaStreamsConfig.java            # Configuración de Kafka Streams con Avro
├── controller/
│   └── EventController.java               # REST API endpoints con serialización Avro
├── avro/                                  # Esquemas Avro (generados automáticamente)
│   ├── SimpleEvent.avsc                   # Esquema para Caso 1
│   ├── LegacyEvent.avsc                   # Esquema para Caso 2 (input)
│   ├── NewFormatEvent.avsc                # Esquema para Caso 2 (output)
│   └── GenericAction.avsc                 # Esquema para Caso 3
├── model/
│   ├── ProcessedEvent.java                # Entidad JPA para persistencia
│   └── OriginalEvent.java                 # Entidad JPA para tracking completo
├── repository/
│   ├── ProcessedEventRepository.java      # Repositorio JPA para eventos procesados
│   └── OriginalEventRepository.java       # Repositorio JPA para eventos originales
└── service/
    ├── ProcessedEventService.java         # Servicio de eventos procesados
    ├── OriginalEventService.java          # Servicio de eventos originales
    └── KafkaPersistenceService.java       # Consumidor para persistencia con Avro
```

### 📁 Estructura de Esquemas Avro

```
src/main/avro/
├── SimpleEvent.avsc                       # Esquema para transformación de contenido
├── LegacyEvent.avsc                       # Esquema para eventos legacy
├── NewFormatEvent.avsc                    # Esquema para eventos convertidos
└── GenericAction.avsc                     # Esquema para acciones genéricas
```

### 🔄 Clases Java Generadas (target/generated-sources/avro/)

```
target/generated-sources/avro/com/example/kafkastream/avro/
├── SimpleEvent.java                       # Clase Avro generada
├── LegacyEvent.java                       # Clase Avro generada
├── NewFormatEvent.java                    # Clase Avro generada
└── GenericAction.java                     # Clase Avro generada
```

## 🚀 Requisitos Previos

1. **Java 21** instalado
2. **Maven 3.8+** instalado
3. **Apache Kafka** ejecutándose en `localhost:9092`
4. **Confluent Schema Registry** ejecutándose en `localhost:8081` (requerido para Avro)

### Iniciar Kafka y Schema Registry

#### Opción 1: Confluent Platform (Recomendado)
```bash
# Iniciar todos los servicios de Confluent
confluent local services start

# O iniciar servicios específicos
confluent local services kafka start
confluent local services schema-registry start
```

#### Opción 2: Apache Kafka + Schema Registry manual
```bash
# Iniciar Zookeeper
bin/zookeeper-server-start etc/kafka/zookeeper.properties

# Iniciar Kafka Server
bin/kafka-server-start etc/kafka/server.properties

# Iniciar Schema Registry (descargar desde Confluent)
bin/schema-registry-start etc/schema-registry/schema-registry.properties
```

### Verificar que Schema Registry está funcionando
```bash
curl http://localhost:8081/subjects
# Debería devolver: []
```

## 📦 Compilación y Ejecución

### 1. Compilar el proyecto y generar clases Avro
```bash
cd kafka_stream_poc
mvn clean compile
```

*Nota: El plugin de Maven `avro-maven-plugin` generará automáticamente las clases Java desde los esquemas `.avsc` durante la compilación.*

### 2. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

### 3. Verificar que está ejecutándose
La aplicación estará disponible en: `http://localhost:8082`

### 4. Verificar integración con Schema Registry
```bash
# Después de enviar algunos eventos, verificar schemas registrados
curl http://localhost:8081/subjects

# Ver el schema de SimpleEvent
curl http://localhost:8081/subjects/input-topic-value/versions/latest
```

## 🧪 Testing de los Casos de Uso

### Caso 1: Transformación de Contenido

**Enviar SimpleEvent (JSON se convierte a Avro internamente):**
```bash
curl -X POST http://localhost:8082/api/events/simple \
  -H "Content-Type: application/json" \
  -d '{
    "id": "simple-001",
    "payload": "hello world from avro",
    "timestamp": 1640995200000
  }'
```

### Caso 2: Conversión de Esquema (Legacy a New Format)

**Enviar LegacyEvent (JSON se convierte a Avro internamente):**
```bash
curl -X POST http://localhost:8082/api/events/legacy \
  -H "Content-Type: application/json" \
  -d '{
    "old_field_name": "legacy_field",
    "value": "legacy data processed via avro"
  }'
```

### Caso 3: Enrutamiento y División

**Enviar GenericAction Tipo A (JSON se convierte a Avro internamente):**
```bash
curl -X POST http://localhost:8082/api/events/action \
  -H "Content-Type: application/json" \
  -d '{
    "actionType": "A",
    "details": "Process action type A via avro serialization"
  }'
```

**Enviar GenericAction Tipo B (JSON se convierte a Avro internamente):**
```bash
curl -X POST http://localhost:8082/api/events/action \
  -H "Content-Type: application/json" \
  -d '{
    "actionType": "B",
    "details": "Process action type B via avro serialization"
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
| POST | `/api/events/simple` | Publicar SimpleEvent (JSON→Avro) |
| POST | `/api/events/legacy` | Publicar LegacyEvent (JSON→Avro) |
| POST | `/api/events/action` | Publicar GenericAction (JSON→Avro) |
| GET | `/api/events/processed` | Obtener todos los eventos procesados |
| GET | `/api/events/processed/stats` | Obtener estadísticas de procesamiento |
| GET | `/api/events/original` | Obtener todos los eventos originales |
| DELETE | `/api/events/processed` | Eliminar todos los eventos procesados |
| DELETE | `/api/events/original` | Eliminar todos los eventos originales |

## 🔧 Detalles Técnicos de Avro

### Esquemas Avro Definidos

#### SimpleEvent.avsc
```json
{
  "type": "record",
  "name": "SimpleEvent",
  "namespace": "com.example.kafkastream.avro",
  "doc": "Simple event for Kafka Streams Use Case 1: Content Transformation",
  "fields": [
    {"name": "id", "type": "string", "doc": "Unique identifier for the event"},
    {"name": "payload", "type": "string", "doc": "The main content/payload of the event"},
    {"name": "timestamp", "type": "long", "doc": "Timestamp when the event was created (epoch milliseconds)"}
  ]
}
```

#### LegacyEvent.avsc  
```json
{
  "type": "record",
  "name": "LegacyEvent",
  "namespace": "com.example.kafkastream.avro",
  "doc": "Legacy event format for Kafka Streams Use Case 2: JSON Schema Conversion",
  "fields": [
    {"name": "old_field_name", "type": "string", "doc": "Legacy field name (to be converted)"},
    {"name": "value", "type": "string", "doc": "Value to be converted to new format"}
  ]
}
```

### Beneficios de Avro vs JSON

| Aspecto | JSON | Avro |
|---------|------|------|
| **Tamaño** | Texto plano, mayor tamaño | Binario compacto, menor tamaño |
| **Rendimiento** | Parsing lento | Serialización/deserialización rápida |
| **Evolución** | Sin garantías | Esquema evolutivo con compatibilidad |
| **Validación** | Manual/externa | Automática por esquema |
| **Tipado** | Dinámico | Estático y fuerte |
| **Interoperabilidad** | Dependiente de librería | Estándar cross-platform |

### Configuración de Avro Serdes

La aplicación está configurada para usar Avro con Schema Registry:

```yaml
spring:
  kafka:
    producer:
      value-serializer: io.confluent.kafka.serializers.KafkaAvroSerializer
    consumer:  
      value-deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
    streams:
      properties:
        default.value.serde: io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
        schema.registry.url: http://localhost:8081
```

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