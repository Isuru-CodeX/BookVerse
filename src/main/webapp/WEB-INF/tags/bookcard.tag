<%@ tag description="Catalogue card for one Book" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bv" uri="http://bookverse.lk/tags" %>
<%@ attribute name="book" required="true" type="com.bookverse.model.Book" description="Book to display" %>
<%@ attribute name="keyword" required="false" description="Search text to highlight" %>
<c:url var="detailUrl" value="/bookDetail.do"><c:param name="id" value="${book.id}"/></c:url>
<article class="book-card">
  <a class="cover" href="${detailUrl}" style="--hue:${(book.id * 47) mod 360}" aria-hidden="true" tabindex="-1">
    <span><c:out value="${book.initials}"/></span>
    <small><c:out value="${book.category}"/></small>
  </a>
  <div class="book-meta">
    <h3><a href="${detailUrl}"><bv:highlight keyword="${keyword}">${book.title}</bv:highlight></a></h3>
    <p class="by"><bv:highlight keyword="${keyword}">${book.author}</bv:highlight> &middot; ${book.year}</p>
    <div class="book-foot">
      <bv:rating value="${book.rating}"/>
      <bv:availability copies="${book.availableCopies}"/>
    </div>
  </div>
</article>
