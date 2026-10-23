<%-- View: architecture summary and demo accounts --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="About"/>
  <jsp:param name="active" value="about"/>
</jsp:include>

<t:pageHeader title="About ${initParam.libraryName}" subtitle="One portal for catalogue search, reservations, reading history and announcements."/>

<div class="two-col">
  <t:card title="How the portal is built">
    <ul class="plain">
      <li><strong>MVC:</strong> JSP views, command classes as the controller, JavaBeans as the model.</li>
      <li><strong>Front Controller:</strong> one servlet receives every <code>*.do</code> request.</li>
      <li><strong>Intercepting Filters:</strong> encoding, request logging and access control.</li>
      <li><strong>Business Delegate and Service Locator:</strong> commands never touch the service directly.</li>
      <li><strong>Transfer Objects:</strong> <code>Book</code>, <code>Member</code>, <code>MembershipPlan</code>, <code>Reservation</code>, <code>Announcement</code>.</li>
      <li><strong>Staff area:</strong> books, members, loans, plans, announcements, feedback and reports.</li>
      <li><strong>Custom tags:</strong> <code>bv:rating</code>, <code>bv:availability</code>, <code>bv:highlight</code> and six tag files.</li>
    </ul>
  </t:card>
  <t:card title="Try it" cssClass="narrow">
    <ul class="plain">
      <li>Browse the <a href="<c:url value='/catalog.do'/>">catalogue</a> without an account.</li>
      <li>Sign in as <code>member@bookverse.lk</code> to reserve and return titles.</li>
      <li>Sign in as <code>admin@bookverse.lk</code> for the staff console.</li>
      <li>Open the <a href="<c:url value='/elDemo.do'/>">EL lab</a> to see implicit objects live.</li>
    </ul>
  </t:card>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
