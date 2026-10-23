<%-- View: sign-in form. "loginForm" may come from LoginCommand (after a failed attempt) or is created here. --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Sign in"/>
  <jsp:param name="active" value="login"/>
</jsp:include>

<jsp:useBean id="loginForm" class="com.bookverse.bean.LoginForm" scope="request"/>
<%-- Copy ?email= and ?redirect= from the request into the bean (jsp:setProperty with param) --%>
<jsp:setProperty name="loginForm" property="email" param="email"/>
<jsp:setProperty name="loginForm" property="redirect" param="redirect"/>

<div class="narrow-page">
  <t:pageHeader title="Sign in" subtitle="Use your BookVerse account to reserve and manage loans."/>

  <c:if test="${not empty errors.login}"><t:alert type="error" message="${errors.login}"/></c:if>

  <form class="form" action="<c:url value='/login.do'/>" method="post" novalidate>
    <input type="hidden" name="redirect" value="<c:out value='${loginForm.redirect}'/>">
    <div class="field">
      <label for="email">Email address</label>
      <input id="email" name="email" type="email" autocomplete="username" required value="<c:out value='${loginForm.email}'/>">
      <c:if test="${not empty errors.email}"><span class="field-error"><c:out value="${errors.email}"/></span></c:if>
    </div>
    <div class="field">
      <label for="password">Password</label>
      <input id="password" name="password" type="password" autocomplete="current-password" required>
      <c:if test="${not empty errors.password}"><span class="field-error"><c:out value="${errors.password}"/></span></c:if>
    </div>
    <button class="btn" type="submit">Sign in</button>
    <p class="muted">No account yet? <a href="<c:url value='/register.do'/>">Join the library</a>.</p>
  </form>

  <t:card title="Demo accounts" cssClass="demo-box">
    <ul class="plain">
      <li><strong>Member:</strong> member@bookverse.lk / member123</li>
      <li><strong>Staff:</strong> admin@bookverse.lk / admin123</li>
    </ul>
  </t:card>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
