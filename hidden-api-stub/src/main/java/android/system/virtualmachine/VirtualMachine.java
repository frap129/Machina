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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.WorkerThread;

import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executor;

/**
 * Represents an VM instance, with its own configuration and state. Instances are persistent and are
 * created or retrieved via {@link VirtualMachineManager}.
 *
 * <p>The {@link #run} method actually starts up the VM and allows the payload code to execute. It
 * will continue until it exits or {@link #stop} is called. Updates on the state of the VM can be
 * received using {@link #setCallback}. The app can communicate with the VM using {@link
 * #connectToVsockServer} or {@link #connectVsock}.
 *
 * <p>The payload code running inside the VM has access to a set of native APIs; see the <a
 * href="https://cs.android.com/android/platform/superproject/+/master:packages/modules/Virtualization/vm_payload/README.md">README
 * file</a> for details.
 *
 * <p>Each VM has a unique secret, computed from the APK that contains the code running in it, the
 * VM configuration, and a random per-instance salt. The secret can be accessed by the payload code
 * running inside the VM (using {@code AVmPayload_getVmInstanceSecret}) but is not made available
 * outside it.
 *
 * @hide
 */
public class VirtualMachine implements AutoCloseable {
    /** The permission needed to create or run a virtual machine. */
    public static final String MANAGE_VIRTUAL_MACHINE_PERMISSION =
            "android.permission.MANAGE_VIRTUAL_MACHINE";

    /**
     * The permission needed to create a virtual machine with more advanced configuration options.
     */
    public static final String USE_CUSTOM_VIRTUAL_MACHINE_PERMISSION =
            "android.permission.USE_CUSTOM_VIRTUAL_MACHINE";

    /**
     * The lowest port number that can be used to communicate with the virtual machine payload.
     *
     * @see #connectToVsockServer
     * @see #connectVsock
     */
    @SuppressLint("MinMaxConstant") // Won't change: see man 7 vsock.
    public static final long MIN_VSOCK_PORT = 1024;

    /**
     * The highest port number that can be used to communicate with the virtual machine payload.
     *
     * @see #connectToVsockServer
     * @see #connectVsock
     */
    @SuppressLint("MinMaxConstant") // Won't change: see man 7 vsock.
    public static final long MAX_VSOCK_PORT = (1L << 32) - 1;

    /**
     * Status of a virtual machine
     *
     * @hide
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {
            STATUS_STOPPED,
            STATUS_RUNNING,
            STATUS_DELETED
    })
    public @interface Status {}

    /** The virtual machine has just been created, or {@link #stop} was called on it. */
    public static final int STATUS_STOPPED = 0;

    /** The virtual machine is running. */
    public static final int STATUS_RUNNING = 1;

    /**
     * The virtual machine has been deleted. This is an irreversible state. Once a virtual machine
     * is deleted all its secrets are permanently lost, and it cannot be run. A new virtual machine
     * with the same name and config may be created, with new and different secrets.
     */
    public static final int STATUS_DELETED = 2;

