<%-- View: public membership plan comparison. Model data: plans (request scope) --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Membership plans"/>
  <jsp:param name="active" value="plans"/>
</jsp:include>

<t:pageHeader title="Membership plans" subtitle="Pick the plan that fits how you read. Staff can adjust these rules at any time."/>

<div class="plan-grid">
  <c:forEach var="p" items="${plans}">
    <c:set var="mine" value="${not empty sessionScope.member and sessionScope.member.membershipType eq p.type}"/>
    <section class="panel plan-card plan-${fn:toLowerCase(p.type)}">
      <div class="panel-head">
        <h2>${p.label} <c:if test="${mine}"><span class="badge badge-ok">Your plan</span></c:if></h2>
        <p class="muted"><c:out value="${p.description}"/></p>
        <p class="price"><c:choose><c:when test="${p.monthlyFee eq 0}">Free</c:when><c:otherwise>Rs. <fmt:formatNumber value="${p.monthlyFee}" pattern="#,##0"/><small> / month</small></c:otherwise></c:choose></p>
      </div>
      <div class="panel-body">
        <ul class="plain checks">
          <li><strong>${p.maxLoans}</strong> active loans at once</li>
          <li><strong>${p.loanDays}</strong> days for books</li>
          <li><strong>${p.shortLoanDays}</strong> days for journals and magazines</li>
          <li><strong>${p.maxRenewals}</strong> ${p.maxRenewals eq 1 ? 'renewal' : 'renewals'} per loan</li>
        </ul>
      </div>
    </section>
  </c:forEach>
</div>

<c:choose>
  <c:when test="${empty sessionScope.member}">
    <p><a class="btn" href="<c:url value='/register.do'/>">Join the library</a> <a class="btn btn-quiet" href="<c:url value='/login.do'/>">Sign in</a></p>
  </c:when>
  <c:otherwise>
    <p><a class="btn" href="<c:url value='/dashboard.do'/>">Manage my plan</a></p>
  </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
