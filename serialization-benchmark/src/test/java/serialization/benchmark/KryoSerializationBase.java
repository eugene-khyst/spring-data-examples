package serialization.benchmark;

import serialization.benchmark.data.Friend;
import serialization.benchmark.data.Gender;
import serialization.benchmark.data.User1;
import serialization.benchmark.data.Post;
import serialization.benchmark.data.User3;
import serialization.benchmark.data.User2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author EKhyst
 */
public abstract class KryoSerializationBase extends Serialization {

    private static final KryoFactory KRYO_FACTORY = new KryoFactory() {
        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            kryo.register(User1.class);
            kryo.register(User2.class);
            kryo.register(User3.class);
            kryo.register(Gender.class);
            kryo.register(Friend.class);
            kryo.register(Post.class);
            kryo.register(Date.class);
            kryo.register(ArrayList.class);
            return kryo;
        }
    };

    private static final KryoPool KRYO_POOL = new KryoPool.Builder(KRYO_FACTORY)
            .softReferences()
            .build();

    public KryoSerializationBase(String name) {
        super(name);
    }

    protected abstract Input getInput(InputStream is);
    
    protected abstract Output getOutput(OutputStream os);
    
    @Override
    public byte[] serialize(Object object) throws Exception {
        Kryo kryo = KRYO_POOL.borrow();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (Output output = getOutput(baos)) {
                kryo.writeObject(output, object);
            }
            return baos.toByteArray();
        } finally {
            KRYO_POOL.release(kryo);
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> type) throws Exception {
        Kryo kryo = KRYO_POOL.borrow();
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            try (Input input = getInput(bais)) {
                return kryo.readObject(input, type);
            }
        } finally {
            KRYO_POOL.release(kryo);
        }
    }
}
