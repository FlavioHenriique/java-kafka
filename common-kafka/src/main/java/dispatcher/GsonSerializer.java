package dispatcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import messages.Message;
import org.apache.kafka.common.serialization.Serializer;
import adapter.MessageAdapter;

public class GsonSerializer<T> implements Serializer<T> {

    private Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageAdapter()).create();

    @Override
    public byte[] serialize(String s, T t) {
        return gson.toJson(t).getBytes();
    }
}