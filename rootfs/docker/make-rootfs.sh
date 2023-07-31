#!/bin/sh

# Build rootfs into a directory
./alpine-make-rootfs/alpine-make-rootfs \
    --packages 'alpine-base socat' \
    /out/machina-rootfs/

# Copy Machina tools to rootfs
mkdir /out/machina-rootfs/opt/machina
cp machina/* /out/machina-rootfs/opt/machina/

# Build C files
clang -o /out/machina-rootfs/opt/machina/vsock-shell src/vsock-shell.c
clang -o /out/machina-rootfs/opt/machina/gvforwarder-proxy src/gvforwarder-proxy.c

# Build gvforwarder
if [ ! -f /out/gvforwarder ]; then
  cd gvisor-tap-vsock
  make vm
  cp bin/gvforwarder /out/
  cd ..
fi
cp /out/gvforwarder /out/machina-rootfs/opt/machina

# Write fstab
echo '/dev/vda / erofs rw,noatime 0 0
tmpfs /tmp tmpfs rw,noatime 0 0' > /out/machina-rootfs/etc/fstab

# Add init to inittab
echo "::sysinit:/opt/machina/init" >> /out/machina-rootfs/etc/inittab
echo "::wait:/opt/machina/postinit" >> /out/machina-rootfs/etc/inittab

# Pack final rootfs
mkfs.erofs /out/machina-rootfs.img /out/machina-rootfs
chmod 666 /out/machina-rootfs.img

# Cleanup
rm -rf /out/machina-rootfs
