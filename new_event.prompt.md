Agrega un nuevo caso de uso para el evento `inbound_message_event` con el payload:
```json
{
    "app": "NoeSushi",
    "timestamp": 1747854609182,
    "version": 2,
    "type": "message",
    "payload": {
        "id": "wamid.HBgMNTkzxhgfjg3Nxc5NzU3FZCqEazxcE1ODg0QUIzQTg4NjUa4NUR1BQzYB",
        "source": "593987699757",
        "type": "text",
        "payload": { "text": "3" },
        "sender": { 
            "phone": "593987699757",
            "name": "Aruponse",
            "country_code": "593",
            "dial_code": "987699757"
        }
    }
}
```
Que lo transforme y publique en 2 nuevos eventos:

1. `create_chat_event` con el payload:
```json
{
    "chat_id": "593987699757",
    "user_name": "Aruponse", // Extraído de payload.sender.name
    "user_phone": "593987699757", // Extraído de payload.sender.phone
    "country_code": "593", // Extraído de payload.sender.country_code
    "dial_code": "987699757", // Extraído de payload.sender.dial_code
    "created_at": 1747854609182
}
```
2. `create_message_event` con el payload:
```json
{
    "message_id": "wamid.HBgMNTkzxhgfjg3Nxc5NzU3FZCqEazxcE1ODg0QUIzQTg4NjUa4NUR1BQzYB", // Extraído de payload.id
    "sender_phone": "593987699757", // Extraído de payload.sender.phone
    "chat_id": "593987699757", // Extraído de payload.source
    "message_type": "text", // Extraído de payload.type
    "content": "3", // Extraído de payload.payload.text
    "timestamp": 1747854609182
}
```

Agrega pruebas unitarias para verificar que el evento `inbound_message_event` se procesa correctamente y que los eventos `create_chat_event` y `create_message_event` se generan con los datos correctos. Y que los endpoints para generar los eventos funcionan correctamente.