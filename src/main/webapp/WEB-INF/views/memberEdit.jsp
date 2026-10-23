<%-- View: manage one member. Model data: target, plans, loans, isSelf --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Manage member"/>
  <jsp:param name="active" value="admin"/>
</jsp:include>
<t:adminNav active="members"/>
<nav class="crumbs"><a href="<c:url value='/adminMembers.do'/>">Members</a> / <c:out value="${target.name}"/></nav>
<t:pageHeader title="${target.name}" subtitle="${target.email}"/>

<div class="two-col">
  <t:card title="Account settings">
    <form class="form flat" action="<c:url value='/adminMember.do'><c:param name='id' value='${target.id}'/></c:url>" method="post" novalidate>
      <input type="hidden" name="action" value="save">
      <div class="field"><label for="name">Full name</label><input id="name" name="name" value="<c:out value='${target.name}'/>" required></div>
      <div class="field">
        <label for="membershipType">Membership plan</label>
        <select id="membershipType" name="membershipType">
          <c:forEach var="p" items="${plans}">
            <option value="${p.type}" ${target.membershipType eq p.type ? 'selected' : ''}>${p.label} - ${p.maxLoans} loans, ${p.loanDays} days</option>
          </c:forEach>
        </select>
      </div>
      <c:choose>
        <c:when test="${isSelf}">
          <%-- an admin cannot demote or suspend their own account; keep the current values --%>
          <input type="hidden" name="role" value="${target.role}"><input type="hidden" name="active" value="true">
          <p class="muted">This is your own account, so role and status are locked.</p>
        </c:when>
        <c:otherwise>
          <div class="field-row">
            <div class="field"><label for="role">Role</label>
              <select id="role" name="role"><option value="MEMBER" ${target.staff ? '' : 'selected'}>Member</option><option value="STAFF" ${target.staff ? 'selected' : ''}>Staff (admin access)</option></select></div>
            <div class="field"><label for="active">Status</label>
              <select id="active" name="active"><option value="true" ${target.active ? 'selected' : ''}>Active</option><option value="false" ${target.active ? '' : 'selected'}>Suspended</option></select></div>
          </div>
        </c:otherwise>
      </c:choose>
      <button class="btn" type="submit">Save changes</button>
    </form>
  </t:card>

  <div class="stack narrow">
    <t:card title="Reset password">
      <form class="form flat" action="<c:url value='/adminMember.do'><c:param name='id' value='${target.id}'/></c:url>" method="post">
        <input type="hidden" name="action" value="password">
        <div class="field"><label for="newPassword">New password</label><input id="newPassword" name="newPassword" type="password" autocomplete="new-password" minlength="6" required></div>
        <button class="btn btn-quiet" type="submit">Set password</button>
      </form>
    </t:card>
    <t:card title="Account facts">
      <dl class="facts">
        <div><dt>Joined</dt><dd><fmt:formatDate value="${target.joinDate}" dateStyle="medium"/></dd></div>
        <div><dt>Loans</dt><dd>${fn:length(loans)} in total</dd></div>
      </dl>
    </t:card>
  </div>
</div>

<t:card title="Loan history">
  <c:choose>
    <c:when test="${empty loans}"><p class="empty">This member has not borrowed anything yet.</p></c:when>
    <c:otherwise>
      <div class="table-wrap"><table class="table">
        <thead><tr><th>Title</th><th>Borrowed</th><th>Due</th><th>Renewals</th><th>Status</th></tr></thead>
        <tbody>
          <c:forEach var="r" items="${loans}">
            <tr>
              <td><c:out value="${r.bookTitle}"/></td>
              <td><fmt:formatDate value="${r.reservedOn}" dateStyle="medium"/></td>
              <td><fmt:formatDate value="${r.dueDate}" dateStyle="medium"/></td>
              <td>${r.renewals}</td>
              <td><span class="badge ${r.overdue ? 'badge-out' : (r.active ? 'badge-low' : 'badge-ok')}">${r.overdue ? 'Overdue' : (r.active ? 'On loan' : 'Returned')}</span></td>
            </tr>
          </c:forEach>
        </tbody>
      </table></div>
    </c:otherwise>
  </c:choose>
</t:card>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
