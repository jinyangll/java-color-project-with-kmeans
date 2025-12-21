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

    ColorLabel colorLabel;

    JSlider colorSlider;
    int colorCnt = 5;

    JCheckBox clear;


    public Home(){
        setTitle("ColorProject");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,600);

        Container c = getContentPane();
        c.setLayout(new BorderLayout());

        // 이미지 띄울 곳
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

        // 버튼 넣을 패널
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout());

        // color 추천 실행 버튼
        JButton goBtn = new JButton("Go");
        goBtn.addActionListener(new colorRecommandListener());

        // 파일 업로드 버튼
        JButton fileBtn = new JButton("File");
        fileBtn.addActionListener(new fileUploadListener());

        // 버튼 사이즈 조정
        goBtn.setPreferredSize(new Dimension(70, 30));
        fileBtn.setPreferredSize(new Dimension(70, 30));


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


    void titleImage(){
        try{
            InputStream is = getClass().getResourceAsStream("title.png");
            if (is == null) return;

            BufferedImage img = ImageIO.read(is);

            Image scaled = img.getScaledInstance(550, 550, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 팔레트 클래스
    class ColorPalette extends JPanel {

        Color[] color = new Color[colorCnt];

        public ColorPalette(int colorCnt) {
            for (int i=0;i<colorCnt;i++){
                color[i] = Color.LIGHT_GRAY;
            }

            int w = 1000 /  colorCnt;
            int h = 50;

            setPreferredSize(new Dimension(w,h));

            addMouseMotionListener(new MouseMotionAdapter(){
                public void mouseMoved(MouseEvent e){
                    updateToolTip(e.getX());
                }
            });

        }

        void updateToolTip(int x){
            int gap=4;
            int rectWidth = (getWidth() - gap * (colorCnt - 1)) / colorCnt;

            for (int i=0;i<colorCnt;i++){
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



        public void setColor(Color[] recommandColor){
            this.color = recommandColor;
            setToolTipText(null);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int gap = 4;
            int rectHeight = getHeight();
            int rectWidth = (getWidth() - gap * (colorCnt - 1)) / colorCnt;

            for (int i = 0; i < colorCnt; i++) {
                int x = i * (rectWidth + gap);
                g.setColor(color[i]);
                g.fillRect(x, 0, rectWidth, rectHeight);
            }
        }


    }




    class ColorLabel extends JPanel{
        private JLabel[] colorLabel = new JLabel[colorCnt];

        public ColorLabel(){
            setLayout(new FlowLayout());

            for(int i=0;i<colorCnt;i++){
                colorLabel[i] = new JLabel("#______", JLabel.CENTER);
                add(colorLabel[i]);
            }
        }
    }

    // 파일 업로드 버튼 클릭시 이벤트
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
                String filePath = file.getPath();

                uploadImage = ImageIO.read(file);

                // 업로드한 이미지
                ImageIcon icon = new ImageIcon(filePath);

                // 이미지 크기 비율에 맞게 조정
                int maxWidth = 400;
                int maxHeight = 350;

                Image img = icon.getImage();
                int originalWidth = icon.getIconWidth();
                int originalHeight = icon.getIconHeight();

                double ratio = Math.min((double)maxWidth/originalWidth,(double)maxHeight/originalHeight);

                int newWidth = (int)(originalWidth * ratio);
                int newHeight = (int)(originalHeight * ratio);

                Image newSizeImage = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
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



    // 색 추천 버튼 go 클릭 시 이벤트
    class colorRecommandListener implements ActionListener {

        int width;
        int height;
        Pixel[] pixels;

        public void actionPerformed(ActionEvent e) {

            boolean clearChecked = clear.isSelected();

            if (uploadImage == null){
                JOptionPane.showMessageDialog(null,
                        "Please select a image.",
                        "error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            width = uploadImage.getWidth();
            height = uploadImage.getHeight();

            pixels = new Pixel[width*height];

            setImageDataSet();

            KMeans kmeans = new KMeans(colorCnt);
            Pixel[] center = kmeans.go(pixels);


            // 후처리 (Pixel을 일단 Color로 변환
            Color[] colors = new Color[colorCnt];
            for (int i = 0; i < colorCnt; i++) {
                Pixel p = center[i];
                colors[i] = new Color(p.r, p.g, p.b);
            }

            // 밝기 clamp
            Color[] clamped = new Color[colorCnt];

            Color[] cleared = new Color[colorCnt];

            for (int i = 0; i < colorCnt; i++) {
                clamped[i] = clamp(colors[i], 0.67f, 0.78f);
                cleared[i] = clamped[i];
            }

            // Hue 정렬
            Arrays.sort(clamped, Comparator.comparingDouble(c ->
                    Color.RGBtoHSB(
                            c.getRed(),
                            c.getGreen(),
                            c.getBlue(),
                            null
                    )[0]
            ));


            for(int i=0;i<colorCnt;i++){
                float[] hsb = Color.RGBtoHSB(
                        clamped[i].getRed(),clamped[i].getGreen(),clamped[i].getBlue(),null
                );

                float h = hsb[0];
                float s = Math.min(1.0f, hsb[1]*1.25f);
                float b = Math.min(1.0f, hsb[2]*1.15f);

                cleared[i] = Color.getHSBColor(h, s, b);

            }



            // 일괄 처리
//            for (int i = 0; i < colorCnt; i++) {
//                clamped[i] = brighten(clamped[i], 0.2f);
//            }

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



        public void setImageDataSet(){

            int cnt = 0;

            for (int y = 0;y<height;y++){
                for(int x = 0;x<width;x++){

                    Color c = new Color(uploadImage.getRGB(x,y));
                    Pixel p = new Pixel(c.getRed(),c.getGreen(),c.getBlue());
                    pixels[cnt] = p;
                    cnt++;
                }
            }
        }




    }


    class colorSliderListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            colorCnt = colorSlider.getValue();
        }
    }


}
