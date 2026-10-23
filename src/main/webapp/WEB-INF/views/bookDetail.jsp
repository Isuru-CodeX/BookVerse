<%-- View: one title. Model data: book, related (request scope) --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="${book.title}"/>
  <jsp:param name="active" value="catalog"/>
</jsp:include>

<%-- Page-scope bean configured from the model: loan length decides the due date --%>
<jsp:useBean id="loanPolicy" class="com.bookverse.bean.LoanPolicy" scope="page"/>
<jsp:setProperty name="loanPolicy" property="loanDays" value="${loanDays}"/>

<nav class="crumbs" aria-label="Breadcrumb">
  <a href="<c:url value='/catalog.do'/>">Catalogue</a> / <c:out value="${book.category}"/>
</nav>

<article class="detail">
  <div class="cover cover-large" style="--hue:${(book.id * 47) mod 360}" aria-hidden="true">
    <span><c:out value="${book.initials}"/></span>
    <small><c:out value="${book.category}"/></small>
  </div>
  <div class="detail-body">
    <h1><c:out value="${book.title}"/></h1>
    <p class="by-line">by <c:out value="${book.author}"/> &middot; ${book.year}</p>
    <div class="book-foot">
      <bv:rating value="${book.rating}"/>
      <bv:availability copies="${book.availableCopies}" lowStock="1"/>
    </div>
    <p class="description"><c:out value="${book.description}"/></p>
    <dl class="facts">
      <div><dt>ISBN / ISSN</dt><dd><c:out value="${book.isbn}"/></dd></div>
      <div><dt>Copies</dt><dd>${book.availableCopies} of ${book.totalCopies} on the shelf</dd></div>
      <div><dt>Loan period</dt><dd><jsp:getProperty name="loanPolicy" property="loanDays"/> days on the ${plan.label} plan, due <fmt:formatDate value="${loanPolicy.dueDate}" dateStyle="medium"/> if borrowed today</dd></div>
    </dl>

    <c:choose>
      <c:when test="${empty sessionScope.member}">
        <a class="btn" href="<c:url value='/login.do'><c:param name='redirect' value='/bookDetail.do?id=${book.id}'/></c:url>">Sign in to reserve</a>
      </c:when>
      <c:when test="${not book.available}">
        <button class="btn" type="button" disabled>All copies are on loan</button>
      </c:when>
      <c:otherwise>
        <form action="<c:url value='/reserve.do'/>" method="post">
          <input type="hidden" name="bookId" value="${book.id}">
          <button class="btn" type="submit">Reserve this title</button>
        </form>
      </c:otherwise>
    </c:choose>
  </div>
</article>

<c:if test="${not empty related}">
  <t:card title="More in ${book.category}">
    <div class="book-grid">
      <c:forEach var="r" items="${related}"><t:bookcard book="${r}"/></c:forEach>
    </div>
  </t:card>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
