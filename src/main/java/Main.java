import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = sc.nextInt();
        }
        Arrays.sort(arr);
        int index = 0;
        long res = 1;
        for (int i = 0; i < n; i++) {
            int num = arr[i] - index++;
            if(num <= 0){
                System.out.println(0);
                return;
            }
            res *= num;
            res %= 1e9+7;
        }
        System.out.println(res);

    }
}
