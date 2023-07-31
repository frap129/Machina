cmake_minimum_required(VERSION 3.22.1)
project("machina-jni")


set(CMAKE_EXECUTE_PROCESS_COMMAND_ECHO STDOUT)
SET(GVPROXY_BIN gvproxy)
SET(LIB_OUT_PATH ../jniLibs/${CMAKE_ANDROID_ARCH_ABI})

link_directories(${CMAKE_LIBRARY_OUTPUT_DIRECTORY})
include_directories(${CMAKE_LIBRARY_OUTPUT_DIRECTORY})

add_custom_target(${GVPROXY_BIN}
        WORKING_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}"
        COMMENT "Building ${GVPROXY_BIN}"
        VERBATIM COMMAND make ${GVPROXY_BIN}
        ANDROID_ARCH_NAME=${ANDROID_ARCH_NAME}
        ANDROID_C_COMPILER=${ANDROID_C_COMPILER}
        ANDROID_TOOLCHAIN_ROOT=${ANDROID_TOOLCHAIN_ROOT}
        ANDROID_LLVM_TRIPLE=${ANDROID_LLVM_TRIPLE}
        ANDROID_SYSROOT=${ANDROID_SYSROOT}
        ANDROID_PACKAGE_NAME=${ANDROID_PACKAGE_NAME}
        GRADLE_USER_HOME=${GRADLE_USER_HOME}
        CFLAGS=${CMAKE_C_FLAGS}\ -Wno-unused-command-line-argument
        LDFLAGS=${CMAKE_SHARED_LINKER_FLAGS}
        OUT_PATH=${LIB_OUT_PATH}
        )

add_library(${CMAKE_PROJECT_NAME} SHARED
        machina-jni.cpp
        )

add_dependencies(${CMAKE_PROJECT_NAME} ${GVPROXY_BIN})

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log
        )
