package kmeans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class KMeans {

    int k;
    int maxIterations = 15;

    public KMeans(int k) {
        this.k = k;
    }

    public Pixel[] go(Pixel[] pixels) {
        Pixel[] center = new Pixel[k];

        Random rand = new Random();

        int l = pixels.length;

        for (int i = 0; i < k; i++) {
            Pixel p = pixels[rand.nextInt(l)];
            center[i] =  new Pixel(p.r, p.g, p.b);
        }

        int[] idx = new int[l];

        for (int i = 0; i<maxIterations;i++){

            for (int j=0;j<l;j++){
                idx[j] = nearestCenter(pixels[j], center);
            }

            int[] sumR = new int[k];
            int[] sumG = new int[k];
            int[] sumB = new int[k];
            int[] count  = new int[k];

            for (int j =0;j<l;j++){
                int p = idx[j];
                sumR[p]+=pixels[j].r;
                sumG[p]+=pixels[j].g;
                sumB[p]+=pixels[j].b;
                count[p]++;
            }


            for (int j=0;j<k;j++){
                if(count[j]==0){continue;}

                center[j] = new Pixel(sumR[j]/count[j],sumG[j]/count[j],sumB[j]/count[j]);
            }
        }





        return center;
    }


    int nearestCenter(Pixel p, Pixel[] center) {
        double minDist = Double.MAX_VALUE;
        int idx = 0;

        for (int i=0;i<center.length;i++){

            int distR = p.r - center[i].r;
            int distG = p.g - center[i].g;
            int distB = p.b - center[i].b;


            double d = Math.sqrt(distR*distR + distG*distG + distB*distB);

            if (d < minDist) {
                minDist = d;
                idx = i;
            }
        }

        return idx;
    }



}
