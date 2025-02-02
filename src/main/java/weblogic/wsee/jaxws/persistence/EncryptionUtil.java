package weblogic.wsee.jaxws.persistence;

import java.security.AccessController;
import weblogic.kernel.Kernel;
import weblogic.security.acl.internal.AuthenticatedSubject;
import weblogic.security.internal.SerializedSystemIni;
import weblogic.security.internal.encryption.EncryptionService;
import weblogic.security.service.PrivilegedActions;
import weblogic.security.service.SecurityServiceManager;

public final class EncryptionUtil {
    private static final AuthenticatedSubject kernelID = (AuthenticatedSubject)AccessController.doPrivileged(PrivilegedActions.getKernelIdentityAction());
    private static EncryptionService es = null;

    public EncryptionUtil() {
    }

    public static byte[] encrypt(byte[] var0) {
        return Kernel.isServer() ? getEncryptionService().encryptBytes(var0) : var0;
    }

    public static byte[] decrypt(byte[] var0) {
        if (Kernel.isServer()) {
            SecurityServiceManager.checkKernelIdentity(kernelID);
            return getEncryptionService().decryptBytes(var0);
        } else {
            return var0;
        }
    }

//    private static final EncryptionService getEncryptionService() {
    public static final EncryptionService getEncryptionService() {
        if (es == null) {
            es = SerializedSystemIni.getExistingEncryptionService();
        }

        return es;
    }
}
