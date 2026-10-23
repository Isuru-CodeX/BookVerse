# BookVerse Online Library Portal
**HF2K – Web Component Development II · Component A (Web Application Development Project)**

A JSP-based Java EE web application (Servlet 4.0 / JSP 2.3 / EL / JSTL 1.2) for the BookVerse scenario.
Data is held in memory (`LibraryService`), so no database or extra setup is needed.

## 1. Run it

The application works in **two modes**:
* **MySQL database mode** (recommended): all data is stored in the `bookverse` database, so it survives restarts.
* **In-memory demo mode**: used automatically if no database is reachable. Data resets on every restart.
The page footer shows which mode is active ("Data store: MySQL database").

### Step-by-step: add the MySQL database

**Step 1 - Install MySQL 8 (or XAMPP / MariaDB) and make sure the server is running.**

**Step 2 - Import the database file.** Open a terminal in the project folder and run
```
mysql -u root -p < database/bookverse.sql
```
(or in MySQL Workbench: File > Open SQL Script > `database/bookverse.sql` > click the lightning bolt;
or in phpMyAdmin: Import tab > choose the file > Go).
This creates the `bookverse` database, the six tables, a `bookverse` user (password `bookverse123`) and sample data.
Use `database/bookverse_schema_only.sql` instead if you want empty tables; the application then fills them with the
same sample data on first start.

**Step 3 - Check the connection settings** in `src/main/webapp/WEB-INF/web.xml` (section "Database"):
```
db.url      jdbc:mysql://localhost:3306/bookverse?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Colombo...
db.user     bookverse
db.password bookverse123
db.driver   com.mysql.cj.jdbc.Driver
```
Change them only if your MySQL uses another port, user or password. You can also set the environment variables
`BOOKVERSE_DB_URL`, `BOOKVERSE_DB_USER`, `BOOKVERSE_DB_PASSWORD`, `BOOKVERSE_DB_DRIVER`, which override web.xml.

**Step 4 - Build with Maven** (the MySQL driver `mysql-connector-j` is declared in `pom.xml` and is packaged into the WAR):
```
mvn clean package
```
*Without Maven?* Use the ready-made `bookverse.war` and copy the driver yourself: download
`mysql-connector-j-8.4.0.jar` (https://dev.mysql.com/downloads/connector/j/ - "Platform Independent", or Maven Central:
`https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar`) and put it into
`<TOMCAT_HOME>/lib/`, then restart Tomcat.

**Step 5 - Deploy on Apache Tomcat 9.x:** copy `target/bookverse.war` (or the ready-made `bookverse.war`) to
`<TOMCAT_HOME>/webapps/`, start Tomcat and open `http://localhost:8080/bookverse/`.

**Step 6 - Verify.** The footer must say **Data store: MySQL database**. Sign in as `member@bookverse.lk` /
`member123`, reserve a book, then run `SELECT * FROM reservations;` in MySQL - the new loan is there.

**Troubleshooting.** If the footer says "In-memory demo data (database unavailable)", open
`<TOMCAT_HOME>/logs/localhost.<date>.log` or `catalina.out` and look for `[BookVerse] SEVERE`. Typical causes:
MySQL not running; wrong user/password; driver jar missing; database not imported
(`Table 'bookverse.books' doesn't exist`); port other than 3306.
MySQL 5.7 / MariaDB: same steps (MariaDB users can also use the MariaDB driver:
`db.driver=org.mariadb.jdbc.Driver`, `db.url=jdbc:mariadb://localhost:3306/bookverse`).

### Run without a database
Set `db.url` to an empty value in web.xml (or just do not start MySQL). Requirements: JDK 11+, Maven 3.6+, Tomcat 9.x.

### IDE (Eclipse / IntelliJ / NetBeans)
Import as an existing **Maven project**, add a Tomcat 9 server, run on server.

> Tomcat 9 uses the `javax.*` namespace, which this project uses. Tomcat 10+ needs `jakarta.*`
> (replace `javax.servlet` with `jakarta.servlet` and use JSTL `jakarta.tags.core` URIs).

### Database design
| Table | Purpose | Key columns |
|---|---|---|
| `membership_plans` | booking rules per plan | PK `type` (STANDARD / PREMIUM / STUDENT) |
| `members` | accounts (staff and members) | PK `id`, unique `email`, FK `membership_type` -> plans |
| `books` | catalogue | PK `id`, CHECK `available_copies <= total_copies` |
| `reservations` | loans and history | PK `id`, FK `member_email` -> members(email), index on `book_id`, `status` |
| `announcements` | home page news | PK `id` |
| `feedback` | member feedback | PK `id`, CHECK `rating` 1-5 |

