package android.os;

public class ServiceManager {
    /**
     * Returns the specified service from the service manager.
     *
     * If the service is not running, servicemanager will attempt to start it, and this function
     * will wait for it to be ready.
     *
     * @return {@code null} only if there are permission problems or fatal errors.
     */
    public static IBinder waitForService(String name) {
        throw new RuntimeException("STUB");
    }
}
