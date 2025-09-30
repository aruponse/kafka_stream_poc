Instrucción: Crea un proyecto completo de prueba de concepto (Proof of Concept - PoC) para Kafka Streams utilizando Java 21, Spring Boot 3, Apache Kafka (para la configuración y testing) y una base de datos H2 en memoria.

El proyecto debe demostrar la funcionalidad de Kafka Streams con tres casos de uso específicos y exponer una API REST para la interacción.

🎯 Requisitos del Proyecto
1. Estructura y Tecnologías
Lenguaje: Java 21.

Framework: Spring Boot 3.

Base de Datos: H2 (en modo embebido para persistencia simple).

Componentes:

Una aplicación Spring Boot que contenga los servicios de Kafka Producer, Kafka Streams y la API REST.

Clases de dominio/DTOs para representar los eventos.

2. Configuración de Tópicos
Define los siguientes tópicos (deben ser configurables en application.yml):

input-topic: Tópico de entrada para los casos de uso 1 y 2.

actions-topic: Tópico de entrada para el caso de uso 3.

output-topic-transformed: Tópico de salida para el caso de uso 1.

output-topic-json-converted: Tópico de salida para el caso de uso 2.

output-topic-action-a: Tópico de salida para la primera acción del caso 3.

output-topic-action-b: Tópico de salida para la segunda acción del caso 3.

3. Implementación de Casos de Uso (Kafka Streams)
Implementa una clase de configuración de Kafka Streams (KafkaStreamsConfig) que defina la topología para manejar los tres casos de uso simultáneamente.

Caso de Uso	Tópico de Entrada	Lógica de Transformación	Tópico(s) de Salida
1. Transformación de Contenido	input-topic	Recibe un evento (ej. SimpleEvent {id, payload, timestamp}). Transforma solo el campo payload (ej. a mayúsculas o añadiendo un prefijo) manteniendo la estructura del evento y la clave.	output-topic-transformed
2. Conversión de Esquema JSON	input-topic	Recibe un evento JSON con un formato (ej. LegacyEvent {old_field_name, value}). Convierte este JSON a un formato completamente nuevo (ej. NewFormatEvent {newFieldName, data}), alterando la estructura y los nombres de campo.	output-topic-json-converted
3. División y Publicación (Routing)	actions-topic	Recibe un evento genérico (ej. GenericAction {actionType, details}). El campo actionType determina la acción. Separar el stream basado en actionType (ej. type=A y type=B). Transformar cada sub-stream independientemente y enviarlo a su tópico dedicado como dos eventos separados.	output-topic-action-a y output-topic-action-b

Export to Sheets
4. Persistencia y API REST
Persistencia (H2):

Crea una entidad JPA (ProcessedEvent) para almacenar los resultados finales de los streams.

El servicio de Streams debe tener un consumidor secundario o un servicio de persistencia que escuche los tópicos de salida (output-topic-transformed, output-topic-json-converted, output-topic-action-a, output-topic-action-b) y persista los datos en H2.

API REST:

POST /api/events/simple: Recibe un evento para el Caso 1 y 2, y lo publica en input-topic.

POST /api/events/action: Recibe un evento para el Caso 3, y lo publica en actions-topic.

GET /api/events/processed: Consulta todos los registros de la tabla ProcessedEvent de H2 para verificar los resultados.

Instrucción Final: Proporciona todo el código fuente organizado en archivos (clases de Java, configuración YAML, DTOs, etc.), asegurando que el proyecto sea ejecutable y que las dependencias de Maven estén correctamente definidas. Incluye comentarios explicativos en el código de Kafka Streams.

Explicación del Prompt para el Generador 💡
Este prompt es efectivo porque:

Define el Stack: Especifica las versiones de Java, Spring Boot y el uso de H2, lo cual es crucial para la generación de código.

Detalla los Casos de Uso: Proporciona un esquema claro de la entrada, la transformación y la salida para cada uno de los tres requisitos, incluyendo el uso de los métodos clave de Kafka Streams como mapValues, map, y branch/to.

Especifica la Interacción (API): Define los endpoints exactos que el generador debe crear para la prueba manual del PoC (producción y consulta).

Añade Persistencia: Incluir la persistencia con H2 obliga al generador a crear un mecanismo (posiblemente un @KafkaListener) para consumir los resultados finales del stream, cerrando el ciclo de la prueba de concepto.