package model;

import java.util.Random;

public class Captcha {
    private int width;
    private String number;

    /*public BufferedImage getCaptcha() {
        String number = randomNumber();
        this.number = number;
        int width = number.length() * 10;
        int height = 15;
        this.width = width;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setFont(new Font("TimesRoman", Font.CENTER_BASELINE, 20));
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.drawString(number, 0, 15);
        return image;
    }

    private String randomNumber() {
        Random random = new Random();
        int low = 4;
        int high = 9;
        int size = random.nextInt(high - low) + low;
        String number = "";
        for (int i = 0; i < size; i++) {
            number += random.nextInt(10) + " ";
        }
        return number;
    }
*/

    public int getWidth() {
        return width;
    }

    public String getNumber() {
        return number;
    }
}