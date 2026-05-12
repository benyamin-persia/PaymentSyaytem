INSERT INTO CUSTOMERS (FIRST_NAME, LAST_NAME, EMAIL)
VALUES ('Alice', 'Smith', 'alice@example.com');

INSERT INTO CUSTOMERS (FIRST_NAME, LAST_NAME, EMAIL)
VALUES ('Sara', 'Ahmed', 'sara@bank.com');

INSERT INTO CUSTOMERS (FIRST_NAME, LAST_NAME, EMAIL)
VALUES ('Khalid', 'Mahmoud', 'khalid@bank.com');

-- owner holds the authenticated username used for IDOR-safe ownership checks
INSERT INTO ACCOUNTS (ACCOUNT_NUMBER, OWNER, BALANCE, TYPE, STATUS, INTEREST_RATE, MINIMUM_BALANCE, CUSTOMER_ID)
VALUES ('ACC-1001', 'user', 1200.50, 'SAVINGS', 'ACTIVE', 1.50, 100.00, 1); -- Adds required fields introduced on Account.

INSERT INTO ACCOUNTS (ACCOUNT_NUMBER, OWNER, BALANCE, TYPE, STATUS, INTEREST_RATE, MINIMUM_BALANCE, CUSTOMER_ID)
VALUES ('ACC-1002', 'benyamin', 530.75, 'CHECKING', 'ACTIVE', 0.00, 0.00, 1); -- Adds required fields introduced on Account.

INSERT INTO ACCOUNTS (ACCOUNT_NUMBER, OWNER, BALANCE, TYPE, STATUS, INTEREST_RATE, MINIMUM_BALANCE, CUSTOMER_ID)
VALUES ('ACC-2001', 'admin', 10000, 'BUSINESS', 'ACTIVE', 0.00, 500.00, 2); -- Adds required fields introduced on Account.

-- BCrypt-hashed passwords (username/password: admin/admin123, user/user123, benyamin/benyamin123)
INSERT INTO APP_USERS (USERNAME, EMAIL, PASSWORD, ROLE, ENABLED, ACCOUNT_NON_LOCKED, FAILED_ATTEMPT, LOCK_TIME)
VALUES ('admin', 'admin@bank.local', '$2a$10$xn7Eo62JckEt3Ji0bKkbC.JlV9qHe.ZqCZtnVGUN7bxV/mcDxxBkW', 'ADMIN', true, true, 0, NULL); -- Seeds admin with required email column.

INSERT INTO APP_USERS (USERNAME, EMAIL, PASSWORD, ROLE, ENABLED, ACCOUNT_NON_LOCKED, FAILED_ATTEMPT, LOCK_TIME)
VALUES ('user', 'user@bank.local', '$2a$10$EPrZEVuZGidf4f9pVy1WP.7h4ZIneaMTuYywaKEKTBJzEQW8x16By', 'USER', true, true, 0, NULL); -- Seeds standard user with required email column.

INSERT INTO APP_USERS (USERNAME, EMAIL, PASSWORD, ROLE, ENABLED, ACCOUNT_NON_LOCKED, FAILED_ATTEMPT, LOCK_TIME)
VALUES ('benyamin', 'benyamin@bank.local', '$2a$10$uCFghag156s4XsT1CHANEOJI7rquD5A4r74FPPifa.vVAEPzKvZFe', 'USER', true, true, 0, NULL); -- Keeps previous sample user aligned with new schema.
