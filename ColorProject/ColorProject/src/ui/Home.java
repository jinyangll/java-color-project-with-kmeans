package ui;

import kmeans.KMeans;
import kmeans.Pixel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import java.util.*;


public class Home extends JFrame {

    JLabel imageLabel;

    BufferedImage uploadImage;

    ColorPalette palette;

    JSlider colorSlider;
    int colorCnt = 5;

    JCheckBox clear;


    public Home(){
        setTitle("ColorProject");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,570);

        Container c = getContentPane();
        c.setLayout(new BorderLayout());

        JMenuBar menu = new JMenuBar();

        JMenu filemenu = new JMenu("File");
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(new saveFileListener());

        filemenu.add(save);
        menu.add(filemenu);

        setJMenuBar(menu);

        // image
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        titleImage();

        // slider, check box
        JPanel slidePanel = new JPanel(new FlowLayout());

        colorSlider = new JSlider(JSlider.HORIZONTAL,1,7,5);
        colorSlider.setPaintLabels(true);
        colorSlider.setMajorTickSpacing(1);
        colorSlider.addChangeListener(new colorSliderListener());

        slidePanel.add(colorSlider);

        clear = new JCheckBox("clearer");
        slidePanel.add(clear);


        clear.setBorder(
                BorderFactory.createEmptyBorder(0, 35, 0, 0)
        );

