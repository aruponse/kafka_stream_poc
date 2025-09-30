#!/bin/bash

# Script para limpiar los tópicos de Kafka y eliminar datos corruptos

echo "🧹 Limpiando tópicos de Kafka..."

# Configuración
KAFKA_CONTAINER="kafka-broker"  # Container name from docker-compose
TOPICS=(
    "input-topic"
    "legacy-events-topic"
    "actions-topic"
    "output-topic-transformed"
    "output-topic-json-converted"
    "output-topic-action-a"
    "output-topic-action-b"
    "inbound-message-topic"
    "create-chat-topic"
    "create-message-topic"
)

# Función para ejecutar comandos de Kafka en el container
run_kafka_cmd() {
    docker exec $KAFKA_CONTAINER kafka-topics --bootstrap-server localhost:9092 "$@"
}

# Verificar que el container esté corriendo
if ! docker ps | grep -q $KAFKA_CONTAINER; then
    echo "❌ Error: Container $KAFKA_CONTAINER no está corriendo"
    echo "   Asegúrate de que Docker Compose esté ejecutándose"
    exit 1
fi

# Eliminar y recrear todos los tópicos
for topic in "${TOPICS[@]}"; do
    echo "🗑️  Eliminando tópico: $topic"
    run_kafka_cmd --delete --topic $topic 2>/dev/null || echo "   Tópico $topic no existía"
    
    echo "➕ Creando tópico: $topic"
    run_kafka_cmd --create --topic $topic --partitions 3 --replication-factor 1
    
    if [ $? -eq 0 ]; then
        echo "   ✅ Tópico $topic creado exitosamente"
    else
        echo "   ❌ Error creando tópico $topic"
    fi
done

echo "✅ Limpieza de tópicos completada"
echo "📊 Listando todos los tópicos:"
run_kafka_cmd --list

echo "🚀 Ahora puedes reiniciar la aplicación Spring Boot"