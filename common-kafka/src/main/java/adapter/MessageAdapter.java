package adapter;

import com.google.gson.*;
import messages.CorrelationId;
import messages.Message;

import java.lang.reflect.Type;

public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {
    @Override
    public JsonElement serialize(Message message, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", message.getPayload().getClass().getName());
        jsonObject.add("payload", jsonSerializationContext.serialize(message.getPayload()));
        jsonObject.add("correlationId", jsonSerializationContext.serialize(message.getId()));

        return jsonObject;
    }

    @Override
    public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        var obj = jsonElement.getAsJsonObject();
        var payloadType = obj.get("type").getAsString();
        var correlationId = (CorrelationId) jsonDeserializationContext.deserialize(obj.get("correlationId"), CorrelationId.class);
        try {
            var payload = jsonDeserializationContext.deserialize(obj.get("payload"), Class.forName(payloadType));
            return new Message(payload, correlationId);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}