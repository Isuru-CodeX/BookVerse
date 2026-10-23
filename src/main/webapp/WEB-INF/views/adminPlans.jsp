<%-- View: edit membership plans. Model data: plans, planCounts (Map type -> members) --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Membership plans"/>
  <jsp:param name="active" value="admin"/>
</jsp:include>
<t:adminNav active="plans"/>
<t:pageHeader title="Membership plans" subtitle="These rules drive every reservation and renewal. Changes apply to new loans and renewals straight away."/>

<div class="plan-grid">
  <c:forEach var="p" items="${plans}">
    <section class="panel plan-edit plan-${fn:toLowerCase(p.type)}">
      <div class="panel-head"><h2>${p.label}</h2><p class="muted">${empty planCounts[p.type] ? 0 : planCounts[p.type]} members on this plan</p></div>
      <div class="panel-body">
      <form class="form flat" action="<c:url value='/adminPlans.do'/>" method="post" novalidate>
        <input type="hidden" name="type" value="${p.type}">
        <div class="field"><label for="d-${p.type}">Description</label><input id="d-${p.type}" name="description" maxlength="120" value="<c:out value='${p.description}'/>"></div>
        <div class="field-row">
          <div class="field"><label for="ml-${p.type}">Active loans allowed</label><input id="ml-${p.type}" name="maxLoans" type="number" min="1" max="20" value="${p.maxLoans}"></div>
          <div class="field"><label for="mr-${p.type}">Renewals per loan</label><input id="mr-${p.type}" name="maxRenewals" type="number" min="0" max="5" value="${p.maxRenewals}"></div>
        </div>
        <div class="field-row">
          <div class="field"><label for="ld-${p.type}">Book loan (days)</label><input id="ld-${p.type}" name="loanDays" type="number" min="1" max="90" value="${p.loanDays}"></div>
          <div class="field"><label for="sd-${p.type}">Journal / magazine (days)</label><input id="sd-${p.type}" name="shortLoanDays" type="number" min="1" max="30" value="${p.shortLoanDays}"></div>
        </div>
        <div class="field"><label for="f-${p.type}">Monthly fee (Rs.)</label><input id="f-${p.type}" name="monthlyFee" type="number" min="0" step="1" value="${p.monthlyFee}"></div>
        <button class="btn" type="submit">Save ${p.label} plan</button>
      </form>
      </div>
    </section>
  </c:forEach>
</div>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
