-- =====================================================================
-- BookVerse Online Library Portal - database schema (MySQL 5.7+ / 8.x, MariaDB 10.3+)
-- HF2K Web Component Development II
-- Run this whole file once (MySQL Workbench: File > Open SQL Script > Run, or: mysql -u root -p < bookverse.sql)
-- =====================================================================
CREATE DATABASE IF NOT EXISTS bookverse CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookverse;

-- Application user (the same credentials are in web.xml). Run this script with an admin account such as root.
CREATE USER IF NOT EXISTS 'bookverse'@'localhost' IDENTIFIED BY 'bookverse123';
GRANT ALL PRIVILEGES ON bookverse.* TO 'bookverse'@'localhost';
FLUSH PRIVILEGES;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS announcements;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS membership_plans;
SET FOREIGN_KEY_CHECKS = 1;

-- Booking rules per membership type (editable in the staff "Plans" tab)
CREATE TABLE membership_plans (
  type             VARCHAR(20)   NOT NULL,
  label            VARCHAR(40)   NOT NULL,
  description      VARCHAR(120)  NOT NULL DEFAULT '',
  max_loans        INT           NOT NULL,
  loan_days        INT           NOT NULL,
  short_loan_days  INT           NOT NULL,
  max_renewals     INT           NOT NULL,
  monthly_fee      DECIMAL(10,2) NOT NULL DEFAULT 0,
  sort_order       INT           NOT NULL DEFAULT 9,
  PRIMARY KEY (type),
  CONSTRAINT chk_plan_limits CHECK (max_loans BETWEEN 1 AND 20 AND loan_days BETWEEN 1 AND 90
                                    AND short_loan_days BETWEEN 1 AND 30 AND max_renewals BETWEEN 0 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Library accounts (passwords are salted SHA-256 hashes, see PasswordUtil.java)
CREATE TABLE members (
  id               INT          NOT NULL,
  name             VARCHAR(60)  NOT NULL,
  email            VARCHAR(120) NOT NULL,
  password_hash    CHAR(64)     NOT NULL,
  role             VARCHAR(10)  NOT NULL DEFAULT 'MEMBER',
  membership_type  VARCHAR(20)  NOT NULL DEFAULT 'STANDARD',
  join_date        DATETIME     NOT NULL,
  active           TINYINT(1)   NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY uq_members_email (email),
  KEY idx_members_plan (membership_type),
  CONSTRAINT fk_members_plan FOREIGN KEY (membership_type) REFERENCES membership_plans (type),
  CONSTRAINT chk_members_role CHECK (role IN ('MEMBER','STAFF'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Catalogue: books, journals and magazines
CREATE TABLE books (
  id               INT           NOT NULL,
  title            VARCHAR(120)  NOT NULL,
  author           VARCHAR(120)  NOT NULL,
  category         VARCHAR(30)   NOT NULL,
  isbn             VARCHAR(30)   NOT NULL DEFAULT '',
  description      VARCHAR(400)  NOT NULL DEFAULT '',
  publish_year     INT           NOT NULL,
  total_copies     INT           NOT NULL,
  available_copies INT           NOT NULL,
  rating           DECIMAL(2,1)  NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_books_category (category),
  KEY idx_books_title (title),
  CONSTRAINT chk_books_copies CHECK (available_copies >= 0 AND available_copies <= total_copies)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Loans / reservations. book_id is deliberately NOT a foreign key: loan history survives when a title is
-- deleted (book_title is stored with every loan).
CREATE TABLE reservations (
  id            INT          NOT NULL,
  book_id       INT          NOT NULL,
  book_title    VARCHAR(120) NOT NULL,
  member_email  VARCHAR(120) NOT NULL,
  reserved_on   DATETIME     NOT NULL,
  due_date      DATETIME     NOT NULL,
  returned_on   DATETIME     NULL,
  status        VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE',
  renewals      INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_res_member (member_email),
  KEY idx_res_book (book_id),
  KEY idx_res_status (status),
  CONSTRAINT fk_res_member FOREIGN KEY (member_email) REFERENCES members (email)
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT chk_res_status CHECK (status IN ('ACTIVE','RETURNED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE announcements (
  id         INT           NOT NULL,
  title      VARCHAR(80)   NOT NULL,
  message    VARCHAR(300)  NOT NULL,
  posted_on  DATETIME      NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE feedback (
  id            INT           NOT NULL,
  name          VARCHAR(60)   NOT NULL,
  email         VARCHAR(120)  NOT NULL,
  rating        INT           NOT NULL,
  comments      VARCHAR(500)  NOT NULL,
  submitted_on  DATETIME      NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT chk_feedback_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- SAMPLE DATA (optional: the application creates the same data itself when the tables are empty)
-- Demo logins:  admin@bookverse.lk / admin123 (staff)    member@bookverse.lk / member123 (member)
-- =====================================================================
INSERT INTO membership_plans (type, label, description, max_loans, loan_days, short_loan_days, max_renewals, monthly_fee, sort_order) VALUES
 ('STANDARD','Standard','Everyday borrowing for general readers.',3,14,7,1,500,1),
 ('PREMIUM','Premium','More titles, the longest loans and the most renewals.',8,28,14,3,1500,2),
 ('STUDENT','Student','Free for enrolled students, with extended study loans.',5,21,10,2,0,3);

INSERT INTO members (id, name, email, password_hash, role, membership_type, join_date, active) VALUES
 (1,'Library Admin','admin@bookverse.lk','3fa897120e2ea920b86e794e83aa31473e5adff1255b93d96a578f39dbd8c237','STAFF','PREMIUM',NOW(),1),
 (2,'Nimali Perera','member@bookverse.lk','7450f5ac4446cd78c90d81501a69629f41c8e52491830c207c307da7629b7ea2','MEMBER','STANDARD',NOW(),1);

INSERT INTO books (id, title, author, category, isbn, description, publish_year, total_copies, available_copies, rating) VALUES
 (1,'Clean Code','Robert C. Martin','Technology','9780132350884','A handbook of agile software craftsmanship: naming, functions, formatting and refactoring habits that keep code readable.',2008,3,2,4.6),
 (2,'Effective Java','Joshua Bloch','Technology','9780134685991','Programming-language guide with 90 items on writing clear, correct and robust Java.',2018,2,2,4.8),
 (3,'Head First Servlets and JSP','Bryan Basham, Kathy Sierra, Bert Bates','Technology','9780596516680','A visual, hands-on guide to servlets, JSP, EL, JSTL and custom tags for the Java EE web tier.',2008,2,2,4.4),
 (4,'The Pragmatic Programmer','Andrew Hunt, David Thomas','Technology','9780135957059','Practical advice on tools, habits and attitudes for working developers.',2019,2,2,4.5),
 (5,'A Brief History of Time','Stephen Hawking','Science','9780553380163','An accessible tour of black holes, the Big Bang and the nature of time.',1988,2,2,4.3),
 (6,'Cosmos','Carl Sagan','Science','9780345539434','A personal voyage through the universe and the history of scientific thought.',1980,1,1,4.6),
 (7,'Sapiens','Yuval Noah Harari','History','9780062316097','A brief history of humankind, from the Stone Age to the present.',2011,1,0,4.4),
 (8,'Guns, Germs, and Steel','Jared Diamond','History','9780393317558','Why human societies developed so differently across the continents.',1997,2,2,4.1),
 (9,'1984','George Orwell','Fiction','9780451524935','A dystopian classic about surveillance, truth and power.',1949,3,3,4.7),
 (10,'Pride and Prejudice','Jane Austen','Fiction','9780141439518','Manners, marriage and misjudgement in Regency England.',1813,2,2,4.5),
 (11,'The Alchemist','Paulo Coelho','Fiction','9780062315007','A shepherd''s journey in search of a treasure and his personal legend.',1988,2,2,4.0),
 (12,'Web Engineering Review','BookVerse Editorial Board','Journal','ISSN 0000-0001','Quarterly journal covering web architecture, Java EE and front-end engineering (sample issue).',2025,2,2,3.9),
 (13,'Science Today','BookVerse Editorial Board','Magazine','ISSN 0000-0002','Monthly magazine of popular science news and long reads (sample issue).',2026,4,4,4.2),
 (14,'Educator''s Digest','BookVerse Editorial Board','Magazine','ISSN 0000-0003','Teaching ideas, study techniques and campus stories (sample issue).',2026,3,3,3.8);

INSERT INTO reservations (id, book_id, book_title, member_email, reserved_on, due_date, returned_on, status, renewals) VALUES
 (1,1,'Clean Code','member@bookverse.lk',NOW(),DATE_ADD(NOW(), INTERVAL 14 DAY),NULL,'ACTIVE',0),
 (2,7,'Sapiens','member@bookverse.lk',NOW(),DATE_ADD(NOW(), INTERVAL 14 DAY),NULL,'ACTIVE',0),
 (3,5,'A Brief History of Time','member@bookverse.lk',DATE_SUB(NOW(), INTERVAL 40 DAY),DATE_SUB(NOW(), INTERVAL 26 DAY),DATE_SUB(NOW(), INTERVAL 30 DAY),'RETURNED',0);

INSERT INTO announcements (id, title, message, posted_on) VALUES
 (1,'Extended weekend hours','The reading room is now open until 8 p.m. on Saturdays and Sundays.',NOW()),
 (2,'New journal issue','The latest issue of Web Engineering Review is on the shelves and ready to reserve.',NOW()),
 (3,'Membership drive','Students can join for free this month. Register online and start reserving today.',NOW());
