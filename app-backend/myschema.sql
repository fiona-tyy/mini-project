DROP DATABASE IF EXISTS go_dutch;

CREATE DATABASE go_dutch;

USE go_dutch;

CREATE TABLE users (
    email VARCHAR(256) NOT NULL,
    name VARCHAR(256),
    phone_number VARCHAR(32),

    PRIMARY KEY(email)
);


CREATE TABLE transactions (

    id VARCHAR(256) NOT NULL,
    transaction_type ENUM('expense', 'settlement'),
    description VARCHAR(256),
    date BIGINT,
    total_amount DECIMAL(10,2),
    recorded_by VARCHAR(256),
    recorded_date BIGINT,
    attachment ENUM('Y','N'),

    PRIMARY KEY(id)
);

CREATE TABLE loans (

    id INT AUTO_INCREMENT,
    transaction_id VARCHAR(256) NOT NULL,
    lender_email VARCHAR(256),
    borrower_email VARCHAR(256),
    amount DECIMAL(10,2),

    PRIMARY KEY(id),
    CONSTRAINT fk_trans_id
        FOREIGN KEY(transaction_id) 
        REFERENCES transactions(id)
        ON DELETE CASCADE
);

CREATE TABLE friends (
    user_email VARCHAR(256) NOT NULL,
    friend_email VARCHAR(256) NOT NULL,

    PRIMARY KEY(user_email, friend_email)
);

grant all privileges on go_dutch.* to 'Fred'@'localhost';