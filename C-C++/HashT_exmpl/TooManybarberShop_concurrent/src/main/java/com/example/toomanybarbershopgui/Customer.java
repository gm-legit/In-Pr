package com.example.toomanybarbershopgui;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Lock;

public class Customer extends Thread {
    public Configuration configuration;
    public volatile LinkedBlockingDeque<Triple<Integer, Integer, Group>> queue;
    Color[] colors = new Color[]{Color.FIREBRICK, Color.BLUE, Color.ALICEBLUE, Color.BURLYWOOD, Color.BISQUE, Color.BEIGE, Color.LAVENDER, Color.BLACK, Color.GREEN, Color.BROWN, Color.DARKRED, Color.MAGENTA, Color.LIME, Color.GOLD, Color.PLUM, Color.STEELBLUE, Color.SILVER};
    Lock mutex;

    public Customer(LinkedBlockingDeque<Triple<Integer, Integer, Group>> queue, Configuration configuration, Lock mutex) {
        this.queue = queue;
        this.configuration = configuration;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        int idCounter = 0, freeWaitingChairs;
        Random random = new Random();
        while (true) {
            try {
                freeWaitingChairs = configuration.numberOfWaitingRoomChairs - queue.size();
                int serviceType = random.nextInt(configuration.numOfServices);
                //Check if there is any free waiting chairs in waiting room
                if (freeWaitingChairs > 0) {

                    int freeWaitingChairNo = configuration.numberOfWaitingRoomChairs - 1;
                    synchronized (configuration.waitingChairStatus) {

                        for (int i = configuration.waitingChairStatus.length - 1; i >= 0; i--) {
                            if (configuration.waitingChairStatus[i] == -1) {
                                configuration.waitingChairStatus[i] = idCounter;
                                freeWaitingChairNo = i;
                                break;
                            }
                        }
                    }

                    Color color = colors[serviceType % configuration.numOfServices];

                    Rectangle rectangle = new Rectangle(configuration.waitingChairCoordinates[freeWaitingChairNo].getKey() + 50 - 20, configuration.waitingChairCoordinates[freeWaitingChairNo].getValue() + 50 - 20, 40.0, 40.0);

                    rectangle.setStroke(color.darker());
                    rectangle.setStrokeWidth(5);
                    rectangle.setFill(color);
                    Text textID = new Text("ID:" + idCounter);
                    Text textServiceType = new Text("S:" + serviceType);
                    textID.setX(rectangle.getX());
                    textID.setY(rectangle.getY() - 5);
                    textServiceType.setX(rectangle.getX());
                    textServiceType.setY(rectangle.getY() + 53);

                    textID.setId("Customer-" + idCounter);
                    textServiceType.setId("Customer-" + serviceType);

                    Group group = new Group(rectangle, textID, textServiceType);
                    group.setId("CGroup-" + idCounter);

                    Triple triple = new Triple<>(idCounter, serviceType, group);

                    synchronized (queue) {
                        queue.addLast(triple);
                        queue.notifyAll();
                    }

                    mutex.lock();

                    this.sleep(300);
                    Platform.runLater(() -> {
                        MyApplication.animationPane.getChildren().add(group);
                    });

                    mutex.unlock();

                    System.out.println("Customer " + idCounter + " is waiting in the queue for service " + serviceType);

                } else {
                    System.out.println("Customer " + idCounter + " left the shop because there were no available chairs in the waiting room. Queue size =  " + queue.size());
                    synchronized (queue) {
                        queue.notify();  //queue.notifyAll();
                    }
                }

                idCounter++;
                this.sleep((long) Math.random() % 5000 + 700);
            } catch (InterruptedException e) {
                this.interrupt();
                break;
            }
        }
    }
}