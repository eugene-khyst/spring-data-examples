package serialization.benchmark;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.io.UnsafeInput;
import com.esotericsoftware.kryo.io.UnsafeOutput;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author EKhyst
 */
public class KryoUnsafeSerialization extends KryoSerializationBase {

    public KryoUnsafeSerialization() {
        super("Kryo Unsafe");
    }

    @Override
    protected Input getInput(InputStream is) {
        return new UnsafeInput(is);
    }

    @Override
    protected Output getOutput(OutputStream os) {
        return new UnsafeOutput(os);
    }
}
