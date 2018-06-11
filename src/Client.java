import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import static java.awt.Desktop.getDesktop;

public class Client {
    static Socket s;
    private static String str;
    public static void main(String[] args) {
        connect();
        input();
    }

    private static void input() {
        System.out.println("Enter the website(Enter 0 to terminate):");
        Scanner sc = new Scanner(System.in);
        str = sc.next();
        send(str);
    }

    private static void send(String str) {
        try {
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeUTF(str);
            if(str.equals("0"))
                System.exit(1);
            receive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void receive() {
        try {
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String ip = dis.readUTF();
            System.out.println("Received IP : " + ip);
            TimeUnit.SECONDS.sleep(2);
            if (Desktop.isDesktopSupported()) {
                try {
                    if (str.contains("www."))
                        getDesktop().browse(new URI(str));
                    else if(ip.contains("http:"))
                        getDesktop().browse(new URI(ip));
                    else
                        getDesktop().browse(new URI("www." + str));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            input();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void connect() {
        try {
            s = new Socket("localhost",6666);
            System.out.println("Connected to DNS server successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}