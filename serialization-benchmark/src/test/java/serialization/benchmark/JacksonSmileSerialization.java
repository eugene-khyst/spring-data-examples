package serialization.benchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;

/**
 *
 * @author EKhyst
 */
public class JacksonSmileSerialization extends Serialization {

    private static final ObjectMapper SMILE_MAPPER = new ObjectMapper(new SmileFactory());

    public JacksonSmileSerialization() {
        super("Jackson Smile");
    }
    
    @Override
    public byte[] serialize(Object object) throws Exception {
        return SMILE_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> type) throws Exception {
        return SMILE_MAPPER.readValue(bytes, type);
    }
}
