package com.example.toomanybarbershopgui;

import javafx.animation.Animation;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Configuration {

    public Pair<Integer, Integer>[] waitingChairCoordinates;
    public Pair<Integer, Integer>[] workingChairCoordinates;
    public Pair<Integer, Integer>[] sleepingChairCoordinates;

    List<Animation> bAnimations;
    List<Animation> cAnimations;

    double bAnimationRate;
    double cAnimationRate;
    int numberOfBarbers;
    int numberOfWaitingRoomChairs;
    int numberOfWorkingChairs;
    int numOfServices;
    public volatile LinkedBlockingDeque<Triple<Integer, Integer, Group>> queue;
    Barber[] barbers;
    Rectangle[] workingChairsRect;
    Rectangle[] waitingChairsRect;
    Rectangle[] sleepingChairsRect;

    int[][] workingChairStatus;
    int[] waitingChairStatus;
    Customer customer;
    public volatile Lock mutex;
    Semaphore workingChairs;

//    public Group group;
    public Configuration(int numberOfWaitingRoomChairs, int numberOfWorkingChairs, int numOfServices, int numberOfBarbers) {
        this.numberOfWaitingRoomChairs = numberOfWaitingRoomChairs;
        this.numberOfWorkingChairs = numberOfWorkingChairs;
        this.numOfServices = numOfServices;
        this.numberOfBarbers = numberOfBarbers;

        int screenHeight = (int) MyApplication.animationPane.getHeight();
        int screenWidth = (int) MyApplication.animationPane.getWidth();

        int free_space, xref;

        free_space = screenWidth - numberOfWaitingRoomChairs * (100 + 10) + 20;
        xref = free_space / 2;
        waitingChairCoordinates = new Pair[numberOfWaitingRoomChairs];
        for (int i = 0; i < waitingChairCoordinates.length; i++) {
            waitingChairCoordinates[i] = new Pair<>(xref + i * (100 + 10), screenHeight - (int) (screenHeight / 2 - 150));
        }

        free_space = screenWidth - numberOfWorkingChairs * (100 + 10) + 20;
        xref = free_space / 2;
        workingChairCoordinates = new Pair[numberOfWorkingChairs];
        for (int i = 0; i < workingChairCoordinates.length; i++) {
            workingChairCoordinates[i] = new Pair<>(xref + i * (100 + 10), screenHeight - (int) (screenHeight / 2 + 50));
        }

        free_space = screenWidth - numberOfBarbers * (100 + 10) + 20;
        xref = free_space / 2;
        sleepingChairCoordinates = new Pair[numberOfBarbers];
        for (int i = 0; i < sleepingChairCoordinates.length; i++) {
            sleepingChairCoordinates[i] = new Pair<>(xref + i * (100 + 10), screenHeight - (int) (screenHeight / 2 + 250));
        }
    }

    public void prepareAnimation() {
        bAnimations = new ArrayList<>();
        cAnimations = new ArrayList<>();
        bAnimationRate = 1;
        cAnimationRate = 1;

        workingChairsRect = new Rectangle[numberOfWorkingChairs];
        for (int i = 0; i < workingChairCoordinates.length; i++) {
            workingChairsRect[i] = new Rectangle(workingChairCoordinates[i].getKey(), workingChairCoordinates[i].getValue(), 100, 100);
            workingChairsRect[i].setFill(Color.LIGHTGRAY);
            workingChairsRect[i].setStroke(Color.BLACK);
            MyApplication.animationPane.getChildren().add(workingChairsRect[i]);
        }

        waitingChairsRect = new Rectangle[numberOfWaitingRoomChairs];
        for (int i = 0; i < waitingChairCoordinates.length; i++) {
            waitingChairsRect[i] = new Rectangle(waitingChairCoordinates[i].getKey(), waitingChairCoordinates[i].getValue(), 100, 100);
            waitingChairsRect[i].setFill(Color.RED);
            waitingChairsRect[i].setStroke(Color.BLACK);
            MyApplication.animationPane.getChildren().add(waitingChairsRect[i]);
        }
//        group = new Group(waitingChairsRect);
//        HelloApplication.animationPane.getChildren().add(group);

        sleepingChairsRect = new Rectangle[numberOfBarbers];
        for (int i = 0; i < sleepingChairCoordinates.length; i++) {
            sleepingChairsRect[i] = new Rectangle(sleepingChairCoordinates[i].getKey(), sleepingChairCoordinates[i].getValue(), 100, 100);
            sleepingChairsRect[i].setFill(Color.SILVER.darker());
            sleepingChairsRect[i].setStroke(Color.BLACK);
            MyApplication.animationPane.getChildren().add(sleepingChairsRect[i]);
        }
    }


    public void customerAnimationSpeedChange(double sliderValue) {

        synchronized (cAnimations) {

            if (sliderValue >= 0) {
                cAnimationRate = 1 + 0.02 * sliderValue;
            } else {
                cAnimationRate = 1 + 4.5 / 1000 * sliderValue;
            }

            for (Animation a : cAnimations) {
                a.setRate(cAnimationRate);
            }
        }
    }

    public void barberAnimationSpeedChange(double sliderValue) {

        synchronized (bAnimations) {

            if (sliderValue >= 0) {
                bAnimationRate = 1 + 0.03 * sliderValue;
            } else {
                bAnimationRate = 1 + 3 / 1000 * sliderValue;
            }

            for (Animation a : bAnimations) {
                a.setRate(bAnimationRate);
            }
        }
    }

    public void stopAnimation() {
        for (Animation a : bAnimations) {
            a.stop();
        }
        for (Animation a : cAnimations) {
            a.stop();
        }
    }

    public void startThreads() {

        queue = new LinkedBlockingDeque<>();
        Random random = new Random();
        this.mutex = new ReentrantLock(true);
        workingChairs = new Semaphore(numberOfWorkingChairs, true);


        workingChairStatus = new int[numberOfWorkingChairs][2];
        for (int i = 0; i < numberOfWorkingChairs; i++) {
            workingChairStatus[i][0] = 0;
            workingChairStatus[i][1] = -1;
        }

        waitingChairStatus = new int[numberOfWaitingRoomChairs];
        for (int i = 0; i < numberOfWaitingRoomChairs; i++) {
            waitingChairStatus[i] = -1;
        }


        barbers = new Barber[numberOfBarbers];
        for (int i = 0; i < numberOfBarbers; i++) {
            int serviceType;
            if (i < numOfServices) {
                serviceType = i;
                barbers[i] = new Barber(i, serviceType, queue, this, workingChairs, mutex);
                barbers[i].start();
            } else {
                serviceType = random.nextInt(numOfServices);
                barbers[i] = new Barber(i, serviceType, queue, this, workingChairs, mutex);
                barbers[i].start();
            }
        }
        customer = new Customer(queue, this, mutex);
        customer.start();
    }

    public void interruptThreads() {

        for (int i = 0; i < numberOfBarbers; i++) {
            barbers[i].interrupt();
        }

        customer.interrupt();

    }
}
