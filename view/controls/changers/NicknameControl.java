package view.controls.changers;

import controller.RegisterMenuController;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import view.controls.MainMenuControl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class NicknameControl {
    public TextField nickname;
    public static Pane pane;
    private static int currentCaptcha;
    private static String currentPath;
    public TextField userCaptchaAnswer;
    private static RegisterMenuController registerMenuController;

    public static void enterCaptcha() throws IOException {
        int picNum = countPictures();
        int randomPick = (int) (picNum * Math.random());
        currentCaptcha = randomPick;
        getAPic();
    }


    public static int countPictures() {
        File directory = new File(System.getProperty("user.dir") + "/src/main/resources/Images/captcha/");
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jpg") ||
                        name.toLowerCase().endsWith(".png") ||
                        name.toLowerCase().endsWith(".jpeg");
            }
        });
        return files.length;
    }

    private static void getAPic() throws IOException {
        File directory = new File(System.getProperty("user.dir") + "/src/main/resources/Images/captcha/");
        File[] files = directory.listFiles();
        Arrays.sort(files);
        File fourthFile = files[currentCaptcha];
        String pathToFile = fourthFile.getAbsolutePath();
        currentPath = pathToFile;
        InputStream inputStream = Files.newInputStream(Path.of(pathToFile));
        ImageView captchaPhoto = new ImageView(new Image(inputStream, 140, 50, false, false));
        if (pane != null) {
            VBox vBox = new VBox();
            vBox.getChildren().add(captchaPhoto);
            pane.getChildren().add(vBox);
            pane.getChildren().get(pane.getChildren().size() - 1).setLayoutY(320);
            pane.getChildren().get(pane.getChildren().size() - 1).setLayoutX(688);
        }
    }
    public void changeCaptcha() throws IOException {
        int picNum = countPictures();
        int randomPick = (int) (picNum * Math.random());
        while(randomPick == currentCaptcha)
            randomPick = (int) (picNum * Math.random());
        currentCaptcha = randomPick;
        pane.getChildren().remove(pane.getChildren().size() - 1);
        getAPic();
    }
    public void confirmUsername(MouseEvent mouseEvent) throws IOException {
        int index = currentPath.lastIndexOf("\\") + 1;
        String photoName = currentPath.substring(index, currentPath.length() - 4);
        Alert alert;
        if(userCaptchaAnswer == null || userCaptchaAnswer.getText().length() == 0 ||
                !userCaptchaAnswer.getText().trim().equals(photoName)) {
            System.out.println(userCaptchaAnswer.getText().trim());
            System.out.println(photoName);
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Wrong Captcha");
            alert.setContentText("Captcha wasn't entered correctly.\nTry again!");
            alert.show();
            changeCaptcha();
        }
       else {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success");
            alert.setHeaderText("your Nickname changed successfully");
            alert.showAndWait();
            MainMenuControl.openMenu("/FXML/profile.fxml");
            MainMenuControl.openMenu("/FXML/profile.fxml");
        }
    }

    public void cancelUsername(MouseEvent mouseEvent) throws IOException {
        MainMenuControl.openMenu("/FXML/profile.fxml");
    }
}
