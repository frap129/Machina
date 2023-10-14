/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.system.virtualmachine;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.ParcelFileDescriptor;
import android.system.virtualizationservice.VirtualMachineAppConfig;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents a configuration of a virtual machine. A configuration consists of hardware
 * configurations like the number of CPUs and the size of RAM, and software configurations like the
 * payload to run on the virtual machine.
 *
 * @hide
 */
public final class VirtualMachineConfig {
    /** @hide */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {
            DEBUG_LEVEL_NONE,
            DEBUG_LEVEL_FULL
    })

    public @interface DebugLevel {}

    /**
     * Not debuggable at all. No log is exported from the VM. Debugger can't be attached to the app
     * process running in the VM. This is the default level.
     *
     * @hide
     */
    public static final int DEBUG_LEVEL_NONE = 0;

    /**
     * Fully debuggable. All logs (both logcat and kernel message) are exported. All processes
     * running in the VM can be attached to the debugger. Rooting is possible.
     *
     * @hide
     */
    public static final int DEBUG_LEVEL_FULL = 1;

    /** @hide */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(
            value = {
                    CPU_TOPOLOGY_ONE_CPU,
                    CPU_TOPOLOGY_MATCH_HOST,
            })
    public @interface CpuTopology {}

    /**
     * Run VM with 1 vCPU. This is the default option, usually the fastest to boot and consuming the
     * least amount of resources. Typically the best option for small or ephemeral workloads.
     *
     * @hide
     */
    public static final int CPU_TOPOLOGY_ONE_CPU = 0;

    /**
     * Run VM with vCPU topology matching the physical CPU topology of the host. Usually takes
     * longer to boot and cosumes more resources compared to a single vCPU. Typically a good option
     * for long-running workloads that benefit from parallel execution.
     *
     * @hide
     */
    public static final int CPU_TOPOLOGY_MATCH_HOST = 1;

    /** Loads a config from a file. */
    @NonNull
    static VirtualMachineConfig from(@NonNull File file) throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }
    /** Loads a config from a {@link ParcelFileDescriptor}. */
    @NonNull
    static VirtualMachineConfig from(@NonNull ParcelFileDescriptor fd)
            throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /** Persists this config to a file. */
    void serialize(@NonNull File file) throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the absolute path of the APK which should contain the binary payload that will
     * execute within the VM. Returns null if no specific path has been set.
     *
     * @hide
     */
    @Nullable
    public String getApkPath() {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the path within the APK to the payload config file that defines software aspects of
     * the VM.
     *
     * @hide
     */
    @Nullable
    public String getPayloadConfigPath() {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the name of the payload binary file, in the {@code lib/<ABI>} directory of the APK,
     * that will be executed within the VM.
     *
     * @hide
     */
    @Nullable
    public String getPayloadBinaryName() {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the debug level for the VM.
     *
     * @hide
     */
    @DebugLevel
    public int getDebugLevel() {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns whether the VM's memory will be protected from the host.
     *
     * @hide
     */
    public boolean isProtectedVm() {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the amount of RAM that will be made available to the VM, or 0 if the default size
     * will be used.
     *
     * @hide
     */
    @IntRange(from = 0)
    public long getMemoryBytes() {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the CPU topology configuration of the VM.
     *
     * @hide
     */
    @CpuTopology
    public int getCpuTopology() {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns whether encrypted storage is enabled or not.
     *
     * @hide
     */
    public boolean isEncryptedStorageEnabled() {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the size of encrypted storage (in bytes) available in the VM, or 0 if encrypted
     * storage is not enabled
     *
     * @hide
     */
    @IntRange(from = 0)
    public long getEncryptedStorageBytes() {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns whether the app can read the VM console or log output. If not, the VM output is
     * automatically forwarded to the host logcat.
     *
     * @see Builder#setVmOutputCaptured
     * @hide
     */
    public boolean isVmOutputCaptured() {
        throw new RuntimeException("STUB");
    }

    /**
     * Tests if this config is compatible with other config. Being compatible means that the configs
     * can be interchangeably used for the same virtual machine; they do not change the VM identity
     * or secrets. Such changes include varying the number of CPUs or the size of the RAM. Changes
     * that would alter the identity of the VM (e.g. using a different payload or changing the debug
     * mode) are considered incompatible.
     *
     * @see VirtualMachine#setConfig
     * @hide
     */
    public boolean isCompatibleWith(@NonNull VirtualMachineConfig other) {
        throw new RuntimeException("STUB");
    }

    /**
     * Converts this config object into the parcelable type used when creating a VM via the
     * virtualization service. Notice that the files are not passed as paths, but as file
     * descriptors because the service doesn't accept paths as it might not have permission to open
     * app-owned files and that could be abused to run a VM with software that the calling
     * application doesn't own.
     */
    VirtualMachineAppConfig toVsConfig(@NonNull PackageManager packageManager)
            throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /**
     * A builder used to create a {@link VirtualMachineConfig}.
     *
     * @hide
     */
    public static final class Builder {
        /**
         * Creates a builder for the given context.
         *
         * @hide
         */
        public Builder(@NonNull Context context) {
            throw new RuntimeException("STUB");
        }

        /**
         * Builds an immutable {@link VirtualMachineConfig}
         *
         * @hide
         */
        @NonNull
        public VirtualMachineConfig build() {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets the absolute path of the APK containing the binary payload that will execute within
         * the VM. If not set explicitly, defaults to the split APK containing the payload, if there
         * is one, and otherwise the primary APK of the context.
         *
         * @hide
         */
        @NonNull
        public Builder setApkPath(@NonNull String apkPath) {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets the path within the APK to the payload config file that defines software aspects of
         * the VM. The file is a JSON file; see
         * packages/modules/Virtualization/microdroid/payload/config/src/lib.rs for the format.
         *
         * @hide
         */
        @NonNull
        public Builder setPayloadConfigPath(@NonNull String payloadConfigPath) {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets the name of the payload binary file that will be executed within the VM, e.g.
         * "payload.so". The file must reside in the {@code lib/<ABI>} directory of the APK.
         *
         * <p>Note that VMs only support 64-bit code, even if the owning app is running as a 32-bit
         * process.
         *
         * @hide
         */
        @NonNull
        public Builder setPayloadBinaryName(@NonNull String payloadBinaryName) {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets the debug level. Defaults to {@link #DEBUG_LEVEL_NONE}.
         *
         * <p>If {@link #DEBUG_LEVEL_FULL} is set then logs from inside the VM are exported to the
         * host and adb connections from the host are possible. This is convenient for debugging but
         * may compromise the integrity of the VM - including bypassing the protections offered by a
         * {@linkplain #setProtectedVm protected VM}.
         *
         * <p>Note that it isn't possible to {@linkplain #isCompatibleWith change} the debug level
         * of a VM instance; debug and non-debug VMs always have different secrets.
         *
         * @hide
         */
        @NonNull
        public Builder setDebugLevel(@DebugLevel int debugLevel) {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets whether to protect the VM memory from the host. No default is provided, this must be
         * set explicitly.
         *
         * <p>Note that if debugging is {@linkplain #setDebugLevel enabled} for a protected VM, the
         * VM is not truly protected - direct memory access by the host is prevented, but e.g. the
         * debugger can be used to access the VM's internals.
         *
         * <p>It isn't possible to {@linkplain #isCompatibleWith change} the protected status of a
         * VM instance; protected and non-protected VMs always have different secrets.
         *
         * @see VirtualMachineManager#getCapabilities
         * @hide
         */
        @NonNull
        public Builder setProtectedVm(boolean protectedVm) {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets the amount of RAM to give the VM, in bytes. If not explicitly set then a default
         * size will be used.
         *
         * @hide
         */
        @NonNull
        public Builder setMemoryBytes(@IntRange(from = 1) long memoryBytes) {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets the CPU topology configuration of the VM. Defaults to {@link #CPU_TOPOLOGY_ONE_CPU}.
         *
         * <p>This determines how many virtual CPUs will be created, and their performance and
         * scheduling characteristics, such as affinity masks. Topology also has an effect on memory
         * usage as each vCPU requires additional memory to keep its state.
         *
         * @hide
         */
        @NonNull
        public Builder setCpuTopology(@CpuTopology int cpuTopology) {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets the size (in bytes) of encrypted storage available to the VM. If not set, no
         * encrypted storage is provided.
         *
         * <p>The storage is encrypted with a key deterministically derived from the VM identity
         *
         * <p>The encrypted storage is persistent across VM reboots as well as device reboots. The
         * backing file (containing encrypted data) is stored in the app's private data directory.
         *
         * <p>Note - There is no integrity guarantee or rollback protection on the storage in case
         * the encrypted data is modified.
         *
         * <p>Deleting the VM will delete the encrypted data - there is no way to recover that data.
         *
         * @hide
         */
        @NonNull
        public Builder setEncryptedStorageBytes(@IntRange(from = 1) long encryptedStorageBytes) {
            throw new RuntimeException("STUB");
        }

        /**
         * Sets whether to allow the app to read the VM outputs (console / log). Default is {@code
         * false}.
         *
         * <p>By default, console and log outputs of a {@linkplain #setDebugLevel debuggable} VM are
         * automatically forwarded to the host logcat. Setting this as {@code true} will allow the
         * app to directly read {@linkplain VirtualMachine#getConsoleOutput console output} and
         * {@linkplain VirtualMachine#getLogOutput log output}, instead of forwarding them to the
         * host logcat.
         *
         * <p>If you turn on output capture, you must consume data from {@link
         * VirtualMachine#getConsoleOutput} and {@link VirtualMachine#getLogOutput} - because
         * otherwise the code in the VM may get blocked when the pipe buffer fills up.
         *
         * <p>The {@linkplain #setDebugLevel debug level} must be {@link #DEBUG_LEVEL_FULL} to be
         * set as true.
         *
         * @hide
         */
        @NonNull
        public Builder setVmOutputCaptured(boolean captured) {
            throw new RuntimeException("STUB");
        }
    }
}