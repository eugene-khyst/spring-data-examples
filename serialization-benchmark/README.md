Java Serialization Libraries Comparison
=======================================

Benchmarks are executed as JUnit tests.

There are 3 benchmarks: `SmallObjectSerializationTest`, `MediumObjectSerializationTest`, `LargeObjectSerializationTest`.
In `src/test/resources` there are separate file for small, medium and large size objects. 
Objects are stored in JSON format. Each file contains 100 objects.
Number of iterations for each serialization framework benchmark is set to `10000` by default to minimize error.
On each iteration 100 objects are serializaed and deserialized.
As a result benchmarks provide arithmetic mean serialization time, arithmetic mean deserialization time and total output size of 100 objects.

To build project and execute benchmarks run:

```
mvn clean install
```

To change the number of iterations pass system property `serialization.iterations`:

```
mvn clean install -Dserialization.iterations=1000
```
