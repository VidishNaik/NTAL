import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DNSServer {
    static Socket s;
    public static void main(String[] args) {
        connect();
        while(true)
            receive();
    }

    private static void receive() {
        try {
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String s = dis.readUTF();
            if(s.equals("0"))
                System.exit(1);
            System.out.println("Received request for site: " + s);
            findDNS(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void findDNS(String s) {
        try {
            Map<String,String> site = new HashMap<>();
            String ip;
            File file = new File("C:\\xampp\\htdocs\\DNS.txt");
            if(!file.exists())
                file.createNewFile();
            Scanner sc = new Scanner(file);
            while (sc.hasNext())
            {
                String a[] = sc.next().split(",");
                site.put(a[0],a[1]);
            }
            if(site.containsKey(s))
            {
                ip = site.get(s);
                System.out.println("Sending "+site.get(s)+" to client");
            }
            else {
                InetAddress inetAddress = InetAddress.getByName(s);
                System.out.println("Sending "+inetAddress.getHostAddress()+" to client");
                ip = inetAddress.getHostAddress();
                if(!site.containsValue(inetAddress.getHostAddress())) {
                    FileWriter fw = new FileWriter(file, true);
                    fw.append(s + "," + inetAddress.getHostAddress() + System.getProperty("line.separator"));
                    fw.close();
                }
            }
            sc.close();
            send(ip);
        } catch (Exception e) {
        }
    }
    private static void send(String ip) {
        try {
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeUTF(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void connect() {
        try {
            ServerSocket ss = new ServerSocket(6666);
            s = ss.accept();
            System.out.println("Connected to client successfully");
        } catch (Exception e) {
        }
    }
}
