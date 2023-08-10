#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <malloc.h>
#include <jni.h>
#include <jni.h>

#define LOG_TAG "machina-jni"

extern "C"
int read_if_available(int vsock_fd, char *data, int data_len) {
    fd_set readfds;
    struct timeval timeout;

    // Initialize the file descriptor set.
    FD_ZERO(&readfds);
    FD_SET(vsock_fd, &readfds);

    // Set the timeout to 1 second.
    timeout.tv_sec = 0;
    timeout.tv_usec = 1;

    // Wait for data to become available on the vsock.
    data_len = select(vsock_fd + 1, &readfds, NULL, NULL, &timeout);

    // If there is no data to read, return 0.
    if (data_len == 0) {
        return 0;
    }

        // Otherwise, read the data.
    else {
        data_len = read(vsock_fd, data, sizeof(data));
    }

    return data_len;
}


extern "C"
JNIEXPORT void JNICALL
Java_dev_maples_vm_model_services_MachinaService_proxyVsockToUnix(JNIEnv *env, jobject thiz,
                                                                  jint vsock_fd,
                                                                  jstring unix_socket) {
    // Get socket path info
    char *unix_path = strdup(env->GetStringUTFChars(unix_socket, 0));

    // Check if the unix socket exists.
    if (access(unix_path, F_OK) == 0) {
        // The file exists.
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Socket exists: \n");
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "The file no exists.\n");
    }

    int fd;
    struct sockaddr_un addr;

    // Create a socket
    fd = socket(AF_UNIX, SOCK_STREAM, 0);
    if (fd < 0) {
        __android_log_write(ANDROID_LOG_ERROR, LOG_TAG, "failed to create socket");
        return;
    }

    // Set the address of the server
    memset(&addr, 0, sizeof(addr));
    addr.sun_family = AF_UNIX;
    strcpy(addr.sun_path, unix_path);

    // Connect to the server
    int ret = connect(fd, (struct sockaddr *) &addr, sizeof(addr));
    if (ret < 0) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "failed to bind to socket: %s",
                            unix_path);
        return;
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "socket was great success!");

    }


    size_t buf_size = sizeof(char) * 4000;
    char *buf = (char *) malloc(buf_size);

    // TODO: Add stop condition to return error if one of the sockets closes
    while (1) {
        int n = read_if_available(vsock_fd, buf, buf_size);
        write(fd, buf, n);

        n = read_if_available(fd, buf, buf_size);
        write(vsock_fd, buf, n);
    }

    free(buf);
    close(fd);

    __android_log_write(ANDROID_LOG_ERROR, LOG_TAG, "proxyVsockToUnix exiting");

    return;
}