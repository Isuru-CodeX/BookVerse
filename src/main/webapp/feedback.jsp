<%-- Public page that is its own form handler:
     jsp:useBean + jsp:setProperty property="*" fill FeedbackBean from the request parameters,
     and on success jsp:forward (with jsp:param) hands the request to FeedbackSubmitCommand. --%>
<jsp:useBean id="fb" class="com.bookverse.bean.FeedbackBean" scope="request"/>
<c:set var="submitted" value="${pageContext.request.method eq 'POST'}"/>
<c:if test="${submitted}">
  <jsp:setProperty name="fb" property="*"/>
  <c:if test="${fb.valid}">
    <jsp:forward page="/feedbackSubmit.do">
      <jsp:param name="source" value="feedback.jsp"/>
    </jsp:forward>
  </c:if>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Feedback"/>
  <jsp:param name="active" value="feedback"/>
</jsp:include>

<div class="narrow-page">
  <t:pageHeader title="Tell us how we are doing" subtitle="Your note goes straight to the library team."/>

  <c:if test="${submitted}">
    <t:alert type="error" message="Please fix the ${fn:length(fb.errors)} highlighted ${fn:length(fb.errors) eq 1 ? 'field' : 'fields'} and send again."/>
  </c:if>

  <form class="form" action="<c:url value='/feedback.jsp'/>" method="post" novalidate>
    <div class="field-row">
      <div class="field">
        <label for="name">Your name</label>
        <input id="name" name="name" required value="<c:out value='${fb.name}'/>">
        <c:if test="${submitted and not empty fb.errors.name}"><span class="field-error"><c:out value="${fb.errors.name}"/></span></c:if>
      </div>
      <div class="field">
        <label for="email">Email address</label>
        <input id="email" name="email" type="email" required value="<c:out value='${fb.email}'/>">
        <c:if test="${submitted and not empty fb.errors.email}"><span class="field-error"><c:out value="${fb.errors.email}"/></span></c:if>
      </div>
    </div>
    <fieldset class="field">
      <legend>Overall rating</legend>
      <div class="choice-row">
        <c:forEach begin="1" end="5" var="n">
          <label class="choice"><input type="radio" name="rating" value="${n}" ${fb.ratingValue eq n ? 'checked' : ''}><span>${n}</span></label>
        </c:forEach>
      </div>
      <c:if test="${submitted and not empty fb.errors.rating}"><span class="field-error"><c:out value="${fb.errors.rating}"/></span></c:if>
    </fieldset>
    <div class="field">
      <label for="comments">Comments</label>
      <textarea id="comments" name="comments" rows="5" maxlength="500" required><c:out value="${fb.comments}"/></textarea>
      <c:if test="${submitted and not empty fb.errors.comments}"><span class="field-error"><c:out value="${fb.errors.comments}"/></span></c:if>
    </div>
    <button class="btn" type="submit">Send feedback</button>
  </form>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