    /**
     * Creates a virtual machine from an {@link VirtualMachineDescriptor} object and associates it
     * with the given name.
     *
     * <p>The new virtual machine will be in the same state as the descriptor indicates.
     *
     * <p>Once a virtual machine is imported it is persisted until it is deleted by calling {@link
     * #delete}. The imported virtual machine is in {@link #STATUS_STOPPED} state. To run the VM,
     * call {@link #run}.
     */
    @NonNull
    static VirtualMachine fromDescriptor(
            @NonNull Context context,
            @NonNull String name,
            @NonNull VirtualMachineDescriptor vmDescriptor)
            throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /**
     * Creates a virtual machine with the given name and config. Once a virtual machine is created
     * it is persisted until it is deleted by calling {@link #delete}. The created virtual machine
     * is in {@link #STATUS_STOPPED} state. To run the VM, call {@link #run}.
     */
    @NonNull
    static VirtualMachine create(
            @NonNull Context context, @NonNull String name, @NonNull VirtualMachineConfig config)
            throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /** Loads a virtual machine that is already created before. */
    @Nullable
    static VirtualMachine load(@NonNull Context context, @NonNull String name)
            throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    void delete(Context context, String name) throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    static void deleteVmDirectory(Context context, String name) throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the name of this virtual machine. The name is unique in the package and can't be
     * changed.
     *
     * @hide
     */
    @NonNull
    public String getName() {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the currently selected config of this virtual machine. There can be multiple virtual
     * machines sharing the same config. Even in that case, the virtual machines are completely
     * isolated from each other; they have different secrets. It is also possible that a virtual
     * machine can change its config, which can be done by calling {@link #setConfig}.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @hide
     */
    @WorkerThread
    @NonNull
    public VirtualMachineConfig getConfig() {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the current status of this virtual machine.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @hide
     */
    @WorkerThread
    @Status
    public int getStatus() {
        throw new RuntimeException("STUB");
    }

    /**
     * Registers the callback object to get events from the virtual machine. If a callback was
     * already registered, it is replaced with the new one.
     *
     * @hide
     */
    public void setCallback(
            @NonNull Executor executor,
            @NonNull VirtualMachineCallback callback) {
        throw new RuntimeException("STUB");
    }

    /**
     * Clears the currently registered callback.
     *
     * @hide
     */
    public void clearCallback() {
        throw new RuntimeException("STUB");
    }

    /**
     * Runs this virtual machine. The returning of this method however doesn't mean that the VM has
     * actually started running or the OS has booted there. Such events can be notified by
     * registering a callback using {@link #setCallback} before calling {@code run()}.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @throws VirtualMachineException if the virtual machine is not stopped or could not be
     *     started.
     * @hide
     */
    @WorkerThread
    @RequiresPermission(MANAGE_VIRTUAL_MACHINE_PERMISSION)
    public void run() throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the stream object representing the console output from the virtual machine. The
     * console output is only available if the {@link VirtualMachineConfig} specifies that it should
     * be {@linkplain VirtualMachineConfig#isVmOutputCaptured captured}.
     *
     * <p>If you turn on output capture, you must consume data from {@code getConsoleOutput} -
     * because otherwise the code in the VM may get blocked when the pipe buffer fills up.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @throws VirtualMachineException if the stream could not be created, or capturing is turned
     *     off.
     * @hide
     */
    @WorkerThread
    @NonNull
    public InputStream getConsoleOutput() throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the stream object representing the log output from the virtual machine. The log
     * output is only available if the VirtualMachineConfig specifies that it should be {@linkplain
     * VirtualMachineConfig#isVmOutputCaptured captured}.
     *
     * <p>If you turn on output capture, you must consume data from {@code getLogOutput} - because
     * otherwise the code in the VM may get blocked when the pipe buffer fills up.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @throws VirtualMachineException if the stream could not be created, or capturing is turned
     *     off.
     * @hide
     */
    @WorkerThread
    @NonNull
    public InputStream getLogOutput() throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /**
     * Stops this virtual machine. Stopping a virtual machine is like pulling the plug on a real
     * computer; the machine halts immediately. Software running on the virtual machine is not
     * notified of the event. Writes to {@linkplain
     * VirtualMachineConfig.Builder#setEncryptedStorageBytes encrypted storage} might not be
     * persisted, and the instance might be left in an inconsistent state.
     *
     * <p>For a graceful shutdown, you could request the payload to call {@code exit()}, e.g. via a
     * {@linkplain #connectToVsockServer binder request}, and wait for {@link
     * VirtualMachineCallback#onPayloadFinished} to be called.
     *
     * <p>A stopped virtual machine can be re-started by calling {@link #run()}.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @throws VirtualMachineException if the virtual machine is not running or could not be
     *     stopped.
     * @hide
     */
    @WorkerThread
    public void stop() throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /**
     * Stops this virtual machine, if it is running.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @see #stop()
     * @hide
     */
    @WorkerThread
    @Override
    public void close() {
        throw new RuntimeException("STUB");
    }

    /**
     * Changes the config of this virtual machine to a new one. This can be used to adjust things
     * like the number of CPU and size of the RAM, depending on the situation (e.g. the size of the
     * application to run on the virtual machine, etc.)
     *
     * <p>The new config must be {@linkplain VirtualMachineConfig#isCompatibleWith compatible with}
     * the existing config.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @return the old config
     * @throws VirtualMachineException if the virtual machine is not stopped, or the new config is
     *     incompatible.
     * @hide
     */
    @WorkerThread
    @NonNull
    public VirtualMachineConfig setConfig(@NonNull VirtualMachineConfig newConfig)
            throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /**
     * Connect to a VM's binder service via vsock and return the root IBinder object. Guest VMs are
     * expected to set up vsock servers in their payload. After the host app receives the {@link
     * VirtualMachineCallback#onPayloadReady}, it can use this method to establish a connection to
     * the guest VM.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @throws VirtualMachineException if the virtual machine is not running or the connection
     *     failed.
     * @hide
     */
    @WorkerThread
    @NonNull
    public IBinder connectToVsockServer(
            @IntRange(from = MIN_VSOCK_PORT, to = MAX_VSOCK_PORT) long port)
            throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /**
     * Opens a vsock connection to the VM on the given port.
     *
     * <p>The caller is responsible for closing the returned {@code ParcelFileDescriptor}.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @throws VirtualMachineException if connecting fails.
     * @hide
     */
    @WorkerThread
    @NonNull
    public ParcelFileDescriptor connectVsock(
            @IntRange(from = MIN_VSOCK_PORT, to = MAX_VSOCK_PORT) long port)
            throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns the root directory where all files related to this {@link VirtualMachine} (e.g.
     * {@code instance.img}, {@code apk.idsig}, etc) are stored.
     *
     * @hide
     */
    @NonNull
    public File getRootDir() {
        throw new RuntimeException("STUB");
    }

    /**
     * Captures the current state of the VM in a {@link VirtualMachineDescriptor} instance. The VM
     * needs to be stopped to avoid inconsistency in its state representation.
     *
     * <p>The state of the VM is not actually copied until {@link
     * VirtualMachineManager#importFromDescriptor} is called. It is recommended that the VM not be
     * started until that operation is complete.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @return a {@link VirtualMachineDescriptor} instance that represents the VM's state.
     * @throws VirtualMachineException if the virtual machine is not stopped, or the state could not
     *     be captured.
     * @hide
     */
    @WorkerThread
    @NonNull
    public VirtualMachineDescriptor toDescriptor() throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    @Override
    public String toString() {
        throw new RuntimeException("STUB");
    }
}
