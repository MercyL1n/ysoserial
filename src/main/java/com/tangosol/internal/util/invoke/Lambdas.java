//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.tangosol.internal.util.invoke;

import com.oracle.common.internal.util.CanonicalNames;
import com.tangosol.coherence.config.Config;
import com.tangosol.internal.util.invoke.lambda.AbstractRemotableLambda;
import com.tangosol.internal.util.invoke.lambda.AnonymousLambdaIdentity;
import com.tangosol.internal.util.invoke.lambda.LambdaIdentity;
import com.tangosol.internal.util.invoke.lambda.MethodReferenceIdentity;
import com.tangosol.internal.util.invoke.lambda.RemotableLambdaGenerator;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public abstract class Lambdas {
    private static final String DUMP_LAMBDAS = Config.getProperty("coherence.remotable.dumpLambdas", Config.getProperty("coherence.remotable.dumpAll"));

    public Lambdas() {
    }

    public static boolean isLambda(Object o) {
        return o != null && isLambdaClass(o.getClass());
    }

    public static boolean isLambdaClass(Class clz) {
        return clz != null && clz.getName().contains("$$Lambda$");
    }

    public static SerializedLambda getSerializedLambda(Object oLambda) {
        if (oLambda != null && isLambda(oLambda) && oLambda instanceof Serializable) {
            try {
                Class<?> clzLambda = oLambda.getClass();
                Method method = clzLambda.getDeclaredMethod("writeReplace");
                method.setAccessible(true);
                return (SerializedLambda)method.invoke(oLambda);
            } catch (Exception var3) {
                throw new IllegalStateException("Unable to extract SerializedLambda from lambda: " + oLambda, var3);
            }
        } else {
            throw new IllegalArgumentException("Specified object is not an instance of a serializable lambda");
        }
    }

    public static boolean isLambdaMethod(String sMethodName) {
        return sMethodName.startsWith("lambda$");
    }

    public static boolean isMethodReference(SerializedLambda lambda) {
        return lambda.getImplMethodKind() == 8 || !isLambdaMethod(lambda.getImplMethodName()) && lambda.getCapturedArgCount() == 0;
    }

    public static ClassDefinition createDefinition(ClassIdentity id, Serializable lambda, ClassLoader loader) {
        SerializedLambda lambdaMetadata = getSerializedLambda(lambda);
        if (lambdaMetadata.getImplMethodKind() != 6 && !isMethodReference(lambdaMetadata)) {
            throw new IllegalArgumentException("The specified lambda is referring to the enclosing class instance or its fields and therefore cannot be marshalled across network boundaries (" + lambdaMetadata + ")");
        } else {
            ClassDefinition definition = new ClassDefinition(id, RemotableLambdaGenerator.createRemoteLambdaClass(id.getName(), lambdaMetadata, loader));
            definition.dumpClass(DUMP_LAMBDAS);
            return definition;
        }
    }

    public static LambdaIdentity createIdentity(SerializedLambda lambdaMetadata, ClassLoader loader) {
        return (LambdaIdentity)(isMethodReference(lambdaMetadata) ? new MethodReferenceIdentity(lambdaMetadata, loader) : new AnonymousLambdaIdentity(lambdaMetadata, loader));
    }

    public static Object[] getCapturedArguments(SerializedLambda lambdaMetadata) {
        int c = lambdaMetadata.getCapturedArgCount();
        Object[] aoArgs = new Object[c];

        for(int i = 0; i < c; ++i) {
            aoArgs[i] = lambdaMetadata.getCapturedArg(i);
        }

        return aoArgs;
    }

    public static <T extends Serializable> T ensureRemotable(T function) {
        if (!(function instanceof AbstractRemotableLambda) && isLambda(function)) {
            RemotableSupport support = RemotableSupport.get(function.getClass().getClassLoader());
            return (T) support.realize(support.createRemoteConstructor(function));
        } else {
            return function;
        }
    }

    public static <T extends Remotable> String getValueExtractorCanonicalName(Object oLambda) {
        if (oLambda instanceof AbstractRemotableLambda) {
            AbstractRemotableLambda lambda = (AbstractRemotableLambda)oLambda;
            return CanonicalNames.computeValueExtractorCanonicalName(((MethodReferenceIdentity)lambda.getId()).getImplMethod() + "()", (Object[])null);
        } else {
            return null;
        }
    }
}