        // color palette
        palette= new ColorPalette(colorCnt);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        // btn
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout());

        // go btn
        JButton goBtn = new JButton("Go");
        goBtn.addActionListener(new colorRecommandListener());

        // file btn
        JButton fileBtn = new JButton("Upload");
        fileBtn.addActionListener(new fileUploadListener());


        goBtn.setPreferredSize(new Dimension(70, 30));
        fileBtn.setPreferredSize(new Dimension(80, 30));

        btnPanel.add(fileBtn);
        btnPanel.add(goBtn);

        btnPanel.setBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );

        bottomPanel.add(slidePanel,BorderLayout.NORTH);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);
        bottomPanel.add(palette, BorderLayout.CENTER);

        bottomPanel.setBorder(
                BorderFactory.createEmptyBorder(0, 25, 15, 25)
        );

        imageLabel.setBorder(
                BorderFactory.createEmptyBorder(20, 0, 10, 0)
        );


        c.add(imageLabel,BorderLayout.CENTER);
        c.add(bottomPanel,BorderLayout.SOUTH);

        setVisible(true);
    }

    // set title image
    void titleImage(){
        try{
            InputStream titleImage = getClass().getResourceAsStream("title.png");

            if (titleImage == null) return;

            BufferedImage img = ImageIO.read(titleImage);

            Image scaled = img.getScaledInstance(600, 600, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // palette class
    class ColorPalette extends JPanel {

        Color[] color = new Color[colorCnt];

        public ColorPalette(int colorCnt) {
            for (int i=0;i<colorCnt;i++){
                color[i] = Color.LIGHT_GRAY;
            }

            int w = 1000 /  colorCnt;
            int h = 50;

            setPreferredSize(new Dimension(w,h));
            ToolTipManager m = ToolTipManager.sharedInstance();
            m.setInitialDelay(0);

            addMouseMotionListener(new MouseMotionAdapter(){
                public void mouseMoved(MouseEvent e){
                    updateToolTip(e.getX());
                }
            });

        }


        void updateToolTip(int x){
            int gap=4;
            int cnt = color.length;
            int rectWidth = (getWidth() - gap * (cnt - 1)) / cnt;


            for (int i=0;i<cnt;i++){
                int startx = i * (rectWidth + gap);
                int endx = startx + rectWidth;

                if(x >= startx && x <= endx){
                    setToolTipText(toHex(color[i]));
                    return;
                }
            }
            setToolTipText(null);
        }


        String toHex(Color c){
            return String.format("#%02X%02X%02X",
                    c.getRed(), c.getGreen(), c.getBlue());
        }



        public void setColor(Color[] color){
            this.color = color;
            setToolTipText(null);
            repaint();
        }


        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int cnt = color.length;
            int gap = 4;
            int rectHeight = getHeight();
            int rectWidth = (getWidth() - gap * (cnt - 1)) / cnt;

            for (int i = 0; i < cnt; i++) {
                int x = i * (rectWidth + gap);
                g.setColor(color[i]);
                g.fillRect(x, 0, rectWidth, rectHeight);
            }
        }


    }


    // upload btn event
    class fileUploadListener implements ActionListener {

        private JFileChooser chooser;

        public fileUploadListener(){
            chooser = new  JFileChooser();
        }
        public void actionPerformed(ActionEvent e) {
            FileNameExtensionFilter filter =
                    new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg");
            chooser.setFileFilter(filter);

            int ret = chooser.showOpenDialog(null);
            if (ret != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null,
                        "Please select a image.","error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }


            try {
                File file = chooser.getSelectedFile();
                uploadImage = ImageIO.read(file);


                int maxWidth = 400;
                int maxHeight = 350;

                int originalWidth = uploadImage.getWidth();
                int originalHeight = uploadImage.getHeight();

                double ratio = Math.min((double)maxWidth/originalWidth,(double)maxHeight/originalHeight);

                int newWidth = (int)(originalWidth * ratio);
                int newHeight = (int)(originalHeight * ratio);

                Image newSizeImage = uploadImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                ImageIcon newSizeIcon = new ImageIcon(newSizeImage);

                imageLabel.setIcon(newSizeIcon);

            }
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null,
                        "Failed to upload.",
                        "error",
                        JOptionPane.WARNING_MESSAGE);
            }


        }
    }


    // go btn event
    class colorRecommandListener implements ActionListener {

        Pixel[] pixels;

        public void actionPerformed(ActionEvent e) {

            if (uploadImage == null){
                JOptionPane.showMessageDialog(null,
                        "Please select an image.",
                        "error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int width = uploadImage.getWidth();
            int height = uploadImage.getHeight();

            pixels = new Pixel[width*height];

            int cnt = 0;

            for (int y = 0;y<height;y++){
                for(int x = 0;x<width;x++){

                    Color c = new Color(uploadImage.getRGB(x,y));
                    Pixel p = new Pixel(c.getRed(),c.getGreen(),c.getBlue());
                    pixels[cnt] = p;
                    cnt++;
                }
            }

            KMeans kmeans = new KMeans(colorCnt);
            Pixel[] center = kmeans.go(pixels);


            // pixel - color
            Color[] colors = new Color[colorCnt];
            for (int i = 0; i < colorCnt; i++) {
                Pixel p = center[i];
                colors[i] = new Color(p.r, p.g, p.b);
            }

            // clamp
            Color[] clamped = new Color[colorCnt];
            Color[] cleared = new Color[colorCnt];

            for (int i = 0; i < colorCnt; i++) {
                clamped[i] = clamp(colors[i], 0.67f, 0.78f);
                cleared[i] = clamped[i];
            }

            // Hue sort
            Arrays.sort(clamped, Comparator.comparingDouble(c ->
                    Color.RGBtoHSB(
                            c.getRed(),
                            c.getGreen(),
                            c.getBlue(),
                            null
                    )[0]
            ));


            // clear
            for(int i=0;i<colorCnt;i++){
                float[] hsb = Color.RGBtoHSB(
                        clamped[i].getRed(),clamped[i].getGreen(),clamped[i].getBlue(),null
                );

                float h = hsb[0];
                float s = Math.min(1.0f, hsb[1]*1.25f);
                float b = Math.min(1.0f, hsb[2]*1.15f);



                cleared[i] = Color.getHSBColor(h, s, b);

            }


            boolean clearChecked = clear.isSelected();

            if (clearChecked){
                palette.setColor(cleared);
            }
            else {
                palette.setColor(clamped);
            }



        }


        public Color clamp(Color c, float min, float max) {
            float[] hsv = Color.RGBtoHSB(c.getRed(),
                    c.getGreen(), c.getBlue(), null);

            hsv[2] = Math.max(min, Math.min(max, hsv[2]));

            Color newColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
            return newColor;
        }




    }


    // slider event
    class colorSliderListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            colorCnt = colorSlider.getValue();
        }
    }


    class saveFileListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (uploadImage == null) {
                JOptionPane.showMessageDialog(null,
                        "Please select an image.",
                        "error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Color[] paletteColors = palette.color;

            // file chooser
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Palette Image");
            chooser.setSelectedFile(new File("palette.png"));

            int ret = chooser.showSaveDialog(null);
            if (ret != JFileChooser.APPROVE_OPTION) return;

            File saveFile = chooser.getSelectedFile();

            // image, palette save
            ImageExport.savePalette(uploadImage, paletteColors, saveFile.getAbsolutePath());
            JOptionPane.showMessageDialog(null,
                    "saved: " + saveFile.getAbsolutePath(),
                    "success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }




}