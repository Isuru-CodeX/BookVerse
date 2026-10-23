package com.bookverse.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.Locale;

/**
 * Simple tag handler (JSP 2.x SimpleTag API): &lt;bv:rating value="4.6" max="5"/&gt;
 * Renders star glyphs plus the numeric score. Empty body.
 */
public class RatingTag extends SimpleTagSupport {
    private double value;
    private int max = 5;

    public void setValue(double value) { this.value = value; }
    public void setMax(int max) { this.max = Math.max(1, max); }

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        if (value <= 0) {
            out.write("<span class=\"rating rating-none\">Not rated yet</span>");
            return;
        }
        int filled = (int) Math.round(Math.min(value, max));
        StringBuilder sb = new StringBuilder();
        sb.append("<span class=\"rating\" title=\"")
          .append(String.format(Locale.ROOT, "%.1f out of %d", value, max)).append("\">");
        sb.append("<span class=\"stars\" aria-hidden=\"true\">");
        for (int i = 1; i <= max; i++) sb.append(i <= filled ? '\u2605' : '\u2606');
        sb.append("</span> <span class=\"score\">").append(String.format(Locale.ROOT, "%.1f", value)).append("</span>");
        sb.append("<span class=\"sr-only\"> out of ").append(max).append("</span></span>");
        out.write(sb.toString());
    }
}
