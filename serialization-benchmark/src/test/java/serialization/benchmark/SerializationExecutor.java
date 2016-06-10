package serialization.benchmark;

import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author EKhyst
 */
public class SerializationExecutor {

    private final Serialization serialization;

    private final SummaryStatistics serializationTimeStatistics = new SummaryStatistics();
    private final SummaryStatistics deserializationTimeStatistics = new SummaryStatistics();
    private final SummaryStatistics sizeStatistics = new SummaryStatistics();

    public SerializationExecutor(Serialization serialization) {
        this.serialization = serialization;
    }

    public void execute(Collection<?> objects, int iterations) throws Exception {
        System.out.print(serialization.getName() + ": ");
        for (int i = 0; i < iterations; i++) {
            double percentDone = 100.0 * i / iterations;
            if (percentDone % 10 == 0) {
                System.out.format("%.0f%%...", percentDone);
            }
            
            long serializationNanoTime = 0;
            long deserializationNanoTime = 0;
            long sizeBytes = 0;
            for (Object object : objects) {
                long serializationStartTime = System.nanoTime();
                byte[] bytes = serialization.serialize(object);
                serializationNanoTime += System.nanoTime() - serializationStartTime;
                sizeBytes += bytes.length;

                long deserializationStartTime = System.nanoTime();
                Object readObject = serialization.deserialize(bytes, object.getClass());
                deserializationNanoTime += System.nanoTime() - deserializationStartTime;

                assertEquals(object, readObject);
            }
            serializationTimeStatistics.addValue(serializationNanoTime);
            deserializationTimeStatistics.addValue(deserializationNanoTime);
            sizeStatistics.addValue(sizeBytes);
        }
        System.out.println("100%");
    }

    public void clear() {
        serializationTimeStatistics.clear();
        deserializationTimeStatistics.clear();
        sizeStatistics.clear();
    }

    public void printStatistics() {
        System.out.println(serialization.getName());
        System.out.println("Mean serialization time: " + formatDuration(serializationTimeStatistics.getMean()));
        System.out.println("Mean deserialization time: " + formatDuration(deserializationTimeStatistics.getMean()));
        System.out.println("Mean output size: " + FileUtils.byteCountToDisplaySize((long) sizeStatistics.getMean()));
        System.out.println();
    }

    private static String formatDuration(double nanoseconds) {
        return String.format("%.2f ms", nanoseconds / 1_000_000);
    }
}
