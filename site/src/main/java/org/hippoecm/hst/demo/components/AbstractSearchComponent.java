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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryManager;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.query.filter.Filter;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.util.SearchInputParsingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSearchComponent extends BaseHstComponent {

    public static final Logger log = LoggerFactory.getLogger(AbstractSearchComponent.class);

    public static final int DEFAULT_PAGE_SIZE = 5;

    
    
    
    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
       super.doBeforeRender(request, response);
    }


    protected void doSearch(HstRequest request, HstResponse response, String query, String nodeType, String sortBy, int pageSize, HippoBean scope) {

        if (scope == null) {
            log.error("Scope for search is null.");
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

            hstQuery.setLimit(pageSize);
            hstQuery.setOffset((page - 1) * pageSize);
            
            if (sortBy != null) {
                hstQuery.addOrderByDescending(sortBy);
            }
            
            if (query != null) {
                query = SearchInputParsingUtils.parse(query, false);
                Filter filter = hstQuery.createFilter();
                filter.addContains(".", query);
                hstQuery.setFilter(filter);
                request.setAttribute("query", StringEscapeUtils.escapeHtml(query));
            }
            
            final HstQueryResult result = hstQuery.execute();

            request.setAttribute("result", result);
            request.setAttribute("crPage", page);
            
            // add pages
            if(result.getTotalSize() > pageSize) {
                List<Integer> pages = new ArrayList<Integer>();
                int numberOfPages = result.getTotalSize() / pageSize ;
                if(result.getTotalSize() % pageSize != 0) {
                    numberOfPages++;
                }
                for(int i = 0; i < numberOfPages; i++) {
                    pages.add(i + 1);
                }
                request.setAttribute("pages", pages);
            }

        } catch (QueryException e) {
            log.error("Exception in searchComponent: ", e);
        }
        
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
