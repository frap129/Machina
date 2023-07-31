NDK_GO_ARCH_x86 := 386
NDK_GO_ARCH_x86_64 := amd64
NDK_GO_ARCH_arm := arm
NDK_GO_ARCH_arm64 := arm64
NDK_GO_ARCH_mips := mipsx
NDK_GO_ARCH_mips64 := mips64x

# arm : arm-linux-android30
CLANG_FLAGS := --target=$(ANDROID_LLVM_TRIPLE) --gcc-toolchain=$(ANDROID_TOOLCHAIN_ROOT) --sysroot=$(ANDROID_SYSROOT)
export CGO_CFLAGS := $(CLANG_FLAGS) $(CFLAGS) -Wno-unused-command-line-argument
export CGO_LDFLAGS := $(CLANG_FLAGS) $(LDFLAGS) -Wl,-soname=gvproxy
export CC := $(ANDROID_C_COMPILER)
export GOARCH := $(NDK_GO_ARCH_$(ANDROID_ARCH_NAME))
export GOOS := android
export CGO_ENABLED := 1

gvproxy:
	go build -o $(OUT_PATH)/libgvproxy.so ./gvisor-tap-vsock/cmd/gvproxy