`reservations.book_id` is intentionally not a foreign key, so loan history survives when a title is deleted.
Data access: `com.bookverse.data.LibraryStore` (DAO interface) with `JdbcStore` (MySQL, PreparedStatements, transaction
for loan + stock update) and `MemoryStore`. `LibraryService` loads everything at start-up and writes every change through
the store.

### Demo accounts
| Role | Email | Password |
|---|---|---|
| Member | member@bookverse.lk | member123 |
| Staff | admin@bookverse.lk | admin123 |

## 2. Application map

| URL | Controller command | View |
|---|---|---|
| `/` , `index.jsp` | `jsp:forward` → `home.do` | – |
| `home.do` | `HomeCommand` | `views/home.jsp` |
| `catalog.do` | `CatalogCommand` | `views/catalog.jsp` |
| `bookDetail.do?id=` | `BookDetailCommand` | `views/bookDetail.jsp` |
| `login.do` / `logout.do` | `LoginCommand` / `LogoutCommand` | `views/login.jsp` |
| `register.do` | `RegisterCommand` | `views/register.jsp` |
| `dashboard.do` (member) | `DashboardCommand` | `views/dashboard.jsp` |
| `reserve.do`, `return.do` (member, POST) | `ReserveCommand`, `ReturnBookCommand` | redirect |
| `plans.do` (public) | `PlansCommand` | `views/plans.jsp` |
| `renew.do`, `changePlan.do` (member, POST) | `RenewCommand`, `ChangePlanCommand` | redirect |
| **Staff area** (all `admin*.do`, STAFF role only) | | |
| `admin.do` | `AdminCommand` | `views/admin.jsp` (overview, overdue, sold-out) |
| `adminBooks.do`, `adminBook.do`, `adminBookDelete.do` | `AdminBooksCommand`, `AdminBookCommand`, `AdminBookDeleteCommand` | `adminBooks.jsp`, `bookForm.jsp` (add + edit) |
| `adminMembers.do`, `adminMember.do?id=` | `AdminMembersCommand`, `AdminMemberCommand` | `adminMembers.jsp`, `memberEdit.jsp` |
| `adminLoans.do`, `adminLoanAction.do` | `AdminLoansCommand`, `AdminLoanActionCommand` | `adminLoans.jsp` |
| `adminPlans.do` | `AdminPlansCommand` | `adminPlans.jsp` |
| `adminAnnouncements.do` | `AdminAnnouncementsCommand` | `adminAnnouncements.jsp` |
| `adminFeedback.do` | `AdminFeedbackCommand` | `adminFeedback.jsp` |
| `adminReports.do` | `AdminReportsCommand` | `adminReports.jsp` |
| `elDemo.do` | `ElDemoCommand` | `views/elDemo.jsp` |
| `about.do` | lambda command | `views/about.jsp` |
| `feedback.jsp` (public) | `jsp:forward` → `feedbackSubmit.do` → `FeedbackSubmitCommand` | `views/feedbackThanks.jsp` |
| any error / 403 / 404 / 500 | `<error-page>` | `error.jsp` |

Shared fragments: `views/fragments/header.jsp`, `footer.jsp`, `note.jsp`.
Total JSP files: **26** (minimum 8).

## 2a. Where is the admin panel?
Sign in as `admin@bookverse.lk` and click **Staff** in the top bar (or open `/bookverse/admin.do`).
A tab bar (`t:adminNav`) links the eight staff sections:

| Tab | What staff can do |
|---|---|
| Overview | Statistics, overdue loans (return / +7 days), sold-out titles, shortcuts |
| Books | Search, add, **edit** (copies, rating, details) and **delete** titles (blocked while on loan) |
| Members | Search/filter accounts; change **plan**, name, **role** (Member/Staff) and **status** (Active/Suspended); reset passwords; see loan history |
| Loans | Filter by All/Active/Overdue/Returned; force return; extend due date by 7 days |
| Plans | Edit the booking rules of Standard, Premium and Student |
| Announcements | Publish and delete announcements |
| Feedback | Read and delete member feedback, average rating |
| Reports | Most borrowed titles, loans by status, members by plan, copies by category |

Safeguards: staff cannot suspend or demote their own account; suspended users are signed out on their next
request and cannot log in; a title on loan cannot be deleted or have copies reduced below the number on loan;
a member cannot be moved to a plan whose loan limit is below their open loans.

