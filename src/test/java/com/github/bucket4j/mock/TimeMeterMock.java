/*
 * Copyright 2015 Vladimir Bukhtoyarov
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.bucket4j.mock;

import com.github.bucket4j.TimeMeter;

import java.util.concurrent.TimeUnit;

/**
 * Created by vladimir.bukhtoyarov on 09.04.2015.
 */
public class TimeMeterMock implements TimeMeter {

    private long currentTimeNanos;
    private long sleeped = 0;
    private long incrementAfterEachSleep;

    public TimeMeterMock() {
        currentTimeNanos = 0;
    }

    public TimeMeterMock(long currentTime) {
        this.currentTimeNanos = currentTime;
    }

    public void addTime(long duration) {
        currentTimeNanos += duration;
    }

    public void setCurrentTimeNanos(long currentTimeNanos) {
        this.currentTimeNanos = currentTimeNanos;
    }

    public void setIncrementAfterEachSleep(long incrementAfterEachSleep) {
        this.incrementAfterEachSleep = incrementAfterEachSleep;
    }

    public long getSleeped() {
        return sleeped;
    }

    @Override
    public long currentTimeNanos() {
        return currentTimeNanos;
    }

    @Override
    public void parkNanos(long nanos) throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
        currentTimeNanos += nanos + incrementAfterEachSleep;
        sleeped += nanos;
    }

    public void reset() {
        currentTimeNanos = 0;
        sleeped = 0;
    }

}
