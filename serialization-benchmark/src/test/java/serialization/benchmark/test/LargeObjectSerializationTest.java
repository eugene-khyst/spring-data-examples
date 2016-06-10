package serialization.benchmark.test;

import java.io.InputStream;
import java.util.List;
import org.junit.BeforeClass;
import static serialization.benchmark.test.SerializationTestBase.objects;
import serialization.benchmark.data.User3;

/**
 *
 * @author EKhyst
 */
public class LargeObjectSerializationTest extends SerializationTestBase {
    
    @BeforeClass
    public static void setUpOnce() throws Exception {
        try (InputStream is = SerializationTestBase.class.getResourceAsStream("/generated_10M.json")) {
            objects = MAPPER.readValue(is, MAPPER.getTypeFactory().constructCollectionType(List.class, User3.class));
        }
    }
}
