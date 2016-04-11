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

import com.github.bucket4j.builder.BucketBuilder;
import org.junit.Test;

import java.time.Duration;

import static com.github.bucket4j.common.TimeMeter.SYSTEM_MILLISECONDS;
import static com.github.bucket4j.common.TimeMeter.SYSTEM_NANOTIME;
import static org.junit.Assert.assertEquals;

public class BucketBuilderTest {

    @Test
    public void testWithNanoTimePrecision() throws Exception {
        assertEquals(SYSTEM_NANOTIME, BucketBuilder.forNanosecondPrecision().getTimeMeter());
    }

    @Test
    public void testWithMillisTimePrecision() throws Exception {
        assertEquals(SYSTEM_MILLISECONDS, BucketBuilder.forMillisecondPrecision().getTimeMeter());
    }

    @Test
    public void testWithCustomTimePrecision() throws Exception {
        assertEquals(SYSTEM_NANOTIME, BucketBuilder.forCustomTimePrecision(SYSTEM_NANOTIME).getTimeMeter());
        assertEquals(SYSTEM_MILLISECONDS, BucketBuilder.forCustomTimePrecision(SYSTEM_MILLISECONDS).getTimeMeter());
    }

    @Test
    public void testToString() throws Exception {
        Bucket bucket = BucketBuilder.forMillisecondPrecision().withLimitedBandwidth(100, Duration.ofMillis(10)).build();
        System.out.println(bucket);
    }

}