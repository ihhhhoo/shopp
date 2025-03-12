import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.*;

public class Main {

    ;
    static StreamTokenizer st = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));
    public static int nextInt() throws IOException {
        st.nextToken();
        return (int)st.nval;
    }


    public static void main(String[] args)  throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        // 读取输入
        String[] sttt = br.readLine().split(" ");
        int n = Integer.parseInt(sttt[0]);
        int m = Integer.parseInt(sttt[1]);

        int[] a = new int[1000];
        int[][] f = new int[1000][1000];
        boolean[][] have = new boolean[1000][1000];
        int[] s = new int[1000];

        // 初始化a数组
        for (int i = 0; i < n; i++) {
            a[i] = 1;
        }

        // 使用Stream API将字符串数组转换为整数数组

        // 输入处理
        for (int ttxyc = 0; ttxyc < m; ttxyc++) {
            String[] temp = br.readLine().split(" ");
            int[] intArray = Arrays.stream(temp)
                    .mapToInt(Integer::parseInt)
                    .toArray();
            s[ttxyc] = intArray[0];
            for (int i = 0; i < s[ttxyc]; i++) {
                f[ttxyc][i] = intArray[i + 1] - 1;
                have[ttxyc][f[ttxyc][i]] = true;
            }
        }

        // 主逻辑
        boolean changed;
        do {
            changed = false;
            for (int ttxyc = 0; ttxyc < m; ttxyc++) {
                int maxn = 0;
                for (int i = f[ttxyc][0]; i <= f[ttxyc][s[ttxyc] - 1]; i++) {
                    if (!have[ttxyc][i]) {
                        maxn = Math.max(maxn, a[i]);
                    }
                }
                maxn++; // 停靠的比不停靠的至少多一
                for (int i = 0; i < s[ttxyc]; i++) {
                    if (a[f[ttxyc][i]] < maxn) {
                        a[f[ttxyc][i]] = maxn;
                        changed = true;
                    }
                }
            }
        } while (changed);

        // 输出结果
        int maxn = 0;
        for (int i = 0; i < n; i++) {
            maxn = Math.max(maxn, a[i]);
        }
        System.out.println(maxn);
    }
}
