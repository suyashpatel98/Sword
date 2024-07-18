package sword.dev;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        IntVectorV2 vector;
        int[] values;
        BufferAllocator allocator = new RootAllocator();
        vector = new IntVectorV2("test", allocator);
        vector.allocateNew();
        values = new int[100];
        for (int i = 0; i < values.length; i++) {
            values[i] = i;
        }
        vector.setSimd(0, values);
        for (int i = 0; i < values.length; i++) {
            System.out.println(vector.get(i));
        }
    }
}
