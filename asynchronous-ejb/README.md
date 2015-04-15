Asynchronous Method Invokation in EJB
=====================================

EJB 3.1 has a simple way of asynchronous method invokation. Methods of the session bean can be implemented as *asynchronous*. The control is returned to the client by EJB container before the method is invoked on session bean instance.

Annotate method of session EJB with `javax.ejb.Asynchronous` to mark method as an asynchronous. `@Asynchronous` annotation can be applied at class level to mark all methods as asynchronous.

The method should return either `void` of the `Future<V>` interface representing result of the computation.

The `javax.ejb.AsyncResult<V>` helper class is the implementation of `Future<V>` interface. It accepts result of computation as constructor parameter.

```
@Stateless
@Local(AsynchronousService.class)
public class AsynchronousServiceBean implements AsynchronousService {

    @Asynchronous
    @Override
    public Future<Long> performLongRunningTask(String jobName) {
        long startTime = System.currentTimeMillis();
        doPerformLongRunningTask(timeout);
        long time = System.currentTimeMillis() - startTime;
        return new AsyncResult<>(jobName);
    }

    private void doPerformLongRunningTask(long timeout) {
        try {
            LOGGER.info("Sleeping {} milliseconds", timeout);
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }
}
```

Use `wasCancelCalled` method of `javax.ejb.SessionContext` class instance that can be injected using `@Resource` annotation to determine whether client cancelled computation using `cancel` method of the `Future<V>` interface.

```
@Stateless
@Local(AsynchronousService.class)
public class AsynchronousServiceBean implements AsynchronousService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsynchronousServiceBean.class);
    
    @Resource
    private SessionContext sessionContext;

    @Asynchronous
    @Override
    public Future<Long> performLongRunningTask(long timeout) {
        long startTime = System.currentTimeMillis();
        while (!sessionContext.wasCancelCalled()) {
            doPerformLongRunningTask(timeout);
        }
        long time = System.currentTimeMillis() - startTime;
        return new AsyncResult<>(time);
    }

    private void doPerformLongRunningTask(long timeout) {
        try {
            LOGGER.info("Sleeping {} milliseconds", timeout);
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }
}
```

```
long timeout = 100;
long totalTimeout = 10 * timeout;

// Start long running task asynchronously 
Future<Long> result = exampleService.performLongRunningTask(timeout);

TimeUnit.MILLISECONDS.sleep(totalTimeout);

// Cancel long running task
result.cancel(true);
```

[Original article](http://developer-should-know.tumblr.com/post/116453807202/asynchronous-method-invokation-in-ejb)
