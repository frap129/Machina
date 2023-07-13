# TODO

### App
- Add UI
  - Bottom tab navigation
    - Machines
    - New Machine
    - Settings
  - Bundle kernel, rootfs, data, and swap images in assets
  - Copy assets to /data/local/tmp/machina
  - Add termux view

### VM
- Automate rootfs building
- mount root as overlay FS (https://github.com/fitu996/overlayroot.sh)
- setup vsock proxy
  - Fork from init
  - use socat to forward STDIO over vsock
- setup lxc/lxd