package com.vuzix.sg.sdk;

/**
 * An interface for implementing timer event
 */
public interface TimerStopwatchCallbacks
{
    void resetStopwatch();
    void startStopwatch();
    void pauseStopwatch();

    void setTimer(int duration);
    void startTimer();
    void pauseTimer();
}
