#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <sys/wait.h>
#include <unistd.h>
#include <linux/vm_sockets.h>
#include <pty.h>

#define VSOCK_PORT 3001
#define UNIX_PATH  "/tmp/.machina_network.sock"

int vsock_listen(int vsock_socket, int port) {
  // Verify the socket is valid.
  if (vsock_socket < 0) {
    perror("socket");
    exit(1);
  }

  // Bind the socket to a CID and port.
  struct sockaddr_vm addr;
  memset(&addr, 0, sizeof(struct sockaddr_vm));
  addr.svm_family = AF_VSOCK;
  addr.svm_port = port;
  addr.svm_cid = VMADDR_CID_ANY;
  if (bind(vsock_socket, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
    perror("bind");
    exit(1);
  }

  // Listen for connections on the stdin socket.
  listen(vsock_socket, 1);

  // Accept a connection on the stdin socket.
  int vsock_conn = accept(vsock_socket, NULL, NULL);
  if (vsock_conn < 0) {
    perror("accept");
    exit(1);
  }
 
  return vsock_conn;
}

int unix_listen(int unix_socket, const char* path) {
  // Verify the socket is valid.
  if (unix_socket < 0) {
    perror("socket");
    exit(1);
  }

  struct sockaddr_un addr;

  // Create the socket.
  if (unix_socket < 0) {
    perror ("socket");
    exit (EXIT_FAILURE);
  }

  // Bind the socket.
  size_t path_size = strlen(path);
  addr.sun_family = AF_UNIX;
  strncpy(addr.sun_path, path, path_size + 1);
  if (bind (unix_socket, (struct sockaddr *) &addr, sizeof(addr)) < 0) {
      perror ("bind");
      exit (EXIT_FAILURE);
  }

  // Listen for connections on the socket.
  listen(unix_socket, 1);

  // Accept a connection on the socket.
  int unix_conn = accept(unix_socket, NULL, NULL);
  if (unix_conn < 0) {
    perror("accept");
    exit(1);
  }
 
    return unix_conn;
}

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

int main() {
  int unix_socket = socket(AF_UNIX, SOCK_STREAM, 0);
  int vsock_socket = socket(AF_VSOCK, SOCK_STREAM, 0);
  int vsock_conn = vsock_listen(vsock_socket, VSOCK_PORT);
  int unix_conn;
  pid_t pid;

  pid = fork();
  if (pid < 0) {
    perror("fork");
    return 1;
  } else if (pid == 0) {
    // Child process
    close(vsock_conn);

    sleep(2);

    // Exec gvforwarder
    char *args[] = {"/opt/machina/gvforwarder", "-url", "unix:///tmp/.machina_network.sock", NULL};
    execvp(args[0], args);
    perror("execvp");
    exit(1);
  }

  unix_conn = unix_listen(unix_socket, UNIX_PATH);


     int buf_size = sizeof(char) * 1500;
    char *buf = (char *) malloc(buf_size);
   while (1) {

       // Write incomming data to stdin
        int n = read_if_available(unix_conn, buf, buf_size);
        if (n > 0) {
            //__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "incoming: %s", buf);
            write(vsock_conn, buf, n);
        }

        // Send data from stdout
        n = read_if_available(vsock_conn, buf, buf_size);
        if (n > 0) {
            write(unix_conn, buf, n);
        }
    }


  // Parent process: wait until child exits
  waitpid(pid, NULL, 0);

  // Close the sockets.
  close(vsock_socket);
  close(vsock_conn);
  close(unix_socket);

  return 0;
}
