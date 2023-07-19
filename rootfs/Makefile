DOCKER ?= docker
OUT ?= out
IMAGE := out/machina-rootfs.img

rootfs:
	$(DOCKER) build --tag 'machina-rootfs-builder' docker
	$(DOCKER) run -v ./out:/out -v ./machina-tools:/machina-rootfs-builder/machina machina-rootfs-builder
	@ls $(IMAGE)

.PHONY: rootfs
