<%-- View: landing page. Model data: featured, newest, announcements, stats, lastVisit (request scope) --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Home"/>
  <jsp:param name="active" value="home"/>
</jsp:include>

<section class="hero">
  <div class="hero-copy">
    <h1>Find your next read, reserve it in two clicks.</h1>
    <p class="lede">${stats.titles} titles and ${stats.availableCopies} copies on the shelf right now, across books, journals and magazines.</p>
    <form class="hero-search" action="<c:url value='/catalog.do'/>" method="get" role="search">
      <label class="sr-only" for="hero-keyword">Search by title or author</label>
      <input id="hero-keyword" type="search" name="keyword" placeholder="Search by title or author">
      <button class="btn" type="submit">Search catalogue</button>
    </form>
    <p class="hero-note">
      <c:choose>
        <c:when test="${not empty sessionScope.member}">
          Signed in as <strong><c:out value="${sessionScope.member.name}"/></strong>.
          <a href="<c:url value='/dashboard.do'/>">See your loans</a>.
        </c:when>
        <c:otherwise>
          New here? <a href="<c:url value='/register.do'/>">Create a free account</a> to reserve titles.
        </c:otherwise>
      </c:choose>
      <c:if test="${not empty lastVisit}">
        Your last visit was <fmt:formatDate value="${lastVisit}" pattern="d MMM yyyy 'at' HH:mm"/>.
      </c:if>
    </p>
  </div>
  <aside class="hero-shelf" aria-label="Top rated titles">
    <c:forEach var="b" items="${featured}" varStatus="st">
      <a class="spine" style="--hue:${(b.id * 47) mod 360}; --h:${150 - st.index * 12}px" href="<c:url value='/bookDetail.do'><c:param name='id' value='${b.id}'/></c:url>" title="<c:out value='${b.title}'/>">
        <span><c:out value="${b.title}"/></span>
      </a>
    </c:forEach>
  </aside>
</section>

<div class="two-col">
  <t:card title="Top rated">
    <div class="book-grid">
      <c:forEach var="b" items="${featured}"><t:bookcard book="${b}"/></c:forEach>
    </div>
  </t:card>

  <t:card cssClass="narrow">
    <jsp:attribute name="heading"><h2>Announcements</h2></jsp:attribute>
    <jsp:body>
      <c:forEach var="a" items="${announcements}">
        <article class="news">
          <h3><c:out value="${a.title}"/></h3>
          <p><c:out value="${a.message}"/></p>
          <small class="muted"><fmt:formatDate value="${a.postedOn}" dateStyle="medium"/></small>
        </article>
      </c:forEach>
    </jsp:body>
  </t:card>
</div>

<t:card title="Recently added">
  <div class="book-grid">
    <c:forEach var="b" items="${newest}"><t:bookcard book="${b}"/></c:forEach>
  </div>
</t:card>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
