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

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Manages {@linkplain VirtualMachine virtual machine} instances created by an app. Each instance is
 * created from a {@linkplain VirtualMachineConfig configuration} that defines the shape of the VM
 * (RAM, CPUs), the code to execute within it, etc.
 *
 * <p>Each virtual machine instance is named; the configuration and related state of each is
 * persisted in the app's private data directory and an instance can be retrieved given the name.
 * The name must be a valid directory name and must not contain '/'.
 *
 * <p>The app can then start, stop and otherwise interact with the VM.
 *
 * <p>An instance of {@link VirtualMachineManager} can be obtained by calling {@link
 * Context#getSystemService(Class)}.
 *
 * @hide
 */
public class VirtualMachineManager {
    /** @hide */
    public VirtualMachineManager(@NonNull Context context) {
        throw new RuntimeException("STUB");
    }

    /**
     * Capabilities of the virtual machine implementation.
     *
     * @hide
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true, value = {
            CAPABILITY_PROTECTED_VM,
            CAPABILITY_NON_PROTECTED_VM
    })
    public @interface Capability {}

    /* The implementation supports creating protected VMs, whose memory is inaccessible to the
     * host OS.
     */
    public static final int CAPABILITY_PROTECTED_VM = 1;

    /* The implementation supports creating non-protected VMs, whose memory is accessible to the
     * host OS.
     */
    public static final int CAPABILITY_NON_PROTECTED_VM = 2;

    /**
     * Returns a set of flags indicating what this implementation of virtualization is capable of.
     *
     * @see #CAPABILITY_PROTECTED_VM
     * @see #CAPABILITY_NON_PROTECTED_VM
     * @hide
     */
    @Capability
    public int getCapabilities() {
        throw new RuntimeException("STUB");
    }

    /**
     * Creates a new {@link VirtualMachine} with the given name and config. Creating a virtual
     * machine with the same name as an existing virtual machine is an error. The existing virtual
     * machine has to be deleted before its name can be reused.
     *
     * <p>Each successful call to this method creates a new (and different) virtual machine even if
     * the name and the config are the same as a deleted one. The new virtual machine will initially
     * be stopped.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @throws VirtualMachineException if the VM cannot be created, or there is an existing VM with
     *     the given name.
     * @hide
     */
    @NonNull
    @WorkerThread
    public VirtualMachine create(@NonNull String name, @NonNull VirtualMachineConfig config)
            throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns an existing {@link VirtualMachine} with the given name. Returns null if there is no
     * such virtual machine.
     *
     * <p>There is at most one {@code VirtualMachine} object corresponding to a given virtual
     * machine instance. Multiple calls to get() passing the same name will get the same object
     * returned, until the virtual machine is deleted (via {@link #delete}) and then recreated.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @see #getOrCreate
     * @throws VirtualMachineException if the virtual machine exists but could not be successfully
     *     retrieved.
     * @hide
     */
    @WorkerThread
    @Nullable
    public VirtualMachine get(@NonNull String name) throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /**
     * Imports a virtual machine from an {@link VirtualMachineDescriptor} object and associates it
     * with the given name.
     *
     * <p>The new virtual machine will be in the same state as the descriptor indicates. The
     * descriptor is automatically closed and cannot be used again.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @throws VirtualMachineException if the VM cannot be imported or the {@code
     *     VirtualMachineDescriptor} has already been closed.
     * @hide
     */
    @NonNull
    @WorkerThread
    public VirtualMachine importFromDescriptor(
            @NonNull String name, @NonNull VirtualMachineDescriptor vmDescriptor)
            throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /**
     * Returns an existing {@link VirtualMachine} if it exists, or create a new one. The config
     * parameter is used only when a new virtual machine is created.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @throws VirtualMachineException if the virtual machine could not be created or retrieved.
     * @hide
     */
    @WorkerThread
    @NonNull
    public VirtualMachine getOrCreate(@NonNull String name, @NonNull VirtualMachineConfig config)
            throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }

    /**
     * Deletes an existing {@link VirtualMachine}. Deleting a virtual machine means deleting any
     * persisted data associated with it including the per-VM secret. This is an irreversible
     * action. A virtual machine once deleted can never be restored. A new virtual machine created
     * with the same name is different from an already deleted virtual machine even if it has the
     * same config.
     *
     * <p>NOTE: This method may block and should not be called on the main thread.
     *
     * @throws VirtualMachineException if the virtual machine does not exist, is not stopped, or
     *     cannot be deleted.
     * @hide
     */
    @WorkerThread
    public void delete(@NonNull String name) throws VirtualMachineException {
        throw new RuntimeException("STUB");
    }
}