FROM arm64v8/alpine:latest

# Needed to clone alpine-make-rootfs
RUN apk add git

RUN apk add clang musl-dev linux-headers
# Needed to pack rootfs
RUN apk add erofs-utils --repository=https://dl-cdn.alpinelinux.org/alpine/edge/testing

# Setup build environment
WORKDIR /machina-rootfs-builder
RUN git clone https://github.com/alpinelinux/alpine-make-rootfs -b master --single-branch --depth=1
COPY /make-rootfs.sh /machina-rootfs-builder
RUN chmod +x /machina-rootfs-builder/make-rootfs.sh

# Mounts
VOLUME /out
VOLUME /machina-rootfs-builder/machina
VOLUME /machina-rootfs-builder/src

ENTRYPOINT ["/machina-rootfs-builder/make-rootfs.sh"]

