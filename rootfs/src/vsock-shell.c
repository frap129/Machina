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
  int master;
  pid_t pid;

  // Fork a child process to run bash in the slave pty.
  // TODO: Handle termios and winsize
  pid = forkpty(&master, NULL, NULL, NULL);
  if (pid < 0) {
    perror("forkpty");
    return 1;
  } else if (pid == 0) {
    // Child process
    close(master);
    
    // Replace STDIN/OUT/ERR with the vsocks
    close(STDIN_FILENO);
    close(STDOUT_FILENO);
    close(STDERR_FILENO);
    dup2(vsockin_conn, STDIN_FILENO);
    close(vsockin_conn);
    dup2(vsockout_conn, STDOUT_FILENO);
    dup2(vsockout_conn, STDERR_FILENO);
    close(vsockout_conn);

    // Exec bash
    char *args[] = {"/bin/ash", "-i", NULL};
    execvp(args[0], args);
    perror("execv");
    exit(1);
  }

  // Parent process: wait until child exits
  waitpid(pid, NULL, 0);
  close(master); 

  // Close the sockets.
  close(vsockin_socket);
  close(vsockout_socket);
  close(vsockin_conn);
  close(vsockout_conn);

  return 0;
}
