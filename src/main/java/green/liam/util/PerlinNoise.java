package green.liam.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PerlinNoise {

    public static double noise(float x, float y) {
        int[] p = init();
        int xi = (int) x & 255;
        int yi = (int) y & 255;
        double xf = x - (int) x;
        double yf = y - (int) y;
        double u = fade(xf);
        double v = fade(yf);
        int a = p[xi] + yi;
        int aa = p[a];
        int ab = p[a + 1];
        int b = p[xi + 1] + yi;
        int ba = p[b];
        int bb = p[b + 1];
        return lerp(v, lerp(u, grad(p[aa], xf, yf), grad(p[ba], xf - 1, yf)), lerp(u, grad(p[ab], xf, yf - 1),
                grad(p[bb], xf - 1, yf - 1)));
    }

    private static int[] init() {
        String randomSeed = System.currentTimeMillis() + "";
        int[] p = new int[512];
        List<Integer> permutation = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            permutation.add(i);
        }
        Collections.shuffle(permutation, new java.util.Random(randomSeed.hashCode()));
        for (int x = 0; x < 512; x++) {
            p[x] = permutation.get(x % 256);
        }
        return p;
    }

    private static double fade(double t) {
        return t * t * t * (t * (t * 6d - 15d) + 10d);
    }

    private static double lerp(double t, double a, double b) {
        return t * a + (1f - t) * b;
    }

    private static double grad(int hash, double x, double y) {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : h == 12 || h == 14 ? x : 0;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}
