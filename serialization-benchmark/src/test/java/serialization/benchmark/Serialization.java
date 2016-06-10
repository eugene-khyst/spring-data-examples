package serialization.benchmark;

/**
 *
 * @author EKhyst
 */
public abstract class Serialization {

    private final String name;

    public Serialization(String name) {
        this.name = name;
    }

    public abstract byte[] serialize(Object object) throws Exception;
    
    public abstract Object deserialize(byte[] bytes, Class<?> type) throws Exception;

    public String getName() {
        return name;
    }
}
