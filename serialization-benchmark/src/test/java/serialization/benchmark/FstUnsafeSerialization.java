package serialization.benchmark;

import org.nustaq.serialization.FSTConfiguration;

/**
 *
 * @author EKhyst
 */
public class FstUnsafeSerialization extends FstSerializationBase {

    // Fastest, but uses Unsafe class to write primitives and arrays
    private static final FSTConfiguration FST_UNSAFE_BINARY_CONF = FSTConfiguration.createUnsafeBinaryConfiguration();

    public FstUnsafeSerialization() {
        super("FST Unsafe");
    }
    
    @Override
    protected FSTConfiguration getConf() {
        return FST_UNSAFE_BINARY_CONF;
    }
}
