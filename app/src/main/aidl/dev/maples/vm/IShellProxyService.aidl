// IShellProxyService.aidl
package dev.maples.vm;

import android.system.virtualizationservice.IVirtualizationService;

interface IShellProxyService {
    void destroy() = 16777114;

    void grantPermission(String permission) = 1;
}