## 2b. Membership plans (booking data per plan)
| Rule | Standard | Premium | Student |
|---|---|---|---|
| Active loans at once | 3 | 8 | 5 |
| Book loan period | 14 days | 28 days | 21 days |
| Journal / magazine loan | 7 days | 14 days | 10 days |
| Renewals per loan | 1 | 3 | 2 |
| Monthly fee | Rs. 500 | Rs. 1,500 | Free |

These are defaults; staff can edit all values on the **Plans** tab and the change applies immediately to new
reservations and renewals (`LibraryService.reserve`, `renew`, `loanDaysFor`). Members can **renew** loans and
**switch between Standard and Premium** on their dashboard; Student is granted by staff. The plan shown on the
book page decides the due date (`BookDetailCommand` -> `LoanPolicy` bean). The `maxActiveLoans` context-param
now sets the default Standard loan limit.

## 3. Where each assessment requirement is implemented

| Requirement | Where to look |
|---|---|
| **EL implicit objects** `pageScope, requestScope, sessionScope, applicationScope, param, paramValues, header, headerValues, cookie, initParam, pageContext` | `views/elDemo.jsp` (live table, one row per expression). Also used across all pages, e.g. `sessionScope.member`, `applicationScope.appName`, `initParam.supportEmail`, `pageContext.request.contextPath` |
| EL `.` and `[]` operators | `elDemo.jsp` section 5 (`books[0].title`, `sampleMap['key.with.dots']`, `header['user-agent']`, `pageContext.session['new']`) |
| `jsp:useBean` | `catalog.jsp`, `login.jsp`, `register.jsp` (with body initialisation), `dashboard.jsp` (session scope), `bookDetail.jsp` (page scope), `feedback.jsp`, `footer.jsp` |
| `jsp:setProperty` | `feedback.jsp` (`property="*"`), `login.jsp` (`param=`), `register.jsp` (`value=`), `bookDetail.jsp` (`value="${book.loanDays}"`) |
| `jsp:getProperty` | `catalog.jsp`, `bookDetail.jsp`, `dashboard.jsp`, `feedbackThanks.jsp` |
| `jsp:include` + `jsp:param` | every view (header/footer), `elDemo.jsp` (`note.jsp`) |
| `jsp:forward` + `jsp:param` | `index.jsp`, `feedback.jsp` |
| `jsp:attribute` | `home.jsp` (fragment attribute of `<t:card>`), `elDemo.jsp` (value of `jsp:param`) |
| JSTL core | `c:set, c:out, c:if, c:choose/when/otherwise, c:forEach (varStatus), c:forTokens, c:remove, c:url, c:param`; also `fn:` and `fmt:formatDate` |
| No scriptlets | enforced by `<scripting-invalid>true` in `web.xml` |
| Custom tags (handlers) | `RatingTag` (SimpleTag), `AvailabilityTag` (Tag, PageContext API, `doStartTag/doEndTag/release`), `HighlightTag` (BodyTag, `doAfterBody`, `getPreviousOut`) – declared in `WEB-INF/tld/bookverse.tld` |
| Tag files | `WEB-INF/tags/`: `card.tag` (fragment attribute + `jsp:doBody`), `alert.tag`, `pageHeader.tag`, `bookcard.tag`, `elrow.tag`, `adminNav.tag` |
| Scopes | application: `appName, visitCounter, activeSessions, startedAt` · session: `member, flash*, demoNote` · request: `books, criteria, errors …` · page: `c:set`, `loanPolicy` bean |
| MVC (mandatory) | Model: `model/`, `bean/`, `service/` · View: JSP · Controller: `FrontController` + `command/` |
| Front Controller | `controller/FrontController.java` (`*.do`) |
| Command | `command/Command.java` and 26 implementing classes |
| Intercepting Filter | `EncodingFilter`, `LoggingFilter`, `AuthFilter` (members, and every `admin*` command for STAFF; order set in `web.xml`) |
| Business Delegate | `service/LibraryDelegate.java` |
| Data Access Object (DAO) | `data/LibraryStore.java` (interface), `JdbcStore.java` (MySQL), `MemoryStore.java` |
| Service Locator | `service/ServiceLocator.java` |
| Data Access Object (DAO) | `data/LibraryStore.java`, `JdbcStore.java`, `MemoryStore.java` |
| Transfer Object | `Book, Member, MembershipPlan, Reservation, Announcement` (Serializable) |
| Listeners | `AppContextListener`, `SessionCountListener` |
| Error handling & validation | `error.jsp` + `<error-page>`; `RegistrationForm.validate()`, `FeedbackBean.getErrors()`, `AddBookCommand`, business rules in `LibraryService` (loan limit, no stock, duplicate email) |
| Security basics | HTML escaping with `c:out` / tag handlers, whitelist redirect after login, session id change on login, HttpOnly cookies, POST for state changes, salted password hashes |

