package serialization.benchmark;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author EKhyst
 */
public class JacksonJsonSerialization extends Serialization {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper(new JsonFactory());

    public JacksonJsonSerialization() {
        super("Jackson JSON");
    }
    
    @Override
    public byte[] serialize(Object object) throws Exception {
        return JSON_MAPPER.writeValueAsString(object).getBytes();
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> type) throws Exception {
        return JSON_MAPPER.readValue(bytes, type);
    }
}
