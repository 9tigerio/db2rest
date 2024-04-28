
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS wakila;
CREATE SCHEMA wakila;
USE wakila;


--
-- Table structure for table `users`
--

CREATE TABLE users (
     auid int(10) UNSIGNED NOT NULL,
     username varchar(100) NOT NULL,
     password varchar(150) NOT NULL,
     createdate datetime NOT NULL,
     isActive tinyint(1) NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `userprofile`
--

CREATE TABLE userprofile (
   apid int(10) UNSIGNED NOT NULL,
   auid int(10) UNSIGNED NOT NULL,
   firstname varchar(50) NOT NULL,
   lastname varchar(50) NOT NULL,
   email varchar(100) NOT NULL,
   phone varchar(45) NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- users
-- userpreference

Insert into users
(auid, username,password, createdate, isActive)
values
    (1,'admin','pswrd123', curdate(), 1);

Insert into userprofile
(apid, auid, firstname, lastname, email, phone)
values
    (1,1,'Jack', 'Wolf', 'bettestroom@gmail.com','600075764216');

Insert into users
(auid,username,password, createdate, isActive)
values
    (2, 'admin1','pass506', curdate(), 1);

Insert into userprofile
(apid, auid, firstname, lastname, email, phone)
values
    (2, 3, 'Tom', 'Collins', 'tnkc@outlook.com','878511311054');

Insert into users
(auid, username,password, createdate, isActive)
values
    (4,'fox12','45@jgo0', curdate(), 1);

Insert into userprofile
(apid, auid, firstname, lastname, email, phone)
values
    (4,5,'Bill', 'Fonskin', 'bill_1290@gmail.com','450985764216');

Insert into users
(auid,username,password, createdate, isActive)
values
    (6, 'lexus1267','98hnfRT6', curdate(), 1);

Insert into userprofile
(apid, auid, firstname, lastname, email, phone)
values
    (7, 7, 'Ivan', 'Levchenko', 'ivan_new@outlook.com','878511311054');


--
-- Table structure for table `employee`
--

CREATE TABLE employee (
          emp_id int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
          first_name varchar(100) NOT NULL,
          last_name varchar(150) NOT NULL,
          create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          is_active tinyint(1) DEFAULT 1,
          PRIMARY KEY (emp_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


Insert into employee (first_name, last_name) values ('Ivan', 'Levchenko');
Insert into employee (first_name, last_name) values ('Roger', 'Federer');


--
-- Table structure for table `department`
--

CREATE TABLE department (
      dept_id int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
      name varchar(100) NOT NULL,
      create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      is_active tinyint(1) DEFAULT 1,
      PRIMARY KEY (dept_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


Insert into department (name) values ('Marketing');
Insert into department (name) values ('Sales');
