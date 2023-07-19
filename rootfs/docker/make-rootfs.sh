#!/bin/sh

# Build rootfs into a directory
./alpine-make-rootfs/alpine-make-rootfs \
    --packages socat \
    /out/machina-rootfs/

# Copy Machina tools to rootfs
mkdir /out/machina-rootfs/opt/machina
cp machina/* /out/machina-rootfs/opt/machina/ 

# Add init to inittab
echo "::sysinit:/opt/machina/init" >> /out/machina-rootfs/etc/inittab
echo "::wait:/opt/machina/postinit" >> /out/machina-rootfs/etc/inittab

# Pack final rootfs
mkfs.erofs /out/machina-rootfs.img /out/machina-rootfs
chmod 666 /out/machina-rootfs.img

# Cleanup
rm -rf /out/machina-rootfs
