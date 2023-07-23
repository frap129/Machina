DOCKER ?= docker
OUT ?= out
IMAGE := out/machina-rootfs.img

rootfs:
	$(DOCKER) build --tag 'machina-rootfs-builder' docker
	$(DOCKER) run -v ./out:/out -v ./scripts:/machina-rootfs-builder/machina -v ./src:/machina-rootfs-builder/src machina-rootfs-builder
	@ls $(IMAGE)

.PHONY: rootfs
