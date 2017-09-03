/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.audit.constant;

/**
 * The Class Column.
 */
public class Column {

	/** The Constant PREFIX. */
	public static final String REQCTX_PREFIX = "ReqCtx_";

	/** The Constant ID. */
	public static final String ID = REQCTX_PREFIX + "id";

	/** The Constant BCID. */
	public static final String BCID = REQCTX_PREFIX + "bcId";

	/** The Constant GUID. */
	public static final String GUID = "guId";

	/** The Constant USER_PRODUCT. */
	public static final String USER_PRODUCT = REQCTX_PREFIX + "userProduct";

	/** The Constant IP_ADDRESS. */
	public static final String IP_ADDRESS = REQCTX_PREFIX + "ipAddress";

	/** The Constant SESSION_ID. */
	public static final String SESSION_ID = REQCTX_PREFIX + "sessionId";

	/** The Constant USER_ID. */
	public static final String USER_ID = REQCTX_PREFIX + "userId";

	/** The Constant HOST_NAME. */
	public static final String HOST_NAME = REQCTX_PREFIX + "hostName";

	/** The Constant TIMEZONE_OFFSET. */
	public static final String TIMEZONE_OFFSET = REQCTX_PREFIX + "tzOffset";

	/** The Constant REGION. */
	public static final String REGION = REQCTX_PREFIX + "region";

	/** The Constant CANONICAL_ID. */
	public static final String CANONICAL_ID = REQCTX_PREFIX + "canonicalId";

	/** The Constant BC_INDEX. */
	public static final String BC_INDEX = REQCTX_PREFIX + "bcIndex";

	/** The Constant OFFERING_ID. */
	public static final String OFFERING_ID = REQCTX_PREFIX + "offeringId";

	/** The Constant APP_ID. */
	public static final String APP_ID = REQCTX_PREFIX + "appId";

	public static final String LOGIN_ID = REQCTX_PREFIX + "loginId";

	public static final String USER_AGENT = REQCTX_PREFIX + "userAgent";

	/** The Constant TIME_STAMP. */
	public static final String TIME_STAMP = "timeStamp";

	/** The Constant EVENT_TYPE. */
	public static final String EVENT_TYPE = "eventType";

	/** The Constant MEMBER. */
	public static final String MEMBER = "member";

	public static final String HOLDING_CO_ID = "holdingCoId";

	/** The Constant EVENT_ID. */
	public static final String EVENT_ID = "eventId";

	public static final String FEATURE_NAME = REQCTX_PREFIX + "featureName";

	public static final String COMPLETION_STATUS = "completionStatus";

	public static final String RAW_DATA = "rawData";

	public static final String NONRC_IP_ADDRESS = "ipAddress";

	public static final String NONRC_SESSION_ID = "sessionId";

	public static final String NONRC_USER_AGENT = "userAgent";
	
	public static final String APPLICATION_ID = "applicationId";
	
	public static final String NONRC_USER_PRODUCT = "userProduct";
}
