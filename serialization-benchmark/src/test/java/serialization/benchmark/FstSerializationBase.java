package serialization.benchmark;

import org.nustaq.serialization.FSTConfiguration;

/**
 *
 * @author EKhyst
 */
public abstract class FstSerializationBase extends Serialization {

    public FstSerializationBase(String name) {
        super(name);
    }

    protected abstract FSTConfiguration getConf();
    
    @Override
    public byte[] serialize(Object object) throws Exception {
        return getConf().asByteArray(object);
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> type) throws Exception {
        return getConf().asObject(bytes);
    }
}
