package view;

import model.Captcha;

import java.util.Random;

public class CaptchaMenu {
    public boolean run() {
        while (true) {
            Captcha captcha = new Captcha();
           /* BufferedImage image = captcha.getCaptcha();
            for (int y = 0; y < 15; y++) {
                StringBuilder sb = new StringBuilder();
                for (int x = 0; x < captcha.getWidth(); x++) {

                    sb.append(image.getRGB(x, y) == -16777216 ? "." : getRandomSymbol());

                }
                if (sb.toString().trim().isEmpty()) {
                    continue;
                }
                System.out.println(sb);
            }*/
            ;
            String[] number = captcha.getNumber().split(" ");
            String finalNum = "";
            for (String s : number) {
                finalNum += s;
            }
            System.out.println("please enter the number :");
            String command = ScanMatch.getScanner().nextLine();
            if (command.matches("back")) return false;
            if (command.matches(finalNum)) {
                System.out.println("correct");
                return true;
            } else System.out.println("input number is invalid!");
        }
    }

    private String getRandomSymbol() {
        String symbols = "$*#+@";
        Random random = new Random();
        int low = 0;
        int high = 5;
        int size = random.nextInt(high - low) + low;
        return symbols.charAt(size) + "";
    }
}
