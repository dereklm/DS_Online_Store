Create Table ITEM (
ITEM_ID INT NOT NULL AUTO_INCREMENT,
model VARCHAR(30),
brand VARCHAR(30),
stock MEDIUMINT(5),
title VARCHAR(255),
description VARCHAR(255),
price DECIMAL(9,2),
PRIMARY KEY(ITEM_ID)
);

create table FILESTORAGE (
    FILE_ID INT NOT NULL AUTO_INCREMENT,
    ITEM_ID INT,
    FILE_NAME VARCHAR(255),
    FILE_TYPE VARCHAR(255),
    FILE_SIZE BIGINT,
    FILE_CONTENTS MEDIUMBLOB,  /* binary data */
    PRIMARY KEY (FILE_ID)
);