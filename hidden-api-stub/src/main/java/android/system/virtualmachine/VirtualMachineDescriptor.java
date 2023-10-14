/*
 * Copyright 2022 The Android Open Source Project
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

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A VM descriptor that captures the state of a Virtual Machine.
 *
 * <p>You can capture the current state of VM by creating an instance of this class with {@link
 * VirtualMachine#toDescriptor}, optionally pass it to another App, and then build an identical VM
 * with the descriptor received.
 *
 * @hide
 */
public final class VirtualMachineDescriptor implements Parcelable, AutoCloseable {
    @NonNull private final ParcelFileDescriptor mConfigFd;
    @NonNull private final ParcelFileDescriptor mInstanceImgFd;

    // File descriptor of the image backing the encrypted storage - Will be null if encrypted
    // storage is not enabled. */
    @Nullable private final ParcelFileDescriptor mEncryptedStoreFd;

    @Override
    public int describeContents() {
        return CONTENTS_FILE_DESCRIPTOR;
    }

    @Override
    public void writeToParcel(@NonNull Parcel out, int flags) {
        throw new RuntimeException("STUB");
    }

    @NonNull
    public static final Parcelable.Creator<VirtualMachineDescriptor> CREATOR =
            new Parcelable.Creator<VirtualMachineDescriptor>() {
                public VirtualMachineDescriptor createFromParcel(Parcel in) {
                    throw new RuntimeException("STUB");
                }
                public VirtualMachineDescriptor[] newArray(int size) {
                    throw new RuntimeException("STUB");
                }
            };

    /**
     * @return File descriptor of the VM configuration file config.xml.
     */
    @NonNull
    ParcelFileDescriptor getConfigFd() {
        throw new RuntimeException("STUB");
    }

    /**
     * @return File descriptor of the instance.img of the VM.
     */
    @NonNull
    ParcelFileDescriptor getInstanceImgFd() {
        throw new RuntimeException("STUB");
    }

    /**
     * @return File descriptor of image backing the encrypted storage.
     *     <p>This method will return null if encrypted storage is not enabled.
     */
    @Nullable
    ParcelFileDescriptor getEncryptedStoreFd() {
        throw new RuntimeException("STUB");
    }

    VirtualMachineDescriptor(
            @NonNull ParcelFileDescriptor configFd,
            @NonNull ParcelFileDescriptor instanceImgFd,
            @Nullable ParcelFileDescriptor encryptedStoreFd) {
        throw new RuntimeException("STUB");
    }

    /**
     * Release any resources held by this descriptor. Calling {@code close} on an already-closed
     * descriptor has no effect.
     */
    @Override
    public void close() {
        throw new RuntimeException("STUB");
    }
}