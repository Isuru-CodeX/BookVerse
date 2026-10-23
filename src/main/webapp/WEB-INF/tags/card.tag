<%@ tag description="Reusable content panel with an optional header fragment" pageEncoding="UTF-8" body-content="scriptless" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" required="false" description="Plain-text heading" %>
<%@ attribute name="heading" required="false" fragment="true" description="Rich heading supplied with jsp:attribute" %>
<%@ attribute name="cssClass" required="false" description="Extra CSS classes" %>
<section class="panel ${cssClass}">
  <c:if test="${not empty title or not empty heading}">
    <div class="panel-head">
      <c:if test="${not empty title}"><h2><c:out value="${title}"/></h2></c:if>
      <c:if test="${not empty heading}"><jsp:invoke fragment="heading"/></c:if>
    </div>
  </c:if>
  <div class="panel-body"><jsp:doBody/></div>
</section>
