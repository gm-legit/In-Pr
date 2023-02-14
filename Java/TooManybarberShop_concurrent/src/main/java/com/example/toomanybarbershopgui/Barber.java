package com.example.toomanybarbershopgui;

import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class Barber extends Thread {

    int id;
    int serviceType;
    public Configuration configuration;
    public volatile LinkedBlockingDeque<Triple<Integer, Integer, Group>> queue; //One queue to every service
    Semaphore workingChairs;
    Triple triple;
    Color[] colors = new Color[]{Color.FIREBRICK, Color.BLUE, Color.ALICEBLUE, Color.BURLYWOOD, Color.BISQUE, Color.BEIGE, Color.LAVENDER, Color.BLACK, Color.GREEN, Color.BROWN, Color.DARKRED, Color.MAGENTA, Color.LIME, Color.GOLD, Color.PLUM, Color.STEELBLUE, Color.SILVER};
    Lock mutex;
    int freeWorkingChair;

    public Barber(int id, int serviceType, LinkedBlockingDeque<Triple<Integer, Integer, Group>> queue, Configuration configuration, Semaphore workingChairs, Lock mutex) {
        this.queue = queue;
        this.configuration = configuration;
        this.serviceType = serviceType;
        this.id = id;
        this.workingChairs = workingChairs;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        int licznik = 0;
        int idCustomer;

        Color color = colors[serviceType % configuration.numOfServices];
        Rectangle rectangle = new Rectangle(configuration.sleepingChairCoordinates[id % configuration.numberOfBarbers].getKey() + 50 - 20, configuration.sleepingChairCoordinates[id % configuration.numberOfBarbers].getValue() + 50 - 20, 40.0, 40.0);
        rectangle.setStroke(color.darker());
        rectangle.setStrokeWidth(8);
        rectangle.setFill(color);
        Text textID = new Text("BID:" + id);
        Text textServiceType = new Text("S:" + serviceType);
        textID.setX(rectangle.getX());
        textID.setY(rectangle.getY() - 5);
        textServiceType.setX(rectangle.getX());
        textServiceType.setY(rectangle.getY() + 53);

        textID.setId("Barber-" + id);
        textServiceType.setId("Barber-" + serviceType);
        Group BGroup = new Group(rectangle, textID, textServiceType);
        BGroup.setId("BGroup-" + id);

        Platform.runLater(() -> {
            MyApplication.animationPane.getChildren().add(BGroup);
        });
//        System.out.println("Barber " + this.id + " has finished appearing.");
        while (true) {
            try {
                licznik++;
                System.out.println("Barber " + this.id + " - loop no: " + licznik);


                synchronized (queue) {
                    while (true) {
                        try {
                            if (queue.isEmpty() || queue.peek().getSecond() != this.serviceType || workingChairs.availablePermits() == 0) {
                                queue.wait();
                            } else {
                                triple = queue.take();
                                break;
                            }
                        } catch (InterruptedException e) {
                           this.interrupt();
                        }
                    }

                    System.out.println("Barber " + this.id + " took a customer.");
                    queue.notifyAll();

                }

                idCustomer = (int) triple.getFirst();
                Group CGroup = (Group) triple.getThird();


                workingChairs.acquire();

                goToChair(false, BGroup);       //I'm going to the working chair
                mutex.lock();
                takeToChair(CGroup);

                synchronized (configuration.waitingChairStatus) {
                    for (int i = 0; i < configuration.numberOfWaitingRoomChairs; i++) {
                        if (configuration.waitingChairStatus[i] == (int) triple.getFirst()) {
                            configuration.waitingChairStatus[i] = -1;
                            break;
                        }
                    }
                }
                mutex.unlock();
                this.sleep((long) (Math.random()%4500 + 2500));


                System.out.println("Barber " + this.id + " has finished serving customer " + idCustomer);


                Platform.runLater(() -> {
                    MyApplication.animationPane.getChildren().remove(CGroup);
                });


                goToChair(true, BGroup);
                synchronized (queue) {
                    queue.notifyAll();
                }
                workingChairs.release();

            } catch (InterruptedException e) {
                this.interrupt();
                break;
            }
        }
    }


    public synchronized void goToChair(boolean chair, Group BGroup) {

        Rectangle brect = (Rectangle) BGroup.getChildren().get(0);
        Path path = new Path();
        int x, y;
        if (chair == false) {
            for (int i = 0; i < configuration.numberOfWorkingChairs; i++) {
                if (configuration.workingChairStatus[i][0] == 0) {
                    configuration.workingChairStatus[i][0] = 1;
                    configuration.workingChairStatus[i][1] = this.id;
                    freeWorkingChair = i;
                    break;
                }
            }

            x = configuration.workingChairCoordinates[freeWorkingChair].getKey() + 50;
            y = configuration.workingChairCoordinates[freeWorkingChair].getValue() - 40;

            path.getElements().add(new MoveTo(brect.getX() + 20, brect.getY() + 20));
            path.getElements().add(new LineTo(x, y));

        } else {
            int myWorkingChair = -1;
            for (int i = 0; i < configuration.numberOfWorkingChairs; i++) {
                if (configuration.workingChairStatus[i][1] == this.id) {
                    configuration.workingChairStatus[i][0] = 0;
                    configuration.workingChairStatus[i][1] = -1;
                    myWorkingChair = i;
                    break;
                }
            }

            x = configuration.workingChairCoordinates[myWorkingChair].getKey() + 50;
            y = configuration.workingChairCoordinates[myWorkingChair].getValue() - 40;

            path.getElements().add(new MoveTo(x, y));
            path.getElements().add(new LineTo(configuration.sleepingChairCoordinates[id].getKey() + 50, configuration.sleepingChairCoordinates[id].getValue() + 50));
        }

        PathTransition pathTransition = new PathTransition(Duration.millis(400*configuration.bAnimationRate), path, BGroup);
        Platform.runLater(() -> {
            pathTransition.play();
        });
        pathTransition.setOnFinished(e -> {
            configuration.bAnimations.remove(pathTransition);
        });
    }


    public synchronized void takeToChair(Group CGroup) throws InterruptedException {

        //Place customer on the chair
        Rectangle crect = (Rectangle) CGroup.getChildren().get(0);
        Path path = new Path();

        path.getElements().add(new MoveTo(crect.getX() + 20, crect.getY() + 20));
        path.getElements().add(new LineTo(configuration.workingChairCoordinates[freeWorkingChair].getKey() + 50, configuration.workingChairCoordinates[freeWorkingChair].getValue() + 50));


        PathTransition pathTransition = new PathTransition(Duration.millis(400*configuration.cAnimationRate), path, CGroup);
        Platform.runLater(() -> {
            pathTransition.play();
        });

        pathTransition.setOnFinished(e -> {
            configuration.bAnimations.remove(pathTransition);
        });
    }
}