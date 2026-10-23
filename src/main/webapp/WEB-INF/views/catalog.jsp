<%-- View: catalogue with search form. Bean "criteria" is created by CatalogCommand (request scope) --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Catalogue"/>
  <jsp:param name="active" value="catalog"/>
</jsp:include>
<jsp:useBean id="criteria" class="com.bookverse.bean.SearchCriteria" scope="request"/>

<t:pageHeader title="Catalogue" subtitle="Search, filter and sort the whole collection."/>

<form class="filter-bar" action="<c:url value='/catalog.do'/>" method="get">
  <div class="field grow">
    <label for="keyword">Title or author</label>
    <input id="keyword" type="search" name="keyword" value="<c:out value='${criteria.keyword}'/>">
  </div>
  <div class="field">
    <label for="category">Category</label>
    <select id="category" name="category">
      <option value="">All categories</option>
      <c:forEach var="cat" items="${categories}">
        <option value="${fn:escapeXml(cat)}" ${criteria.category eq cat ? 'selected' : ''}><c:out value="${cat}"/></option>
      </c:forEach>
    </select>
  </div>
  <div class="field">
    <label for="sort">Sort by</label>
    <select id="sort" name="sort">
      <option value="title"  ${criteria.sort eq 'title'  ? 'selected' : ''}>Title (A-Z)</option>
      <option value="rating" ${criteria.sort eq 'rating' ? 'selected' : ''}>Highest rated</option>
      <option value="year"   ${criteria.sort eq 'year'   ? 'selected' : ''}>Newest first</option>
    </select>
  </div>
  <label class="check"><input type="checkbox" name="availableOnly" value="true" ${criteria.availableOnly ? 'checked' : ''}> Available now</label>
  <button class="btn" type="submit">Apply</button>
  <c:if test="${criteria.filtered}"><a class="btn btn-quiet" href="<c:url value='/catalog.do'/>">Clear</a></c:if>
</form>

<p class="result-count" aria-live="polite">
  <strong>${fn:length(books)}</strong> ${fn:length(books) eq 1 ? 'title' : 'titles'} found,
  sorted by <jsp:getProperty name="criteria" property="sort"/>.
</p>

<c:choose>
  <c:when test="${empty books}">
    <t:alert type="info" message="No titles match those filters. Try a shorter keyword or clear the filters."/>
  </c:when>
  <c:otherwise>
    <div class="book-grid">
      <c:forEach var="b" items="${books}">
        <t:bookcard book="${b}" keyword="${criteria.keyword}"/>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
