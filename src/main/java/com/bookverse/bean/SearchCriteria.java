package com.bookverse.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/** JavaBean holding the catalogue search/filter state (request scope). */
public class SearchCriteria implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final List<String> SORTS = Arrays.asList("title", "rating", "year");

    private String keyword = "";
    private String category = "";
    private boolean availableOnly;
    private String sort = "title";

    public SearchCriteria() { }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword == null ? "" : keyword.trim(); }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category == null ? "" : category.trim(); }
    public boolean isAvailableOnly() { return availableOnly; }
    public void setAvailableOnly(boolean availableOnly) { this.availableOnly = availableOnly; }
    public String getSort() { return sort; }
    /** Only whitelisted sort keys are accepted. */
    public void setSort(String sort) { this.sort = SORTS.contains(sort) ? sort : "title"; }

    /** True when the user supplied any filter. */
    public boolean isFiltered() { return !keyword.isEmpty() || !category.isEmpty() || availableOnly; }
}
