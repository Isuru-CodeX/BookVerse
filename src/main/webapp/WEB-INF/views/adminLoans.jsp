<%-- View: staff loan list. Model data: loans, status (all|active|overdue|returned) --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Manage loans"/>
  <jsp:param name="active" value="admin"/>
</jsp:include>
<t:adminNav active="loans"/>
<t:pageHeader title="Loans" subtitle="${fn:length(loans)} records in this view."/>

<div class="seg" aria-label="Loan status filter">
  <c:forTokens items="all,active,overdue,returned" delims="," var="s">
    <a href="<c:url value='/adminLoans.do'><c:param name='status' value='${s}'/></c:url>" class="${status eq s ? 'current' : ''}">${fn:toUpperCase(fn:substring(s, 0, 1))}${fn:substring(s, 1, -1)}</a>
  </c:forTokens>
</div>

<t:card>
  <div class="table-wrap"><table class="table">
    <thead><tr><th>#</th><th>Title</th><th>Member</th><th>Borrowed</th><th>Due</th><th>Renewals</th><th>Status</th><th><span class="sr-only">Actions</span></th></tr></thead>
    <tbody>
      <c:forEach var="r" items="${loans}">
        <tr>
          <td>${r.id}</td>
          <td><c:out value="${r.bookTitle}"/></td>
          <td><c:out value="${r.memberEmail}"/></td>
          <td><fmt:formatDate value="${r.reservedOn}" dateStyle="medium"/></td>
          <td><fmt:formatDate value="${r.dueDate}" dateStyle="medium"/></td>
          <td>${r.renewals}</td>
          <td><span class="badge ${r.overdue ? 'badge-out' : (r.active ? 'badge-low' : 'badge-ok')}">${r.overdue ? 'Overdue' : (r.active ? 'On loan' : 'Returned')}</span></td>
          <td class="right actions">
            <c:if test="${r.active}">
              <form action="<c:url value='/adminLoanAction.do'/>" method="post"><input type="hidden" name="id" value="${r.id}"><input type="hidden" name="status" value="${status}"><input type="hidden" name="action" value="extend"><button class="btn btn-small btn-quiet" type="submit">+7 days</button></form>
              <form action="<c:url value='/adminLoanAction.do'/>" method="post"><input type="hidden" name="id" value="${r.id}"><input type="hidden" name="status" value="${status}"><input type="hidden" name="action" value="return"><button class="btn btn-small" type="submit">Return</button></form>
            </c:if>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty loans}"><tr><td colspan="8" class="empty">No loans in this view.</td></tr></c:if>
    </tbody>
  </table></div>
</t:card>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
