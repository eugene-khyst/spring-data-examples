package serialization.benchmark.test;

import java.io.InputStream;
import java.util.List;
import org.junit.BeforeClass;
import static serialization.benchmark.test.SerializationTestBase.objects;
import serialization.benchmark.data.User2;

/**
 *
 * @author EKhyst
 */
public class MediumObjectSerializationTest extends SerializationTestBase {
    
    @BeforeClass
    public static void setUpOnce() throws Exception {
        try (InputStream is = SerializationTestBase.class.getResourceAsStream("/generated_130KB.json")) {
            objects = MAPPER.readValue(is, MAPPER.getTypeFactory().constructCollectionType(List.class, User2.class));
        }
    }
}
