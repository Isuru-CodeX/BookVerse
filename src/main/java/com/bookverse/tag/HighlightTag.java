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
            Matcher m = Pattern.compile(Pattern.quote(escape(keyword.trim())), Pattern.CASE_INSENSITIVE).matcher(safe);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                count++;
                m.appendReplacement(sb, "<mark>" + Matcher.quoteReplacement(m.group()) + "</mark>");
            }
            m.appendTail(sb);
            safe = sb.toString();
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
}
