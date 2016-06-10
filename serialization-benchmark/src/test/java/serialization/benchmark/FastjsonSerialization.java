package serialization.benchmark;

import com.alibaba.fastjson.JSON;

/**
 *
 * @author EKhyst
 */
public class FastjsonSerialization extends Serialization {

    public FastjsonSerialization() {
        super("fastjson");
    }

    @Override
    public byte[] serialize(Object object) throws Exception {
        return JSON.toJSONBytes(object);
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> type) throws Exception {
        return JSON.parseObject(bytes, type);
    }
}
