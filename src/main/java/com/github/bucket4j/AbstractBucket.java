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
package com.github.bucket4j;

import java.time.Duration;

import static com.github.bucket4j.BucketExceptions.nonPositiveNanosToWait;
import static com.github.bucket4j.BucketExceptions.nonPositiveTokensToConsume;

public abstract class AbstractBucket implements Bucket {

    protected static final long UNSPECIFIED_WAITING_LIMIT = -1;
    protected final BucketConfiguration configuration;

    protected AbstractBucket(BucketConfiguration configuration) {
        this.configuration = configuration;
    }

    protected abstract long consumeAsMuchAsPossibleImpl(long limit);

    protected abstract boolean tryConsumeImpl(long tokensToConsume);

    protected abstract long consumeOrAwaitImpl(long tokensToConsume, long waitIfBusyNanos) throws InterruptedException;

    @Override
    public boolean tryConsumeSingleToken() {
        return tryConsumeImpl(1);
    }

    @Override
    public boolean tryConsume(long tokensToConsume) {
        if (tokensToConsume <= 0) {
            throw nonPositiveTokensToConsume(tokensToConsume);
        }
        return tryConsumeImpl(tokensToConsume);
    }

    @Override
    public long consumeSingleToken() throws InterruptedException {
        return consume(1);
    }

    @Override
    public long consume(long tokensToConsume) throws InterruptedException {
        if (tokensToConsume <= 0) {
            throw nonPositiveTokensToConsume(tokensToConsume);
        }
        return consumeOrAwaitImpl(tokensToConsume, UNSPECIFIED_WAITING_LIMIT);
    }

    @Override
    public boolean tryConsumeSingleToken(Duration maxWaiting) throws InterruptedException {
        return tryConsume(1, maxWaiting);
    }

    @Override
    public boolean tryConsume(long tokensToConsume, Duration maxWaiting) throws InterruptedException {
        long maxWaitNanos = maxWaiting.toNanos();
        if (tokensToConsume <= 0) {
            throw nonPositiveTokensToConsume(tokensToConsume);
        }

        if (maxWaitNanos <= 0) {
            throw nonPositiveNanosToWait(maxWaitNanos);
        }

        long waitedNanos = consumeOrAwaitImpl(tokensToConsume, maxWaitNanos);
        return waitedNanos >= 0;
    }

    @Override
    public long consumeAsMuchAsPossible(long limit) {
        if (limit <= 0) {
            throw nonPositiveTokensToConsume(limit);
        }
        return consumeAsMuchAsPossibleImpl(limit);
    }

    @Override
    public long consumeAsMuchAsPossible() {
        return consumeAsMuchAsPossibleImpl(Long.MAX_VALUE);
    }

    @Override
    public BucketConfiguration getConfiguration() {
        return configuration;
    }

}