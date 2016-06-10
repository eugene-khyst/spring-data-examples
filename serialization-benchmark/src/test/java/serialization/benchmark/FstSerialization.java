package serialization.benchmark;

import org.nustaq.serialization.FSTConfiguration;

/**
 *
 * @author EKhyst
 */
public class FstSerialization extends FstSerializationBase {

    private static final FSTConfiguration FST_CONF = FSTConfiguration.createDefaultConfiguration();

    public FstSerialization() {
        super("FST");
    }
    
    @Override
    protected FSTConfiguration getConf() {
        return FST_CONF;
    }
}
