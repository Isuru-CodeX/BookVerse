<%-- View: member feedback. Model data: feedbackList, report --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Feedback"/>
  <jsp:param name="active" value="admin"/>
</jsp:include>
<t:adminNav active="feedback"/>
<t:pageHeader title="Member feedback" subtitle="${fn:length(feedbackList)} notes received.">
  <c:if test="${not empty feedbackList}"><span class="muted">Average rating</span> <bv:rating value="${report.averageFeedback}"/></c:if>
</t:pageHeader>

<c:choose>
  <c:when test="${empty feedbackList}"><t:alert type="info" message="No feedback yet. It arrives from the public Feedback page."/></c:when>
  <c:otherwise>
    <c:forEach var="f" items="${feedbackList}">
      <t:card>
        <div class="row-between">
          <div>
            <strong><c:out value="${f.name}"/></strong> <span class="muted">&lt;<c:out value="${f.email}"/>&gt;</span>
            <div><bv:rating value="${f.ratingValue}"/> <small class="muted"><fmt:formatDate value="${f.submittedOn}" dateStyle="medium" timeStyle="short" type="both"/></small></div>
            <p class="feedback-text"><c:out value="${f.comments}"/></p>
          </div>
          <form action="<c:url value='/adminFeedback.do'/>" method="post" onsubmit="return confirm('Delete this feedback?');">
            <input type="hidden" name="id" value="${f.id}"><button class="btn btn-small btn-danger" type="submit">Delete</button>
          </form>
        </div>
      </t:card>
    </c:forEach>
  </c:otherwise>
</c:choose>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
