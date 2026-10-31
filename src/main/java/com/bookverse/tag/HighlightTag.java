package com.bookverse.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Body tag handler (BodyTag lifecycle):
 * <pre>&lt;bv:highlight keyword="java"&gt;Effective Java&lt;/bv:highlight&gt;</pre>
 * doStartTag -&gt; EVAL_BODY_BUFFERED, body is evaluated into a BodyContent buffer,
 * doAfterBody reads that buffer, HTML-escapes it, wraps keyword matches in &lt;mark&gt; and writes
 * to the enclosing writer (getPreviousOut). The match count is stored in page scope.
 */
public class HighlightTag extends BodyTagSupport {
    private static final long serialVersionUID = 1L;
    private String keyword;

    public void setKeyword(String keyword) { this.keyword = keyword; }

    @Override
    public int doStartTag() { return EVAL_BODY_BUFFERED; }

    @Override
    public int doAfterBody() throws JspException {
        String body = bodyContent.getString();
        String safe = escape(body);
        int count = 0;
        if (keyword != null && !keyword.trim().isEmpty()) {
            String q = escape(keyword.trim());
            int qLen = q.length();
            if (qLen > 0) {
                // ⚡ Bolt: Replaced Regex compilation and Matcher with fast indexOfIgnoreCase + regionMatches to eliminate regex overhead and object allocation.
                StringBuilder sb = new StringBuilder();
                int lastIdx = 0;
                int len = safe.length();
                while (lastIdx < len) {
                    int found = indexOfIgnoreCase(safe, q, lastIdx);
                    if (found == -1) {
                        sb.append(safe.substring(lastIdx));
                        break;
                    }
                    count++;
                    sb.append(safe.substring(lastIdx, found));
                    sb.append("<mark>").append(safe.substring(found, found + qLen)).append("</mark>");
                    lastIdx = found + qLen;
                }
                safe = sb.toString();
            }
        }
        try {
            getPreviousOut().write(safe);
        } catch (IOException e) {
            throw new JspException("Unable to write highlighted text", e);
        }
        pageContext.setAttribute("highlightMatches", count, PageContext.PAGE_SCOPE);
        return SKIP_BODY;
    }

    @Override
    public void release() {
        super.release();
        keyword = null;
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static int indexOfIgnoreCase(String src, String what, int fromIndex) {
        if (what.isEmpty()) return -1;
        final int srcLen = src.length();
        final int whatLen = what.length();
        final int max = srcLen - whatLen;
        if (max < 0) return -1;
        char firstLower = Character.toLowerCase(what.charAt(0));
        char firstUpper = Character.toUpperCase(what.charAt(0));
        for (int i = fromIndex; i <= max; i++) {
            char c = src.charAt(i);
            if (c == firstLower || c == firstUpper) {
                if (src.regionMatches(true, i, what, 0, whatLen)) return i;
            }
        }
        return -1;
    }
}
