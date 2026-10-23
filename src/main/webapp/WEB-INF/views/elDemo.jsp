<%-- View: Expression Language lab. Every row prints the expression and its live result. --%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
  <jsp:param name="pageTitle" value="EL lab"/>
  <jsp:param name="active" value="el"/>
</jsp:include>

<c:set var="pageNote" value="Set with c:set in page scope" scope="page"/>
<c:set var="key" value="two"/>

<t:pageHeader title="Expression Language lab" subtitle="Live examples of every EL implicit object and both access operators.">
  <a class="btn btn-quiet" href="<c:url value='/elDemo.do'><c:param name='q' value='Hello EL'/><c:param name='tag' value='java'/><c:param name='tag' value='jsp'/></c:url>">Load sample parameters</a>
</t:pageHeader>

<%-- jsp:include + jsp:param, with jsp:attribute supplying a parameter value from a nested element --%>
<jsp:include page="/WEB-INF/views/fragments/note.jsp">
  <jsp:param name="label" value="Parameters sent with jsp:include"/>
  <jsp:param name="text"><jsp:attribute name="value">This text was passed through jsp:attribute; request method is ${pageContext.request.method}.</jsp:attribute></jsp:param>
</jsp:include>

<t:card title="1. Scope objects">
  <table class="table el-table">
    <thead><tr><th>Expression</th><th>Result</th><th>Notes</th></tr></thead>
    <tbody>
      <t:elrow expr="pageScope.pageNote" note="page scope">${pageScope.pageNote}</t:elrow>
      <t:elrow expr="requestScope.demoMessage" note="request scope, set by ElDemoCommand">${requestScope.demoMessage}</t:elrow>
      <t:elrow expr="sessionScope.demoNote" note="session scope">${sessionScope.demoNote}</t:elrow>
      <t:elrow expr="applicationScope.appName" note="application scope, set by AppContextListener">${applicationScope.appName}</t:elrow>
      <t:elrow expr="demoMessage" note="no scope prefix: searched page, request, session, application">${demoMessage}</t:elrow>
      <t:elrow expr="sessionScope.member.name" note="bean property in session scope (guest if empty)"><c:out value="${empty sessionScope.member ? 'guest' : sessionScope.member.name}"/></t:elrow>
    </tbody>
  </table>
</t:card>

<t:card title="2. Request parameters">
  <table class="table el-table">
    <thead><tr><th>Expression</th><th>Result</th><th>Notes</th></tr></thead>
    <tbody>
      <t:elrow expr="param.q" note="first value of ?q="><c:out value="${param.q}" default="(add ?q=... to the URL)"/></t:elrow>
      <t:elrow expr="paramValues.tag[0]" note="array of all ?tag= values, index 0"><c:out value="${paramValues.tag[0]}" default="(none)"/></t:elrow>
      <t:elrow expr="paramValues.tag[1]" note="index 1"><c:out value="${paramValues.tag[1]}" default="(none)"/></t:elrow>
      <t:elrow expr="fn:length(paramValues.tag)" note="number of tag values">${fn:length(paramValues.tag)}</t:elrow>
    </tbody>
  </table>
</t:card>

<t:card title="3. Headers and cookies">
  <table class="table el-table">
    <thead><tr><th>Expression</th><th>Result</th><th>Notes</th></tr></thead>
    <tbody>
      <t:elrow expr="header['user-agent']" note="[] needed because of the hyphen"><span class="clip"><c:out value="${header['user-agent']}"/></span></t:elrow>
      <t:elrow expr="header.host" note="dot operator on a map"><c:out value="${header.host}"/></t:elrow>
      <t:elrow expr="headerValues['accept-language'][0]" note="first of all values"><c:out value="${headerValues['accept-language'][0]}" default="(not sent)"/></t:elrow>
      <t:elrow expr="cookie.bv_lastVisit.value" note="cookie written by HomeCommand"><c:out value="${cookie.bv_lastVisit.value}" default="(visit the home page first)"/></t:elrow>
      <t:elrow expr="fn:length(cookie.JSESSIONID.value)" note="session cookie length (value stays private)">${fn:length(cookie.JSESSIONID.value)}</t:elrow>
    </tbody>
  </table>
</t:card>

