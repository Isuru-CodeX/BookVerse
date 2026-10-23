<%-- Central error page referenced by <error-page> in web.xml --%>
<%@ page isErrorPage="true" %>
<c:set var="status" value="${pageContext.errorData.statusCode}"/>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Error ${status}"/>
</jsp:include>

<div class="narrow-page">
  <c:choose>
    <c:when test="${status eq 404}">
      <t:pageHeader title="We could not find that page" subtitle="The address may be mistyped, or the title may have been removed."/>
    </c:when>
    <c:when test="${status eq 403}">
      <t:pageHeader title="This area is for library staff" subtitle="Sign in with a staff account to continue."/>
    </c:when>
    <c:otherwise>
      <t:pageHeader title="Something went wrong on our side" subtitle="The problem has been logged. Please try again in a moment."/>
    </c:otherwise>
  </c:choose>

  <t:card>
    <p class="muted">Status ${status} for <code><c:out value="${pageContext.errorData.requestURI}"/></code></p>
    <div class="btn-row">
      <a class="btn" href="<c:url value='/home.do'/>">Go to home</a>
      <a class="btn btn-quiet" href="<c:url value='/catalog.do'/>">Browse the catalogue</a>
    </div>
  </t:card>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
