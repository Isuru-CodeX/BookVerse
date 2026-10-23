<%-- Shared page opening: <head>, navigation, flash message. Parameters: pageTitle, active --%>
<%-- one formatting locale for numbers and dates on every page, even when the browser sends no Accept-Language --%>
<fmt:setLocale value="en_US" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="user" value="${sessionScope.member}"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${empty param.pageTitle ? 'Welcome' : param.pageTitle}"/> | BookVerse</title>
  <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>
<a class="skip-link" href="#main">Skip to content</a>
<header class="site-header">
  <div class="container nav-bar">
    <a class="brand" href="<c:url value='/home.do'/>"><span class="brand-mark">B</span>BookVerse</a>
    <nav aria-label="Main navigation">
      <a href="<c:url value='/home.do'/>"    class="${param.active eq 'home' ? 'active' : ''}">Home</a>
      <a href="<c:url value='/catalog.do'/>" class="${param.active eq 'catalog' ? 'active' : ''}">Catalogue</a>
      <a href="<c:url value='/plans.do'/>"   class="${param.active eq 'plans' ? 'active' : ''}">Plans</a>
      <a href="<c:url value='/elDemo.do'/>"  class="${param.active eq 'el' ? 'active' : ''}">EL lab</a>
      <a href="<c:url value='/about.do'/>"   class="${param.active eq 'about' ? 'active' : ''}">About</a>
      <a href="<c:url value='/feedback.jsp'/>" class="${param.active eq 'feedback' ? 'active' : ''}">Feedback</a>
    </nav>
    <div class="nav-user">
      <c:choose>
        <c:when test="${not empty user}">
          <a href="<c:url value='/dashboard.do'/>" class="${param.active eq 'dashboard' ? 'active' : ''}">My loans</a>
          <c:if test="${user.staff}"><a href="<c:url value='/admin.do'/>" class="${param.active eq 'admin' ? 'active' : ''}">Staff</a></c:if>
          <a class="btn btn-small btn-quiet" href="<c:url value='/logout.do'/>">Sign out</a>
        </c:when>
        <c:otherwise>
          <a href="<c:url value='/login.do'/>" class="${param.active eq 'login' ? 'active' : ''}">Sign in</a>
          <a class="btn btn-small" href="<c:url value='/register.do'/>">Join</a>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</header>
<main id="main" class="container">
<%-- one-shot flash message stored in session by the controller --%>
<c:if test="${not empty sessionScope.flashMessage}">
  <t:alert type="${sessionScope.flashType}" message="${sessionScope.flashMessage}"/>
  <c:remove var="flashMessage" scope="session"/>
  <c:remove var="flashType" scope="session"/>
</c:if>
