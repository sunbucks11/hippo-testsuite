/*
 *  Copyright 2009 Hippo.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.hippoecm.hst.demo.components;

import org.apache.commons.lang.StringEscapeUtils;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryManager;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.query.filter.Filter;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.demo.beans.BaseBean;
import org.hippoecm.hst.demo.util.PageableCollection;
import org.hippoecm.hst.demo.util.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSearchComponent extends BaseHstComponent {

    public static final Logger log = LoggerFactory.getLogger(AbstractSearchComponent.class);

    private static PageableCollection<SearchResult<HippoBean>> EMPTY_RESULTS = new PageableCollection<SearchResult<HippoBean>>(0);

    public static final int DEFAULT_PAGE_SIZE = 5;

    
    
    
    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
       super.doBeforeRender(request, response);
    }


    protected void doSearch(HstRequest request, HstResponse response, String query, String nodeType, String sortBy, int pageSize, HippoBean scope) {

        if (scope == null) {
            log.error("Scope for search is null.");
            request.setAttribute("searchResults", EMPTY_RESULTS);
            return;
        }

        String pageParam = request.getParameter("page");
        if (pageParam == null) {
            pageParam = getPublicRequestParameter(request, "page");
        }
        int page = getIntValue(pageParam, 1);

        request.setAttribute("page", page);
        HstQueryManager manager = getQueryManager(request);
        try {
            
            final HstQuery hstQuery;
            if(nodeType == null) {
               hstQuery = manager.createQuery(scope);
            } else {
               hstQuery = manager.createQuery(scope, nodeType);
            }
            
            if (sortBy != null) {
                hstQuery.addOrderByDescending(sortBy);
            }
            
            if (query != null) {
                Filter filter = hstQuery.createFilter();
                filter.addContains(".", query);
                hstQuery.setFilter(filter);
                request.setAttribute("query", StringEscapeUtils.escapeHtml(query));
            }
            
            final HstQueryResult result = hstQuery.execute();
            PageableCollection<SearchResult<HippoBean>> results = new PageableCollection<SearchResult<HippoBean>>(
                    result.getSize());
            results.setPageNumber(page);
            results.setPageSize(pageSize);
            int startAt = results.getStartOffset();

            final HippoBeanIterator iterator = result.getHippoBeans();
            // don't skip past unreachable item:
            if (startAt < results.getTotal()) {
                iterator.skip(startAt);
            }
            int count = 0;

            while (iterator.hasNext() && count < pageSize) {
                HippoBean bean = iterator.nextHippoBean();
                // note: bean can be null
                if (bean != null && bean instanceof BaseBean) {
                    BaseBean pageBean = (BaseBean) bean;
                    results.addItem(new SearchResult<HippoBean>(bean, pageBean.getTitle(), pageBean.getSummary(),
                            pageBean.getDate()));
                    count++;
                }
            }
            request.setAttribute("searchResults", results);
            

        } catch (QueryException e) {
            log.error("Exception in searchComponent: ", e);
            setError(request, "An error occurred, invalid query syntax?");
        }
        
    }

    
    /**
     * Set error message on request so we can display it to user
     *
     * @param errorMessage message to display
     * @param request      HstRequest instance
     */
    public void setError(final HstRequest request, final String errorMessage) {
        request.setAttribute("error", errorMessage);
        request.setAttribute("searchResults", EMPTY_RESULTS);
    }

    /**
     * Parses int value from string object.
     * If value is null or parsing error occures, it returns default value
     *
     * @param value        value to be parsed
     * @param defaultValue default value to return
     * @return parsed value or default value on error
     */
    protected static int getIntValue(String value, int defaultValue) {

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            //ignore
        }
        return defaultValue;
    }
    
    
}
