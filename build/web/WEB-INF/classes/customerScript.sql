Create Table CUSTOMER (
CUSTOMER_ID INT NOT NULL AUTO_INCREMENT,
email VARCHAR(30),
password char(64),
realName VARCHAR(30),
phone VARCHAR(12),
billingAddress VARCHAR(65),
shippingAddress VARCHAR(65),
PRIMARY KEY(CUSTOMER_ID)
);

create table user_groups (
user_id int not null auto_increment,
username varchar(30),
groupname varchar(15),
primary key(user_id)
);

insert into customer values (1, "root@amazon.com", "4813494D137E1631BBA301D5ACAB6E7BB7AA74CE1185D456565EF51D737677B2", "root", "1111111111", "1 Amazon Drive", "1 Amazon Drive");
insert into user_groups values (1, "root@amazon.com", "admingroup");
