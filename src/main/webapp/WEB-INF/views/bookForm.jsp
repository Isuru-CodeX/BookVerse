<%-- View: add / edit a title (staff). Model data: book, categories, errors. book.id > 0 means editing. --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Title form"/>
  <jsp:param name="active" value="admin"/>
</jsp:include>
<c:set var="editing" value="${book.id gt 0}"/>
<t:adminNav active="books"/>
<div class="narrow-page">
  <t:pageHeader title="${editing ? 'Edit title' : 'Add a title'}" subtitle="${editing ? 'Changes are visible in the catalogue immediately.' : 'New titles appear in the catalogue immediately.'}"/>

  <c:if test="${not empty errors}">
    <t:alert type="error" message="Please fix the ${fn:length(errors)} highlighted ${fn:length(errors) eq 1 ? 'field' : 'fields'}."/>
  </c:if>
  <c:if test="${editing}">
    <p class="muted">${book.totalCopies - book.availableCopies} of ${book.totalCopies} copies are on loan right now.</p>
  </c:if>

  <form class="form" action="<c:url value='/adminBook.do'/>" method="post" novalidate>
    <input type="hidden" name="id" value="${book.id}">
    <div class="field">
      <label for="title">Title</label>
      <input id="title" name="title" maxlength="120" required value="<c:out value='${book.title}'/>">
      <c:if test="${not empty errors.title}"><span class="field-error"><c:out value="${errors.title}"/></span></c:if>
    </div>
    <div class="field">
      <label for="author">Author or publisher</label>
      <input id="author" name="author" maxlength="120" required value="<c:out value='${book.author}'/>">
      <c:if test="${not empty errors.author}"><span class="field-error"><c:out value="${errors.author}"/></span></c:if>
    </div>
    <div class="field-row">
      <div class="field">
        <label for="category">Category</label>
        <input id="category" name="category" list="category-list" maxlength="30" required value="<c:out value='${book.category}'/>">
        <datalist id="category-list"><c:forEach var="cat" items="${categories}"><option value="${fn:escapeXml(cat)}"></option></c:forEach></datalist>
        <c:if test="${not empty errors.category}"><span class="field-error"><c:out value="${errors.category}"/></span></c:if>
      </div>
      <div class="field">
        <label for="isbn">ISBN / ISSN (optional)</label>
        <input id="isbn" name="isbn" maxlength="30" value="<c:out value='${book.isbn}'/>">
      </div>
    </div>
    <div class="field-row">
      <div class="field">
        <label for="year">Year</label>
        <input id="year" name="year" type="number" required value="${book.year}">
        <c:if test="${not empty errors.year}"><span class="field-error"><c:out value="${errors.year}"/></span></c:if>
      </div>
      <div class="field">
        <label for="totalCopies">Total copies</label>
        <input id="totalCopies" name="totalCopies" type="number" min="1" max="50" required value="${book.totalCopies}">
        <c:if test="${not empty errors.totalCopies}"><span class="field-error"><c:out value="${errors.totalCopies}"/></span></c:if>
      </div>
    </div>
    <div class="field">
      <label for="rating">Editorial rating (0 = not rated)</label>
      <input id="rating" name="rating" type="number" min="0" max="5" step="0.1" value="${book.rating}">
      <c:if test="${not empty errors.rating}"><span class="field-error"><c:out value="${errors.rating}"/></span></c:if>
    </div>
    <div class="field">
      <label for="description">Description</label>
      <textarea id="description" name="description" rows="4" maxlength="400"><c:out value="${book.description}"/></textarea>
      <c:if test="${not empty errors.description}"><span class="field-error"><c:out value="${errors.description}"/></span></c:if>
    </div>
    <div class="btn-row">
      <button class="btn" type="submit">${editing ? 'Save changes' : 'Add to catalogue'}</button>
      <a class="btn btn-quiet" href="<c:url value='/adminBooks.do'/>">Cancel</a>
    </div>
  </form>
</div>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
