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

package realworld.grid;

import com.github.bucket4j.Bucket;
import com.github.bucket4j.builder.BucketBuilder;
import com.github.bucket4j.impl.BucketState;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import realworld.ConsumptionScenario;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class HazelcastTest {

    private static final String KEY = "42";
    private IMap<Object, GridBucketState> imap;
    private HazelcastInstance hazelcastInstance;

    @Before
    public void setup() {
        hazelcastInstance = Hazelcast.newHazelcastInstance();
        imap = hazelcastInstance.getMap("my_buckets");
    }

    @After
    public void shutdown() {
        hazelcastInstance.shutdown();
    }

    @Test
    public void test15Seconds() throws Exception {
        Bucket bucket = BucketBuilder.forNanosecondPrecision()
                .withLimitedBandwidth(1_000, 0, Duration.ofMinutes(1))
                .withLimitedBandwidth(200, 0, Duration.ofSeconds(10))
                .buildHazelcast(imap, KEY);

        ConsumptionScenario scenario = new ConsumptionScenario(4, TimeUnit.SECONDS.toNanos(15), bucket);
        long consumed = scenario.execute();
        long duration = scenario.getDurationNanos();
        System.out.println("Consumed " + consumed + " tokens in the " + duration + " nanos");

        float actualRate = (float) consumed / (float) duration;
        float permittedRate = 200.0f / (float) TimeUnit.SECONDS.toNanos(10);

        String msg = "Actual rate " + actualRate + " is greater then permitted rate " + permittedRate;
        assertTrue(msg, actualRate <= permittedRate);

        BucketState snapshot = bucket.getStateSnapshot();
        long available = snapshot.getAvailableTokens(bucket.getConfiguration().getBandwidths());
        long rest = bucket.consumeAsMuchAsPossible();
        assertTrue(rest >= available);
    }

}