## 4. Assumptions (document these in the report)
1. Data is stored in MySQL when a database is configured (section 1). Without one, the app falls back to in-memory demo data that resets on restart. The service keeps a working copy in memory and writes every change through to the database; connections are opened per operation (a connection pool is a future improvement).
2. A "reservation" is an immediate loan of one copy; limits and periods come from the member's plan (section 2b).
3. Student status is not verified in software (library staff check student ID at the desk).
4. Two roles only: MEMBER and STAFF. Staff accounts are pre-created.
5. Ratings are static editorial scores (members cannot rate books).
6. Passwords use salted SHA-256 for the demo; BCrypt/PBKDF2 would be used in production.

## 5. Test checklist (all verified on Tomcat 9.0 during development)
| # | Test | Expected |
|---|---|---|
| 1 | Open `/bookverse/` | Home page via `jsp:forward`; visit counter in footer increases |
| 2 | Catalogue → search `java`, sort by rating | Matches highlighted with `<mark>` |
| 3 | Catalogue → keyword `zzzz` | "No titles match" alert |
| 4 | Open `bookDetail.do?id=999` / `id=abc` | Custom 404 page |
| 5 | Open `dashboard.do` when signed out | Redirect to login with return address |
| 6 | Sign in with wrong password | Error message, email kept |
| 7 | Sign in as member → reserve a title | Flash message, loan on dashboard, copies reduced |
| 8 | Reserve a 4th title | Loan-limit message |
| 9 | Reserve a title with 0 copies | Button disabled / error message |
| 10 | Return a title | Moves to reading history, copies restored |
| 11 | Member opens `admin.do` | 403 page |
| 12 | Register with bad data | Five field-level errors |
| 13 | Register with an existing email | "already exists" message |
| 14 | Staff → add title with empty fields | Field errors; valid input adds the title |
| 15 | Staff → post announcement | Appears on home page |
| 16 | `feedback.jsp` invalid then valid | Errors, then thank-you page (`jsp:forward`) |
| 17 | `elDemo.do?q=Hello&tag=java&tag=jsp` | `param`, `paramValues` rows populated |
| 18 | `catalog.do?keyword=<script>` | Text is escaped, nothing executes |
| 19 | Staff -> Plans: set Standard book loan to 10 days, save | Plans page and new loans use 10 days; limit 99 is rejected |
| 20 | Member (Standard) renews a loan twice | First succeeds ("1 / 1"), second refused |
| 21 | Member switches to Premium | Loan limit 8, book page shows 28-day due date |
| 22 | Member with 8 loans switches back to Standard | Refused (limit 3) |
| 23 | Staff -> Members: suspend a member | Member's next request signs them out; login shows "suspended" |
| 24 | Staff edits own account to Member role | "You cannot suspend or demote your own account" |
| 25 | Staff -> Books: delete a title that is on loan | Refused; unloaned title deletes |
| 26 | Staff -> Loans -> Return / +7 days | Copies restored / due date moved |
| 27 | Staff resets a member's password | Member can log in with the new one |
| 28 | Member opens any `admin*.do` URL | 403 page |
| 29 | Import `database/bookverse.sql`, start the app | Footer: "Data store: MySQL database"; 14 titles, 3 plans |
| 30 | Reserve a book, then `SELECT * FROM reservations` | New loan row; `books.available_copies` reduced in the same transaction |
| 31 | Staff edits the Premium plan, restart Tomcat | New values still shown on the Plans page |
| 32 | Add a title with unicode characters (e.g. "Caf\u00e9"), restart | Title still correct (utf8mb4) |
| 33 | Start with a wrong database password | Application still starts, footer says "database unavailable", error in log |

## 6. Future improvements (for the report)
A connection pool (HikariCP / Tomcat JDBC pool) and JPA; CSRF tokens; BCrypt; pagination; waiting lists and
renewals; e-mail notifications; REST/JSON API; i18n through `fmt:message`; unit tests for the service layer;
caching of catalogue queries; moving to Jakarta EE 10 / Tomcat 10.
