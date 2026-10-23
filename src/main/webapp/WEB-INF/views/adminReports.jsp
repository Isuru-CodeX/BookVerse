<%-- View: reports. Model data: report (Maps of label -> number, plus max* values), stats --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Reports"/>
  <jsp:param name="active" value="admin"/>
</jsp:include>
<t:adminNav active="reports"/>
<t:pageHeader title="Reports" subtitle="A quick look at how the library is used."/>

<div class="two-col eq">
  <t:card title="Most borrowed titles">
    <c:forEach var="e" items="${report.loansPerTitle}">
      <div class="bar-row"><span class="bar-label"><c:out value="${e.key}"/></span><span class="bar"><i style="width:${(e.value * 100) div report.maxTitleLoans}%"></i></span><span class="bar-num">${e.value}</span></div>
    </c:forEach>
  </t:card>
  <t:card title="Loans by status">
    <c:forEach var="e" items="${report.loansByStatus}">
      <div class="bar-row"><span class="bar-label">${e.key}</span><span class="bar"><i style="width:${(e.value * 100) div report.maxStatus}%"></i></span><span class="bar-num">${e.value}</span></div>
    </c:forEach>
  </t:card>
  <t:card title="Members by plan">
    <c:forEach var="e" items="${report.membersByPlan}">
      <div class="bar-row"><span class="bar-label">${e.key}</span><span class="bar"><i style="width:${(e.value * 100) div report.maxPlanMembers}%"></i></span><span class="bar-num">${e.value}</span></div>
    </c:forEach>
  </t:card>
  <t:card title="Copies by category">
    <c:forEach var="e" items="${report.copiesByCategory}">
      <div class="bar-row"><span class="bar-label"><c:out value="${e.key}"/></span><span class="bar"><i style="width:${(e.value * 100) div report.maxCategoryCopies}%"></i></span><span class="bar-num">${e.value}</span></div>
    </c:forEach>
  </t:card>
</div>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
