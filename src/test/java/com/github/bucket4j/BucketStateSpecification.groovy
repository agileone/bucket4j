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

package com.github.bucket4j

import com.github.bucket4j.builder.BucketBuilder
import com.github.bucket4j.impl.SmoothlyRefillingBandwidthBandwidth
import com.github.bucket4j.impl.BucketState
import com.github.bucket4j.mock.TimeMeterMock
import spock.lang.Specification

import java.time.Duration

import static java.lang.Long.MAX_VALUE


class BucketStateSpecification extends Specification {

    def "GetAvailableTokens specification"(long requiredAvailableTokens, Bucket bucket) {
        setup:
            SmoothlyRefillingBandwidthBandwidth[] bandwidths = bucket.configuration.bandwidths
            BucketState state = bucket.getStateSnapshot()
        when:
            long availableTokens = state.getAvailableTokens(bandwidths)
        then:
            availableTokens == requiredAvailableTokens
        where:
            requiredAvailableTokens |                    bucket
                    10              | BucketBuilder.forNanosecondPrecision().withLimitedBandwidth(10, Duration.ofNanos(100)).build()
                     0              | BucketBuilder.forNanosecondPrecision().withLimitedBandwidth(10, 0, Duration.ofNanos(100)).build()
                     5              | BucketBuilder.forNanosecondPrecision().withLimitedBandwidth(10, 5, Duration.ofNanos(100)).build()
                     2              | BucketBuilder.forNanosecondPrecision().withLimitedBandwidth(10, 5, Duration.ofNanos(100)).withLimitedBandwidth(2, Duration.ofNanos(10)).build()
                     3              | BucketBuilder.forNanosecondPrecision().withLimitedBandwidth(10, 5, Duration.ofNanos(100)).withLimitedBandwidth(2, Duration.ofNanos(10)).withGuaranteedBandwidth(3, Duration.ofNanos(1000)).build()
                     2              | BucketBuilder.forNanosecondPrecision().withLimitedBandwidth(10, 5, Duration.ofNanos(100)).withLimitedBandwidth(2, Duration.ofNanos(10)).withGuaranteedBandwidth(1, Duration.ofNanos(1000)).build()
    }

    def "delayAfterWillBePossibleToConsume specification"(long toConsume, long requiredTime, Bucket bucket) {
        setup:
            SmoothlyRefillingBandwidthBandwidth[] bandwidths = bucket.configuration.bandwidths
            BucketState state = bucket.getStateSnapshot()
        when:
            long actualTime = state.delayNanosAfterWillBePossibleToConsume(bandwidths, 0, toConsume)
        then:
            actualTime == requiredTime
        where:
            toConsume | requiredTime |                               bucket
               10     |    100       | BucketBuilder.forCustomTimePrecision(new TimeMeterMock(0)).withLimitedBandwidth(10, 0, Duration.ofNanos(100)).build()
                7     |     30       | BucketBuilder.forCustomTimePrecision(new TimeMeterMock(0)).withLimitedBandwidth(10, 4, Duration.ofNanos(100)).build()
               11     |  MAX_VALUE   | BucketBuilder.forCustomTimePrecision(new TimeMeterMock(0)).withLimitedBandwidth(10, 4, Duration.ofNanos(100)).build()
                3     |     20       | BucketBuilder.forCustomTimePrecision(new TimeMeterMock(0)).withLimitedBandwidth(10, 1, Duration.ofNanos(100)).withLimitedBandwidth(5, 2, Duration.ofNanos(10)).build()
                3     |     20       | BucketBuilder.forCustomTimePrecision(new TimeMeterMock(0)).withLimitedBandwidth(5, 2, Duration.ofNanos(10)).withLimitedBandwidth(10, 1, Duration.ofNanos(100)).build()
                3     |      0       | BucketBuilder.forCustomTimePrecision(new TimeMeterMock(0)).withLimitedBandwidth(5, 2, Duration.ofNanos(10)).withGuaranteedBandwidth(10, 9, Duration.ofNanos(100)).build()
                6     |      0       | BucketBuilder.forCustomTimePrecision(new TimeMeterMock(0)).withLimitedBandwidth(5, 2, Duration.ofNanos(10)).withGuaranteedBandwidth(10, 9, Duration.ofNanos(100)).build()
                4     |      4       | BucketBuilder.forCustomTimePrecision(new TimeMeterMock(0)).withLimitedBandwidth(5, 0, Duration.ofNanos(10)).withGuaranteedBandwidth(25, 3, Duration.ofNanos(100)).build()
                4     |      2       | BucketBuilder.forCustomTimePrecision(new TimeMeterMock(0)).withLimitedBandwidth(5, 3, Duration.ofNanos(10)).withGuaranteedBandwidth(25, 3, Duration.ofNanos(100)).build()
    }

}
