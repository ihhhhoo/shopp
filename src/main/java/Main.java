import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        HashMap<String, ArrayList> map = new HashMap<>();
        // List<String>[] list = new ArrayList[n];
        HashMap<String, Integer> inMap = new HashMap<>();//入度
        String[] arr = new String[n];
        for (int i = 0; i < n; i++) {
            String str = sc.next();
            // list[]
            map.put(str, new ArrayList());
            arr[i] = str;
        }

        Set<Map.Entry<String, ArrayList>> entries = map.entrySet();
        for (Map.Entry<String, ArrayList> entry : entries) {

        }

    }
}
