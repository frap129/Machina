package android.content;


import dev.rikka.tools.refine.RefineAs;

@RefineAs(Context.class)
public abstract class ContextHidden {
    /**
     * Use with [.getSystemService] to retrieve a
     * [android.system.virtualmachine.VirtualMachineManager].
     *
     *
     * On devices without [PackageManager.FEATURE_VIRTUALIZATION_FRAMEWORK] system feature
     * the [.getSystemService] will return `null`.
     *
     * @see .getSystemService
     * @see android.system.virtualmachine.VirtualMachineManager
     *
     * @hide
     */
    public static String VIRTUALIZATION_SERVICE = "virtualization";
}