<t:card title="4. Context parameters and pageContext">
  <table class="table el-table">
    <thead><tr><th>Expression</th><th>Result</th><th>Notes</th></tr></thead>
    <tbody>
      <t:elrow expr="initParam.libraryName" note="web.xml context-param"><c:out value="${initParam.libraryName}"/></t:elrow>
      <t:elrow expr="initParam['supportEmail']" note="same lookup with []"><c:out value="${initParam['supportEmail']}"/></t:elrow>
      <t:elrow expr="initParam.maxActiveLoans" note="also read by AppContextListener">${initParam.maxActiveLoans}</t:elrow>
      <t:elrow expr="pageContext.request.method" note="getRequest().getMethod()">${pageContext.request.method}</t:elrow>
      <t:elrow expr="pageContext.request.contextPath" note="getContextPath()"><c:out value="${pageContext.request.contextPath}" default="(root context)"/></t:elrow>
      <t:elrow expr="pageContext.request.requestURI" note="after the controller forwards, this is the view's path"><c:out value="${pageContext.request.requestURI}"/></t:elrow>
      <t:elrow expr="requestScope['javax.servlet.forward.request_uri']" note="the URL the browser asked for"><c:out value="${requestScope['javax.servlet.forward.request_uri']}"/></t:elrow>
      <t:elrow expr="pageContext.session.maxInactiveInterval" note="seconds, from web.xml session-timeout">${pageContext.session.maxInactiveInterval}</t:elrow>
      <t:elrow expr="pageContext.session['new']" note="isNew(); 'new' is a reserved word, so [] is required">${pageContext.session['new']}</t:elrow>
      <t:elrow expr="pageContext.servletContext.serverInfo" note="getServletContext().getServerInfo()"><c:out value="${pageContext.servletContext.serverInfo}"/></t:elrow>
    </tbody>
  </table>
</t:card>

<t:card title="5. Property (.) and collection ([]) operators">
  <table class="table el-table">
    <thead><tr><th>Expression</th><th>Result</th><th>Notes</th></tr></thead>
    <tbody>
      <t:elrow expr="requestScope.books[0].title" note="List index, then bean property"><c:out value="${requestScope.books[0].title}"/></t:elrow>
      <t:elrow expr="requestScope.books[0]['author']" note="bean property through []"><c:out value="${requestScope.books[0]['author']}"/></t:elrow>
      <t:elrow expr="requestScope.sampleMap.one" note="Map key with .">${requestScope.sampleMap.one}</t:elrow>
      <t:elrow expr="requestScope.sampleMap['key.with.dots']" note="keys with special characters need []">${requestScope.sampleMap['key.with.dots']}</t:elrow>
      <t:elrow expr="requestScope.sampleMap[key]" note="key is the page variable key=two">${requestScope.sampleMap[key]}</t:elrow>
      <t:elrow expr="requestScope.sampleArray[2]" note="String[] index">${requestScope.sampleArray[2]}</t:elrow>
      <t:elrow expr="requestScope.sampleList[1]" note="List&lt;Integer&gt; index">${requestScope.sampleList[1]}</t:elrow>
      <t:elrow expr="fn:length(requestScope.books)" note="size of the book list">${fn:length(requestScope.books)}</t:elrow>
    </tbody>
  </table>
</t:card>

<t:card title="6. Operators">
  <table class="table el-table">
    <thead><tr><th>Expression</th><th>Result</th><th>Notes</th></tr></thead>
    <tbody>
      <t:elrow expr="7 + 3 * 2" note="arithmetic">${7 + 3 * 2}</t:elrow>
      <t:elrow expr="10 div 4" note="division">${10 div 4}</t:elrow>
      <t:elrow expr="17 mod 5" note="modulus">${17 mod 5}</t:elrow>
      <t:elrow expr="3 lt 5 and 5 le 5" note="relational + logical">${3 lt 5 and 5 le 5}</t:elrow>
      <t:elrow expr="'JSP' eq 'jsp'" note="string comparison is case sensitive">${'JSP' eq 'jsp'}</t:elrow>
      <t:elrow expr="empty requestScope.nothing" note="empty is true for null">${empty requestScope.nothing}</t:elrow>
      <t:elrow expr="not empty sessionScope.member" note="signed in?">${not empty sessionScope.member}</t:elrow>
      <t:elrow expr="requestScope.books[0].available ? 'In stock' : 'On loan'" note="conditional (ternary)">${requestScope.books[0].available ? 'In stock' : 'On loan'}</t:elrow>
    </tbody>
  </table>
</t:card>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
