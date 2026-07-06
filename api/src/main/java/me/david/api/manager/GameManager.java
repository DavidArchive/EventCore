package me.david.api.manager;

import java.util.concurrent.atomic.AtomicInteger;

public interface GameManager {

    void start();

    void stop(String winner);

    void startInGameTimer();

    void stopInGameTimer();

    boolean isRunning();

    boolean isTimerRunning();

    AtomicInteger getTimer();

    long getInGameTimer();

    boolean isAutoDropped();
}
