<%-- View: staff overview. Model data: stats, overdue, soldOut, announcements --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Staff overview"/>
  <jsp:param name="active" value="admin"/>
</jsp:include>
<t:adminNav active="overview"/>
<t:pageHeader title="Staff overview" subtitle="Everything that needs attention today.">
  <a class="btn" href="<c:url value='/adminBook.do'/>">Add a title</a>
</t:pageHeader>

<div class="stat-row">
  <div class="stat"><strong>${stats.titles}</strong><span>titles</span></div>
  <div class="stat"><strong>${stats.availableCopies} / ${stats.copies}</strong><span>copies on shelf</span></div>
  <div class="stat"><strong>${stats.activeLoans}</strong><span>active loans</span></div>
  <div class="stat ${stats.overdueLoans gt 0 ? 'stat-warn' : ''}"><strong>${stats.overdueLoans}</strong><span>overdue</span></div>
  <div class="stat"><strong>${stats.members}</strong><span>accounts</span></div>
  <div class="stat"><strong>${stats.feedbackCount}</strong><span>feedback notes</span></div>
</div>

<div class="quick-links">
  <a href="<c:url value='/adminBooks.do'/>"><strong>Manage books</strong><span>Edit, restock or remove titles</span></a>
  <a href="<c:url value='/adminMembers.do'/>"><strong>Manage members</strong><span>Plans, roles, suspensions, passwords</span></a>
  <a href="<c:url value='/adminLoans.do'/>"><strong>Manage loans</strong><span>Force returns and extend due dates</span></a>
  <a href="<c:url value='/adminPlans.do'/>"><strong>Membership plans</strong><span>Loan limits, periods, renewals, fees</span></a>
</div>

<div class="two-col">
  <t:card title="Overdue loans">
    <c:choose>
      <c:when test="${empty overdue}"><p class="empty">Nothing is overdue.</p></c:when>
      <c:otherwise>
        <div class="table-wrap"><table class="table">
          <thead><tr><th>Title</th><th>Member</th><th>Due</th><th><span class="sr-only">Actions</span></th></tr></thead>
          <tbody>
            <c:forEach var="r" items="${overdue}">
              <tr>
                <td><c:out value="${r.bookTitle}"/></td>
                <td><c:out value="${r.memberEmail}"/></td>
                <td><fmt:formatDate value="${r.dueDate}" dateStyle="medium"/></td>
                <td class="right actions">
                  <form action="<c:url value='/adminLoanAction.do'/>" method="post"><input type="hidden" name="id" value="${r.id}"><input type="hidden" name="action" value="extend"><button class="btn btn-small btn-quiet" type="submit">+7 days</button></form>
                  <form action="<c:url value='/adminLoanAction.do'/>" method="post"><input type="hidden" name="id" value="${r.id}"><input type="hidden" name="action" value="return"><button class="btn btn-small" type="submit">Mark returned</button></form>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table></div>
      </c:otherwise>
    </c:choose>
  </t:card>

  <div class="stack narrow">
    <t:card title="Sold-out titles">
      <c:choose>
        <c:when test="${empty soldOut}"><p class="empty">Every title has a copy on the shelf.</p></c:when>
        <c:otherwise>
          <ul class="plain">
            <c:forEach var="b" items="${soldOut}">
              <li><a href="<c:url value='/adminBook.do'><c:param name='id' value='${b.id}'/></c:url>"><c:out value="${b.title}"/></a> <span class="muted">(${b.totalCopies} total)</span></li>
            </c:forEach>
          </ul>
        </c:otherwise>
      </c:choose>
    </t:card>
    <t:card title="Latest announcements">
      <c:forEach var="a" items="${announcements}">
        <article class="news"><h3><c:out value="${a.title}"/></h3><small class="muted"><fmt:formatDate value="${a.postedOn}" dateStyle="medium"/></small></article>
      </c:forEach>
      <a href="<c:url value='/adminAnnouncements.do'/>">Manage announcements</a>
    </t:card>
  </div>
</div>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
