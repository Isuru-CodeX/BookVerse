<%-- View shown after FeedbackSubmitCommand. Bean "fb" is still in request scope after the forward. --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="Thank you"/>
  <jsp:param name="active" value="feedback"/>
</jsp:include>
<jsp:useBean id="fb" class="com.bookverse.bean.FeedbackBean" scope="request"/>

<div class="narrow-page">
  <t:pageHeader title="Thank you, ${fb.name}" subtitle="We read every note."/>
  <t:card>
    <p>You rated the portal <strong><jsp:getProperty name="fb" property="ratingValue"/> out of 5</strong>:</p>
    <p><bv:rating value="${fb.ratingValue}"/></p>
    <p class="muted">Submitted from <c:out value="${param.source}"/> on <fmt:formatDate value="${fb.submittedOn}" dateStyle="medium" timeStyle="short" type="both"/>.</p>
    <a class="btn" href="<c:url value='/home.do'/>">Back to home</a>
  </t:card>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
