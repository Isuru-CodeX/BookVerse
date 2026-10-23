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
