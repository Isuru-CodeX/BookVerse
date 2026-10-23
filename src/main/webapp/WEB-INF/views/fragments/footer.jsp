<%-- Shared page closing: reads application-scope attributes and an init-param --%>
<jsp:useBean id="now" class="java.util.Date"/>
</main>
<footer class="site-footer">
  <div class="container footer-grid">
    <div>
      <strong><c:out value="${applicationScope.appName}"/></strong>
      <p class="muted">Books, journals and magazines for every member. Questions? Write to
        <a href="mailto:${initParam.supportEmail}"><c:out value="${initParam.supportEmail}"/></a>.</p>
    </div>
    <ul class="footer-stats">
      <li>${applicationScope.visitCounter} home page visits</li>
      <li>${applicationScope.activeSessions} active sessions</li>
      <li>Data store: <c:out value="${applicationScope.storageMode}"/></li>
      <li>Server up since <fmt:formatDate value="${applicationScope.startedAt}" pattern="d MMM yyyy, HH:mm"/></li>
    </ul>
  </div>
  <div class="container copyright">&copy; <fmt:formatDate value="${now}" pattern="yyyy"/> BookVerse &middot; HF2K Web Component Development II</div>
</footer>
</body>
</html>
