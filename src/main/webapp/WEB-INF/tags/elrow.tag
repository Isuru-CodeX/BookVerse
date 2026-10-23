<%@ tag description="One row of the EL lab table: shows the expression text and its evaluated result" pageEncoding="UTF-8" body-content="scriptless" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="expr" required="true" description="Expression text WITHOUT the ${ } wrapper" %>
<%@ attribute name="note" required="false" %>
<tr>
  <td><code>\${<c:out value="${expr}"/>}</code></td>
  <td class="result"><jsp:doBody/></td>
  <td class="muted"><c:out value="${note}"/></td>
</tr>
