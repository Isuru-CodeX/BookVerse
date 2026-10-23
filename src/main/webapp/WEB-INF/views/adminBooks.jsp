<%-- View: staff book list. Model data: books, categories, criteria --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Manage books"/>
  <jsp:param name="active" value="admin"/>
</jsp:include>
<jsp:useBean id="criteria" class="com.bookverse.bean.SearchCriteria" scope="request"/>
<t:adminNav active="books"/>
<t:pageHeader title="Books" subtitle="${fn:length(books)} titles shown.">
  <a class="btn" href="<c:url value='/adminBook.do'/>">Add a title</a>
</t:pageHeader>

<form class="filter-bar" action="<c:url value='/adminBooks.do'/>" method="get">
  <div class="field grow"><label for="keyword">Title or author</label><input id="keyword" type="search" name="keyword" value="<c:out value='${criteria.keyword}'/>"></div>
  <div class="field"><label for="category">Category</label>
    <select id="category" name="category"><option value="">All categories</option>
      <c:forEach var="cat" items="${categories}"><option value="${fn:escapeXml(cat)}" ${criteria.category eq cat ? 'selected' : ''}><c:out value="${cat}"/></option></c:forEach>
    </select></div>
  <button class="btn" type="submit">Filter</button>
  <c:if test="${criteria.filtered}"><a class="btn btn-quiet" href="<c:url value='/adminBooks.do'/>">Clear</a></c:if>
</form>

<t:card>
  <div class="table-wrap"><table class="table">
    <thead><tr><th>#</th><th>Title</th><th>Category</th><th>Year</th><th>Shelf</th><th>Rating</th><th><span class="sr-only">Actions</span></th></tr></thead>
    <tbody>
      <c:forEach var="b" items="${books}">
        <tr>
          <td>${b.id}</td>
          <td><a href="<c:url value='/bookDetail.do'><c:param name='id' value='${b.id}'/></c:url>"><c:out value="${b.title}"/></a><br><small class="muted"><c:out value="${b.author}"/></small></td>
          <td><c:out value="${b.category}"/></td>
          <td>${b.year}</td>
          <td><bv:availability copies="${b.availableCopies}"/> <span class="muted">of ${b.totalCopies}</span></td>
          <td><bv:rating value="${b.rating}"/></td>
          <td class="right actions">
            <a class="btn btn-small btn-quiet" href="<c:url value='/adminBook.do'><c:param name='id' value='${b.id}'/></c:url>">Edit</a>
            <form action="<c:url value='/adminBookDelete.do'/>" method="post" onsubmit="return confirm('Remove this title from the catalogue?');">
              <input type="hidden" name="id" value="${b.id}"><button class="btn btn-small btn-danger" type="submit">Delete</button>
            </form>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty books}"><tr><td colspan="7" class="empty">No titles match.</td></tr></c:if>
    </tbody>
  </table></div>
</t:card>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
