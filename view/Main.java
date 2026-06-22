package view;
import model.User;
import view.MapMenu;
import view.RegisterMenu;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        RegisterMenu reg = new RegisterMenu();
        reg.run();
    }
    //try...
}