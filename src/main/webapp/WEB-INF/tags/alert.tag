<%@ tag description="Message banner: success, error, info or warning" pageEncoding="UTF-8" body-content="scriptless" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="type" required="false" description="success | error | info | warning (default info)" %>
<%@ attribute name="message" required="false" description="Plain-text message (escaped)" %>
<c:set var="kind" value="${empty type ? 'info' : type}"/>
<div class="alert alert-${kind}" role="${kind eq 'error' ? 'alert' : 'status'}">
  <c:if test="${not empty message}"><span><c:out value="${message}"/></span></c:if>
  <jsp:doBody/>
</div>
