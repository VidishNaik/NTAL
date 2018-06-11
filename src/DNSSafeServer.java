import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.*;

public class DNSSafeServer {
    static Socket s;
    static Cipher c;
    static Key key;

    public static void main(String[] args) {
        try {
            c = Cipher.getInstance("AES");
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            key = keyGen.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connect();
        while (true)
            receive();
    }

    private static void receive() {
        try {
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String s = dis.readUTF();
            if (s.equals("0"))
                System.exit(1);
            System.out.println("Received request for site: " + s);
            findDNS(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void findDNS(String s) {
        String ip = "";
        try {
            Map<String, String> site = new HashMap<>();
            File file = new File("C:\\xampp\\htdocs\\DNS.txt");
            if (!file.exists())
                file.createNewFile();
            Scanner sc = new Scanner(file);
            while (sc.hasNext()) {
                String a[] = sc.next().split(",");
                site.put(decrypt(a[0]), decrypt(a[1]));
            }
            if (site.containsKey(s)) {
                ip = site.get(s);
                if (ip == null) {
                    InetAddress inetAddress = InetAddress.getByName(s);
                    System.out.println("Sending " + inetAddress.getHostAddress() + " to client");
                    ip = inetAddress.getHostAddress();
                    file.delete();
                    file.createNewFile();
                    if (!site.containsValue(encrypt(ip))) {
                        FileWriter fw = new FileWriter(file);
                        fw.write(encrypt(s) + "," + encrypt(ip) + System.getProperty("line.separator"));
                        fw.close();
                    }
                } else {
                    String[] a = ip.split("\\.");
                    if (a.length != 4) {
                        InetAddress inetAddress = InetAddress.getByName(s);
                        System.out.println("Sending " + inetAddress.getHostAddress() + " to client");
                        ip = inetAddress.getHostAddress();
                        file.delete();
                        file.createNewFile();
                        if (!site.containsValue(encrypt(ip))) {
                            FileWriter fw = new FileWriter(file);
                            fw.write(encrypt(s) + "," + encrypt(ip) + System.getProperty("line.separator"));
                            fw.close();
                        }
                    } else {
                        if (!isIP(a)) {
                            System.out.println("BBBBB");
                            InetAddress inetAddress = InetAddress.getByName(s);
                            System.out.println("Sending " + inetAddress.getHostAddress() + " to client");
                            ip = inetAddress.getHostAddress();
                            file.delete();
                            file.createNewFile();
                            if (!site.containsValue(encrypt(ip))) {
                                FileWriter fw = new FileWriter(file);
                                fw.write(encrypt(s) + "," + encrypt(ip) + System.getProperty("line.separator"));
                                fw.close();
                            }
                        }
                    }
                    System.out.println("Sending " + site.get(s) + " to client");
                }
            } else {
                InetAddress inetAddress = InetAddress.getByName(s);
                System.out.println("Sending " + inetAddress.getHostAddress() + " to client");
                ip = inetAddress.getHostAddress();
                if (!site.containsValue(encrypt(ip))) {
                    FileWriter fw = new FileWriter(file, true);
                    fw.append(encrypt(s) + "," + encrypt(ip) + System.getProperty("line.separator"));
                    fw.close();
                }
            }
            sc.close();
        } catch (Exception ignored) {
        }
        send(ip);
    }

    private static boolean isIP(String[] a) {
        for (String anA : a) {
            try {
                Integer.parseInt(anA);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
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

    private static String encrypt(String s) {
        try {
            c.init(Cipher.ENCRYPT_MODE, key);
            return new BASE64Encoder().encode(c.doFinal(s.getBytes("UTF8")));
        } catch (Exception e) {
        }
        return null;
    }

    private static String decrypt(String s) {
        try {
            c.init(Cipher.DECRYPT_MODE, key, c.getParameters());
            return new String(c.doFinal(new BASE64Decoder().decodeBuffer(s)));
        } catch (Exception e) {
        }
        return null;
    }
}