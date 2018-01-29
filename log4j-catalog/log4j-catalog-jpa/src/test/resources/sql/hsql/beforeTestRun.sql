/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
INSERT INTO CATALOG_PRODUCT VALUES (1, 'DEFAULT', 'banking', 'Banking', 'Fictional banking product');

INSERT INTO EVENT_ATTRIBUTE VALUES (1, 'DEFAULT', 'accountNumber', 'Account Number', 'Company account number', 'int', 'Y', 'Y', 'Y', 'Y');
INSERT INTO EVENT_ATTRIBUTE VALUES (2, 'DEFAULT', 'ipAddress', 'IP Address', 'IP address of the caller', 'string', 'Y', 'Y', 'N', 'Y');
INSERT INTO EVENT_ATTRIBUTE VALUES (3, 'DEFAULT', 'userId', 'UserId', 'Id of the user', 'int', 'Y', 'Y', 'Y', 'Y');
INSERT INTO EVENT_ATTRIBUTE VALUES (4, 'DEFAULT', 'loginId', 'LoginId', 'Id user logs in with', 'string', 'Y', 'Y', 'Y', 'Y');
INSERT INTO EVENT_ATTRIBUTE VALUES (5, 'DEFAULT', 'hostName', 'Host Name', 'Name of the server', 'string', 'Y', 'Y', 'N', 'Y');
INSERT INTO EVENT_ATTRIBUTE VALUES (6, 'DEFAULT', 'fromAccount', 'From Account Number', 'Source of funds', 'int', 'N', 'N', 'Y', 'N');
INSERT INTO EVENT_ATTRIBUTE VALUES (7, 'DEFAULT', 'toAccount', 'To Account Number', 'Destination account', 'int', 'N', 'N', 'Y', 'N');
INSERT INTO EVENT_ATTRIBUTE VALUES (8, 'DEFAULT', 'amount', 'Amount', 'Amount to transfer', 'bigDecimal', 'N', 'N', 'Y', 'N');
INSERT INTO EVENT_ATTRIBUTE VALUES (9, 'DEFAULT', 'account', 'Account Number', 'Account number', 'int', 'N', 'N', 'Y', 'N');
INSERT INTO EVENT_ATTRIBUTE VALUES (10, 'DEFAULT', 'payee', 'Payee', 'Recipient of payment', 'string', 'N', 'N', 'Y', 'N');

INSERT INTO ATTRIBUTE_EXAMPLES VALUES (2, '127.0.0.1')

INSERT INTO CATALOG_EVENT VALUES (1, 'DEFAULT', 'login', 'Login', 'User login');
INSERT INTO CATALOG_EVENT VALUES (2, 'DEFAULT', 'transfer', 'Transfer', 'Transfer between accounts');
INSERT INTO CATALOG_EVENT VALUES (3, 'DEFAULT', 'deposit', 'Deposit', 'Deposit funds');
INSERT INTO CATALOG_EVENT VALUES (4, 'DEFAULT', 'billPay', 'Bill Pay', 'Payment of a bill');

INSERT INTO EVENT_ATTRIBUTES VALUES (1, 2, 6, 'Y');
INSERT INTO EVENT_ATTRIBUTES VALUES (2, 2, 7, 'Y');
INSERT INTO EVENT_ATTRIBUTES VALUES (3, 2, 8, 'Y');
INSERT INTO EVENT_ATTRIBUTES VALUES (4, 3, 8, 'Y');
INSERT INTO EVENT_ATTRIBUTES VALUES (5, 3, 9, 'Y');
INSERT INTO EVENT_ATTRIBUTES VALUES (6, 4, 10, 'Y');
INSERT INTO EVENT_ATTRIBUTES VALUES (7, 4, 8, 'Y');

INSERT INTO ATTRIBUTE_CONSTRAINT VALUES (1, 2, 'pattern', '^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$');

INSERT INTO PRODUCT_EVENTS VALUES (1, 1);
INSERT INTO PRODUCT_EVENTS VALUES (1, 2);
INSERT INTO PRODUCT_EVENTS VALUES (1, 3);

INSERT INTO CATALOG_CATEGORY VALUES (1, 'DEFAULT', 'account', 'Account', 'Events related to accounts');
INSERT INTO CATALOG_CATEGORY VALUES (2, 'DEFAULT', 'billPay', 'Bill Pay', 'Events related to bill payment');

INSERT INTO CATEGORY_EVENTS VALUES (1, 1);
INSERT INTO CATEGORY_EVENTS VALUES (1, 2);
INSERT INTO CATEGORY_EVENTS VALUES (1, 3);
INSERT INTO CATEGORY_EVENTS VALUES (2, 4);


