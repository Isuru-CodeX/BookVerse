<%@ tag description="Tab bar shown at the top of every staff page" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="active" required="true" description="overview | books | members | loans | plans | announcements | feedback | reports" %>
<nav class="admin-tabs" aria-label="Staff sections">
  <a href="<c:url value='/admin.do'/>"               class="${active eq 'overview'      ? 'current' : ''}">Overview</a>
  <a href="<c:url value='/adminBooks.do'/>"          class="${active eq 'books'         ? 'current' : ''}">Books</a>
  <a href="<c:url value='/adminMembers.do'/>"        class="${active eq 'members'       ? 'current' : ''}">Members</a>
  <a href="<c:url value='/adminLoans.do'/>"          class="${active eq 'loans'         ? 'current' : ''}">Loans</a>
  <a href="<c:url value='/adminPlans.do'/>"          class="${active eq 'plans'         ? 'current' : ''}">Plans</a>
  <a href="<c:url value='/adminAnnouncements.do'/>"  class="${active eq 'announcements' ? 'current' : ''}">Announcements</a>
  <a href="<c:url value='/adminFeedback.do'/>"       class="${active eq 'feedback'      ? 'current' : ''}">Feedback</a>
  <a href="<c:url value='/adminReports.do'/>"        class="${active eq 'reports'       ? 'current' : ''}">Reports</a>
</nav>
