/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.logging.log4j.audit.rest;

import java.text.DecimalFormat;

public class ElapsedUtil {

    private static final long NANO_PER_SECOND = 1000000000L;
    private static final long NANO_PER_MINUTE = NANO_PER_SECOND * 60;
    private static final long NANO_PER_HOUR = NANO_PER_MINUTE * 60;

    static void addElapsed(long elapsed, StringBuilder msg) {
        long nanoseconds = elapsed;
        // Get elapsed hours
        long hours = nanoseconds / NANO_PER_HOUR;
        // Get remaining nanoseconds
        nanoseconds = nanoseconds % NANO_PER_HOUR;
        // Get minutes
        long minutes = nanoseconds / NANO_PER_MINUTE;
        // Get remaining nanoseconds
        nanoseconds = nanoseconds % NANO_PER_MINUTE;
        // Get seconds
        long seconds = nanoseconds / NANO_PER_SECOND;
        // Get remaining nanoseconds
        nanoseconds = nanoseconds % NANO_PER_SECOND;

        if (hours > 0) {
            msg.append(hours).append(" hours ");
        }
        if (minutes > 0 || hours > 0) {
            msg.append(minutes).append(" minutes ");
        }

        DecimalFormat numFormat = new DecimalFormat("#0");
        msg.append(numFormat.format(seconds)).append('.');
        numFormat = new DecimalFormat("000000000");
        msg.append(numFormat.format(nanoseconds)).append(" seconds");
    }
}
