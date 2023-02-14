package com.example.toomanybarbershopgui;

import javafx.animation.Animation;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Controller {

    @FXML
    public TextField servicesTF;
    @FXML
    private TextField waitingChairsTF;
    @FXML
    private TextField barbersTF;
    @FXML
    private TextField workingChairsTF;
    @FXML
    private Button saveBtn;
    @FXML
    private Button stpBtn;
    @FXML
    private Button runBtn;
    @FXML
    private Slider barberSpeedSlider;
    @FXML
    private Slider customerSpeedSlider;

    public Controller() {
    }

    @FXML
    private void initialize() {
        runBtn.setDisable(true);
        stpBtn.setDisable(true);
        barberSpeedSlider.setDisable(true);
        customerSpeedSlider.setDisable(true);

    }

    @FXML
    private void onSaveButtonAction() {

        MyApplication.animationPane.getChildren().clear();

        if (Integer.parseInt(waitingChairsTF.getText()) > 20 || Integer.parseInt(waitingChairsTF.getText()) <= 0)
            waitingChairsTF.setText("7");
        if (Integer.parseInt(barbersTF.getText()) < Integer.parseInt(servicesTF.getText()))
            barbersTF.setText(String.valueOf(Integer.parseInt(servicesTF.getText()) + 1));
        if (Integer.parseInt(servicesTF.getText()) > 14 || Integer.parseInt(servicesTF.getText()) <= 0)
            servicesTF.setText("14");
        if (Integer.parseInt(barbersTF.getText()) > 25) barbersTF.setText("25");
        if (Integer.parseInt(workingChairsTF.getText()) >= Integer.parseInt(barbersTF.getText()) || Integer.parseInt(barbersTF.getText()) <= 0 || Integer.parseInt(barbersTF.getText()) > 20)
            barbersTF.setText(String.valueOf(Integer.parseInt(workingChairsTF.getText()) + 1));

        MyApplication.config = new Configuration(Integer.parseInt(waitingChairsTF.getText()), Integer.parseInt(workingChairsTF.getText()), Integer.parseInt(servicesTF.getText()), Integer.parseInt(barbersTF.getText()));
        MyApplication.config.prepareAnimation();
        saveBtn.setDisable(true);
        runBtn.setDisable(false);
        barberSpeedSlider.setDisable(false);
        customerSpeedSlider.setDisable(false);

    }

    @FXML
    private void onRunButtonAction() {
        if (MyApplication.config != null) {
            MyApplication.animationStatus = Animation.Status.RUNNING;
            MyApplication.config.startThreads();
            runBtn.setDisable(true);
            stpBtn.setDisable(false);
        }
    }

    @FXML
    private void onStopButtonAction() {
        if (MyApplication.animationStatus == Animation.Status.RUNNING) {
            MyApplication.animationStatus = Animation.Status.STOPPED;
            MyApplication.config.interruptThreads();
            MyApplication.config.stopAnimation();
            MyApplication.animationPane.getChildren().clear();
            saveBtn.setDisable(false);
            runBtn.setDisable(true);
            stpBtn.setDisable(true);
            barberSpeedSlider.setValue(0);
            customerSpeedSlider.setValue(0);
        }
    }

    @FXML
    private void onCustomerSpeedSliderChanged() {
        MyApplication.config.customerAnimationSpeedChange(customerSpeedSlider.getValue());
    }

    @FXML
    private void onBarberSpeedSliderChanged() {
        MyApplication.config.barberAnimationSpeedChange(barberSpeedSlider.getValue());
    }


    public void MSG(String title, String header, String desc) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(desc);

        alert.showAndWait();
    }
}


