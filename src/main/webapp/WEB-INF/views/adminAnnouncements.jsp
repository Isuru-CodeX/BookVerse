<%-- View: manage announcements. Model data: announcements --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Announcements"/>
  <jsp:param name="active" value="admin"/>
</jsp:include>
<t:adminNav active="announcements"/>
<t:pageHeader title="Announcements" subtitle="The three newest appear on the home page."/>

<div class="two-col">
  <t:card title="All announcements">
    <c:choose>
      <c:when test="${empty announcements}"><p class="empty">No announcements yet.</p></c:when>
      <c:otherwise>
        <c:forEach var="a" items="${announcements}" varStatus="st">
          <article class="news row-between">
            <div>
              <h3><c:out value="${a.title}"/> <c:if test="${st.index lt 3}"><span class="badge badge-ok">On home page</span></c:if></h3>
              <p><c:out value="${a.message}"/></p>
              <small class="muted"><fmt:formatDate value="${a.postedOn}" dateStyle="medium"/></small>
            </div>
            <form action="<c:url value='/adminAnnouncements.do'/>" method="post" onsubmit="return confirm('Delete this announcement?');">
              <input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${a.id}">
              <button class="btn btn-small btn-danger" type="submit">Delete</button>
            </form>
          </article>
        </c:forEach>
      </c:otherwise>
    </c:choose>
  </t:card>
  <t:card title="Post an announcement" cssClass="narrow">
    <form class="form flat" action="<c:url value='/adminAnnouncements.do'/>" method="post">
      <input type="hidden" name="action" value="add">
      <div class="field"><label for="a-title">Title</label><input id="a-title" name="title" maxlength="80" required></div>
      <div class="field"><label for="a-msg">Message</label><textarea id="a-msg" name="message" rows="4" maxlength="300" required></textarea></div>
      <button class="btn" type="submit">Publish</button>
    </form>
  </t:card>
</div>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
