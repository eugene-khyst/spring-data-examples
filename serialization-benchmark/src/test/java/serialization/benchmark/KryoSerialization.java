package serialization.benchmark;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author EKhyst
 */
public class KryoSerialization extends KryoSerializationBase {

    public KryoSerialization() {
        super("Kryo");
    }

    @Override
    protected Input getInput(InputStream is) {
        return new Input(is);
    }

    @Override
    protected Output getOutput(OutputStream os) {
        return new Output(os);
    }
}
