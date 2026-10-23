<%-- View: staff member list. Model data: members, loanCounts (Map by e-mail), plans, planFilter, q --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Manage members"/>
  <jsp:param name="active" value="admin"/>
</jsp:include>
<t:adminNav active="members"/>
<t:pageHeader title="Members" subtitle="${fn:length(members)} accounts shown."/>

<form class="filter-bar" action="<c:url value='/adminMembers.do'/>" method="get">
  <div class="field grow"><label for="q">Name or email</label><input id="q" type="search" name="q" value="<c:out value='${q}'/>"></div>
  <div class="field"><label for="plan">Plan</label>
    <select id="plan" name="plan"><option value="">All plans</option>
      <c:forEach var="p" items="${plans}"><option value="${p.type}" ${planFilter eq p.type ? 'selected' : ''}>${p.label}</option></c:forEach>
    </select></div>
  <button class="btn" type="submit">Filter</button>
  <c:if test="${not empty planFilter or not empty q}"><a class="btn btn-quiet" href="<c:url value='/adminMembers.do'/>">Clear</a></c:if>
</form>

<t:card>
  <div class="table-wrap"><table class="table">
    <thead><tr><th>Name</th><th>Plan</th><th>Role</th><th>Status</th><th>Joined</th><th>Open loans</th><th><span class="sr-only">Actions</span></th></tr></thead>
    <tbody>
      <c:forEach var="m" items="${members}">
        <%-- collection operator with a computed key: e-mail keys contain '.' and '@', so [] is required --%>
        <c:set var="open" value="${empty loanCounts[fn:toLowerCase(m.email)] ? 0 : loanCounts[fn:toLowerCase(m.email)]}"/>
        <tr>
          <td><c:out value="${m.name}"/><br><small class="muted"><c:out value="${m.email}"/></small></td>
          <td><span class="badge badge-plan plan-${fn:toLowerCase(m.membershipType)}">${fn:substring(m.membershipType, 0, 1)}${fn:toLowerCase(fn:substring(m.membershipType, 1, -1))}</span></td>
          <td>${m.staff ? 'Staff' : 'Member'}</td>
          <td><span class="badge ${m.active ? 'badge-ok' : 'badge-out'}">${m.active ? 'Active' : 'Suspended'}</span></td>
          <td><fmt:formatDate value="${m.joinDate}" dateStyle="medium"/></td>
          <td>${open}</td>
          <td class="right"><a class="btn btn-small btn-quiet" href="<c:url value='/adminMember.do'><c:param name='id' value='${m.id}'/></c:url>">Manage</a></td>
        </tr>
      </c:forEach>
      <c:if test="${empty members}"><tr><td colspan="7" class="empty">No accounts match.</td></tr></c:if>
    </tbody>
  </table></div>
</t:card>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
