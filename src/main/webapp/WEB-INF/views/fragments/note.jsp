<%-- Included fragment: receives its data through jsp:param (read back with the "param" implicit object) --%>
<aside class="include-note">
  <strong><c:out value="${param.label}"/></strong>
  <p><c:out value="${param.text}"/></p>
</aside>
