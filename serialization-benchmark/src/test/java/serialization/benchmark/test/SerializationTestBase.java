package serialization.benchmark.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.util.Collection;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import serialization.benchmark.FastjsonSerialization;
import serialization.benchmark.FstSerialization;
import serialization.benchmark.FstUnsafeSerialization;
import serialization.benchmark.JacksonJsonSerialization;
import serialization.benchmark.JacksonSmileSerialization;
import serialization.benchmark.JavaSerialization;
import serialization.benchmark.KryoSerialization;
import serialization.benchmark.KryoUnsafeSerialization;
import serialization.benchmark.Serialization;
import serialization.benchmark.SerializationExecutor;

/**
 *
 * @author EKhyst
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class SerializationTestBase {

    private static final int ITERATIONS = Integer.parseInt(System.getProperty("serialization.iterations"));
    
    protected static final ObjectMapper MAPPER = new ObjectMapper()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss X"));
    
    protected static Collection<Object> objects;

    private void executeSerialization(Serialization serialization) throws Exception {
        SerializationExecutor executor = new SerializationExecutor(serialization);
        executor.execute(objects, ITERATIONS);
        executor.printStatistics();
    }

    @Test
    public void test01_javaSerialization() throws Exception {
        executeSerialization(new JavaSerialization());
    }

    @Test
    public void test02_kryo() throws Exception {
        executeSerialization(new KryoSerialization());
    }

    @Test
    public void test03_kryoUnsafe() throws Exception {
        executeSerialization(new KryoUnsafeSerialization());
    }

    @Test
    public void test04_fst() throws Exception {
        executeSerialization(new FstSerialization());
    }

    @Test
    public void test05_fstUnsafe() throws Exception {
        executeSerialization(new FstUnsafeSerialization());
    }

    @Test
    public void test06_jacksonJson() throws Exception {
        executeSerialization(new JacksonJsonSerialization());
    }

    @Test
    public void test07_jacksonSmile() throws Exception {
        executeSerialization(new JacksonSmileSerialization());
    }

    @Test
    public void test08_fastjson() throws Exception {
        executeSerialization(new FastjsonSerialization());
    }
}
