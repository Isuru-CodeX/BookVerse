<%-- View: member dashboard. Session bean "member"; request data: reservations, activeCount, plan, plans, maxLoans --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="My loans"/>
  <jsp:param name="active" value="dashboard"/>
</jsp:include>

<%-- Reuse the Member bean that LoginCommand stored in session scope.
     Name/e-mail are regex-validated at registration, so jsp:getProperty output is safe here. --%>
<jsp:useBean id="member" class="com.bookverse.model.Member" scope="session"/>
<c:set var="slotsLeft" value="${maxLoans - activeCount}" scope="page"/>
<c:set var="historyCount" value="${fn:length(reservations) - activeCount}" scope="page"/>

<t:pageHeader title="My loans" subtitle="Everything you have borrowed, in one place.">
  <a class="btn" href="<c:url value='/catalog.do'><c:param name='availableOnly' value='true'/></c:url>">Browse available titles</a>
</t:pageHeader>

<div class="two-col">
  <t:card title="Member card" cssClass="narrow">
    <dl class="facts">
      <div><dt>Name</dt><dd><jsp:getProperty name="member" property="name"/></dd></div>
      <div><dt>Email</dt><dd><jsp:getProperty name="member" property="email"/></dd></div>
      <div><dt>Plan</dt><dd><span class="badge badge-plan plan-${fn:toLowerCase(member.membershipType)}">${plan.label}</span></dd></div>
      <div><dt>Joined</dt><dd><fmt:formatDate value="${member.joinDate}" dateStyle="medium"/></dd></div>
      <div><dt>Loan slots</dt><dd>${activeCount} used, ${slotsLeft} free (limit ${maxLoans})</dd></div>
      <div><dt>Loan period</dt><dd>${plan.loanDays} days (books), ${plan.shortLoanDays} days (journals)</dd></div>
      <div><dt>Renewals</dt><dd>${plan.maxRenewals} per loan</dd></div>
    </dl>
    <form class="plan-switch" action="<c:url value='/changePlan.do'/>" method="post">
      <label for="switch-plan" class="sr-only">Switch plan</label>
      <select id="switch-plan" name="membershipType">
        <c:forEach var="p" items="${plans}">
          <c:if test="${p.type ne 'STUDENT'}"><option value="${p.type}" ${member.membershipType eq p.type ? 'selected' : ''}>${p.label} (${p.maxLoans} loans, ${p.loanDays} days)</option></c:if>
        </c:forEach>
      </select>
      <button class="btn btn-small" type="submit">Switch plan</button>
    </form>
    <p class="muted small">The Student plan is granted by library staff.</p>
  </t:card>

  <t:card title="On loan now">
    <c:choose>
      <c:when test="${activeCount eq 0}">
        <p class="empty">You have nothing on loan. <a href="<c:url value='/catalog.do'/>">Pick something from the catalogue</a>.</p>
      </c:when>
      <c:otherwise>
        <div class="table-wrap"><table class="table">
          <thead><tr><th>Title</th><th>Borrowed</th><th>Due</th><th>Renewed</th><th><span class="sr-only">Actions</span></th></tr></thead>
          <tbody>
            <c:forEach var="r" items="${reservations}">
              <c:if test="${r.active}">
                <tr>
                  <td><a href="<c:url value='/bookDetail.do'><c:param name='id' value='${r.bookId}'/></c:url>"><c:out value="${r.bookTitle}"/></a></td>
                  <td><fmt:formatDate value="${r.reservedOn}" dateStyle="medium"/></td>
                  <td>
                    <fmt:formatDate value="${r.dueDate}" dateStyle="medium"/>
                    <c:if test="${r.overdue}"><span class="badge badge-out">Overdue</span></c:if>
                  </td>
                  <td>${r.renewals} / ${plan.maxRenewals}</td>
                  <td class="right actions">
                    <c:if test="${r.renewals lt plan.maxRenewals and not r.overdue}">
                      <form action="<c:url value='/renew.do'/>" method="post">
                        <input type="hidden" name="reservationId" value="${r.id}">
                        <button class="btn btn-small btn-quiet" type="submit">Renew</button>
                      </form>
                    </c:if>
                    <form action="<c:url value='/return.do'/>" method="post">
                      <input type="hidden" name="reservationId" value="${r.id}">
                      <button class="btn btn-small" type="submit">Return</button>
                    </form>
                  </td>
                </tr>
              </c:if>
            </c:forEach>
          </tbody>
        </table></div>
      </c:otherwise>
    </c:choose>
  </t:card>
</div>

<t:card title="Reading history">
  <c:choose>
    <c:when test="${historyCount le 0}"><p class="empty">Returned titles will show up here.</p></c:when>
    <c:otherwise>
      <table class="table">
        <thead><tr><th>Title</th><th>Borrowed</th><th>Returned</th></tr></thead>
        <tbody>
          <c:forEach var="r" items="${reservations}">
            <c:if test="${not r.active}">
              <tr>
                <td><c:out value="${r.bookTitle}"/></td>
                <td><fmt:formatDate value="${r.reservedOn}" dateStyle="medium"/></td>
                <td><fmt:formatDate value="${r.returnedOn}" dateStyle="medium"/></td>
              </tr>
            </c:if>
          </c:forEach>
        </tbody>
      </table>
    </c:otherwise>
  </c:choose>
</t:card>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
