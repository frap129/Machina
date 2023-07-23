#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/wait.h>
#include <termios.h>
#include <unistd.h>
#include <linux/vm_sockets.h>
#include <pty.h>
#include <fcntl.h>

#define RX_PORT 3000
#define TX_PORT 5000

int vsock_listen(int vsock_socket, int port) {
  // Verify the socket is valid.
  if (vsock_socket < 0) {
    perror("socket");
    exit(1);
  }

  // Bind the socket to a CID and port.
  struct sockaddr_vm stdin_addr;
  memset(&stdin_addr, 0, sizeof(struct sockaddr_vm));
  stdin_addr.svm_family = AF_VSOCK;
  stdin_addr.svm_port = port;
  stdin_addr.svm_cid = VMADDR_CID_ANY;
  if (bind(vsock_socket, (struct sockaddr *)&stdin_addr, sizeof(stdin_addr)) < 0) {
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

int read_if_available(int vsock_fd, char *data, int data_len) {
  fd_set readfds;
  struct timeval timeout;

  // Initialize the file descriptor set.
  FD_ZERO(&readfds);
  FD_SET(vsock_fd, &readfds);

  // Set the timeout to 1 second.
  timeout.tv_sec = 0;
  timeout.tv_usec = 50;

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
  int vsockin_socket = socket(AF_VSOCK, SOCK_STREAM, 0);
  int vsockout_socket = socket(AF_VSOCK, SOCK_STREAM, 0);
  int vsockin_conn = vsock_listen(vsockin_socket, RX_PORT);
  int vsockout_conn = vsock_listen(vsockout_socket, TX_PORT);
  int master, slave;
  pid_t pid;


  // Create a new master/slave pty pair.
  // TODO: Handle termios and winsize
  if (openpty(&master, &slave, NULL, NULL, NULL) < 0) {
    perror("openpty");
    return 1;
  }

  // Create pipe to communicate with shell process
  int to_shell[2];
  int from_shell[2];
  pipe(to_shell);
  pipe(from_shell);

  // Fork a child process to run bash in the slave pty.
  pid = fork();
  if (pid < 0) {
    perror("fork");
    return 1;
  } else if (pid == 0) {
    // Child process: exec bash
    
    dup2(to_shell[0], STDIN_FILENO);
    close(to_shell[0]);

    close(from_shell[0]);
    dup2(from_shell[1], STDOUT_FILENO);
    dup2(from_shell[1], STDERR_FILENO);
    close(from_shell[1]); 

    char *args[] = {"/bin/ash", "-i", NULL};
    execvp(args[0], args);
    perror("execv");
    
    close(to_shell[1]);
    close(slave);
    close(master);
    exit(1);
  }

  // Parent process: Transfer data between vsocks and bash process.
  close(to_shell[0]);
  close(from_shell[1]);
  close(master); 

  int buf_size = sizeof(char) * 1024;
  char *buf = malloc(buf_size);
  while (1) {

    // Write incomming data to stdin
    int n = read_if_available(vsockin_conn, buf, sizeof(buf));
    if (n > 0) {
      write(to_shell[1], buf, n);
    }

    // Send data from stdout
    n = read_if_available(from_shell[0], buf, sizeof(buf));
    if (n > 0) {
      write(vsockout_conn, buf, n);
    }
  }

  // Wait for child process to exit
  waitpid(pid, NULL, 0);

  // Close the sockets.
  close(vsockin_socket);
  close(vsockout_socket);
  close(vsockin_conn);
  close(vsockout_conn);
  free(buf);

  return 0;
}
