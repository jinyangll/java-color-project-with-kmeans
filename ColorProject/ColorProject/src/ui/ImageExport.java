package ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageExport {

    public static void savePalette(BufferedImage uploadImage, Color[] colors, String savePath) {
        try {
            int w = uploadImage.getWidth();
            int h = uploadImage.getHeight();

            int paletteHeight = 60;
            int hexHeight = 20;
            int totalHeight = h + paletteHeight + hexHeight;

            BufferedImage export = new BufferedImage(w, totalHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = export.createGraphics();

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, totalHeight);

            g.drawImage(uploadImage, 0, 0, null);


            int cnt = colors.length;
            int gap = 4;
            int rectWidth = (w - gap * (cnt - 1)) / cnt;
            int rectHeight = paletteHeight;

            for (int i = 0; i < cnt; i++) {
                int x = i * (rectWidth + gap);
                int y = h;
                g.setColor(colors[i]);
                g.fillRect(x, y, rectWidth, rectHeight);
            }


            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 14));

            for (int i = 0; i < cnt; i++) {
                int x = i * (rectWidth + gap);

                int y = h + paletteHeight + 15;
                String hex = String.format("#%02X%02X%02X", colors[i].getRed(), colors[i].getGreen(), colors[i].getBlue());
                g.drawString(hex, x, y);

            }


            ImageIO.write(export, "png", new File(savePath));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}