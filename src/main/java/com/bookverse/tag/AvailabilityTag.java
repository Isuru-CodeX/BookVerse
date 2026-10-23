package com.bookverse.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * Classic tag handler (Tag interface lifecycle): &lt;bv:availability copies="2" lowStock="1"/&gt;
 * Lifecycle: setters -&gt; doStartTag() -&gt; (SKIP_BODY) -&gt; doEndTag() -&gt; release().
 * Uses the inherited PageContext API to write output and to keep a per-request counter.
 */
public class AvailabilityTag extends TagSupport {
    private static final long serialVersionUID = 1L;
    private int copies;
    private int lowStock = 1;

    public void setCopies(int copies) { this.copies = copies; }
    public void setLowStock(int lowStock) { this.lowStock = lowStock; }

    @Override
    public int doStartTag() throws JspException {
        String css;
        String text;
        if (copies <= 0) {
            css = "badge badge-out";
            text = "On loan";
        } else if (copies <= lowStock) {
            css = "badge badge-low";
            text = "Last " + (copies == 1 ? "copy" : copies + " copies");
        } else {
            css = "badge badge-ok";
            text = copies + " available";
        }
        try {
            JspWriter out = pageContext.getOut();
            out.write("<span class=\"" + css + "\">" + text + "</span>");
        } catch (IOException e) {
            throw new JspException("Unable to write availability badge", e);
        }
        // PageContext API: track how many badges this request rendered (request scope)
        Integer n = (Integer) pageContext.getAttribute("badgesRendered", PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("badgesRendered", n == null ? 1 : n + 1, PageContext.REQUEST_SCOPE);
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() { return EVAL_PAGE; }

    @Override
    public void release() {
        super.release();
        copies = 0;
        lowStock = 1;
    }
}
