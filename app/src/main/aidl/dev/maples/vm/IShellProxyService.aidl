// IShellProxyService.aidl
package dev.maples.vm;

interface IShellProxyService {
    void destroy() = 16777114;

    void grantPermission(String permission) = 1;
}