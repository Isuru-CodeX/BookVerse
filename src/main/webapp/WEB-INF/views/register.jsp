<%-- View: registration. "form" is supplied by RegisterCommand after a failed validation. --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Join the library"/>
  <jsp:param name="active" value="login"/>
</jsp:include>

<%-- Body of jsp:useBean only runs when the bean is newly created (first visit) --%>
<jsp:useBean id="form" class="com.bookverse.bean.RegistrationForm" scope="request">
  <jsp:setProperty name="form" property="membershipType" value="STANDARD"/>
</jsp:useBean>

<div class="narrow-page">
  <t:pageHeader title="Join the library" subtitle="Free to register. Choose the plan that suits you; you can switch later."/>

  <c:if test="${not empty errors}">
    <t:alert type="error" message="Please fix the ${fn:length(errors)} highlighted ${fn:length(errors) eq 1 ? 'field' : 'fields'} and try again."/>
  </c:if>

  <form class="form" action="<c:url value='/register.do'/>" method="post" novalidate>
    <div class="field">
      <label for="name">Full name</label>
      <input id="name" name="name" type="text" autocomplete="name" required value="<c:out value='${form.name}'/>">
      <c:if test="${not empty errors.name}"><span class="field-error"><c:out value="${errors.name}"/></span></c:if>
    </div>
    <div class="field">
      <label for="email">Email address</label>
      <input id="email" name="email" type="email" autocomplete="email" required value="<c:out value='${form.email}'/>">
      <c:if test="${not empty errors.email}"><span class="field-error"><c:out value="${errors.email}"/></span></c:if>
    </div>
    <div class="field-row">
      <div class="field">
        <label for="password">Password</label>
        <input id="password" name="password" type="password" autocomplete="new-password" required>
        <c:if test="${not empty errors.password}"><span class="field-error"><c:out value="${errors.password}"/></span></c:if>
      </div>
      <div class="field">
        <label for="confirmPassword">Confirm password</label>
        <input id="confirmPassword" name="confirmPassword" type="password" autocomplete="new-password" required>
        <c:if test="${not empty errors.confirmPassword}"><span class="field-error"><c:out value="${errors.confirmPassword}"/></span></c:if>
      </div>
    </div>
    <fieldset class="field">
      <legend>Membership type</legend>
      <div class="plan-choices">
        <c:forEach var="p" items="${plans}">
          <label class="plan-option">
            <input type="radio" name="membershipType" value="${p.type}" ${form.membershipType eq p.type ? 'checked' : ''}>
            <span><strong>${p.label}</strong>
              <small>${p.maxLoans} loans &middot; ${p.loanDays}-day loans &middot; ${p.maxRenewals} ${p.maxRenewals eq 1 ? 'renewal' : 'renewals'} &middot;
                <c:choose><c:when test="${p.monthlyFee eq 0}">free</c:when><c:otherwise>Rs. <fmt:formatNumber value="${p.monthlyFee}" pattern="#,##0"/>/month</c:otherwise></c:choose></small></span>
          </label>
        </c:forEach>
      </div>
      <c:if test="${not empty errors.membershipType}"><span class="field-error"><c:out value="${errors.membershipType}"/></span></c:if>
    </fieldset>
    <button class="btn" type="submit">Create account</button>
    <p class="muted">Already a member? <a href="<c:url value='/login.do'/>">Sign in</a>.</p>
  </form>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
