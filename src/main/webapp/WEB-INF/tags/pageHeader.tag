<%@ tag description="Page title block used at the top of every view" pageEncoding="UTF-8" body-content="scriptless" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="subtitle" required="false" %>
<div class="page-header">
  <div>
    <h1><c:out value="${title}"/></h1>
    <c:if test="${not empty subtitle}"><p class="lede"><c:out value="${subtitle}"/></p></c:if>
  </div>
  <div class="page-actions"><jsp:doBody/></div>
</div>
