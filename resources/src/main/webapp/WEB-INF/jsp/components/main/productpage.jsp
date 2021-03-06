<%--
  Copyright 2008-2013 Hippo B.V. (http://www.onehippo.com)

  Licensed under the Apache License, Version 2.0 (the  "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS"
  BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. --%>

<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://www.hippoecm.org/jsp/hst/core" prefix='hst'%>

<hst:defineObjects/>

<%---------------------------------------------------------------------------------------------------------------------
!!!!! HST ResourceBundle Examples !!!!!
-----------------------------------------------------------------------------------------------------------------------
  * Use one of the three following choices and read the explanation for each.

--%>
  <%-- Option 1: Set resource bundle with <hst:setBundle/> with specific basename
                 by which a resource bundle is chosen from the repository.
                 By default, if not found, it falls back to the default resource bundle configured in virtualhost(s)/mount/sitemapitem (See the option 4).
                 You can disable it by setting 'fallbackToDefaultLocalizationContext' attribute to 'false' though. --%>
    <%--
    <hst:setBundle basename="org.onehippo.hst.demo.resources.ProductResources" />
    --%>
  <%-- Option 2: If the resource bundle by the basename is not found in the repository,
                 it loads a resource bundle from the Java standard resource bundles by default
                 This fallback option can be configured by 'fallbackToJavaResourceBundle' attribute though.
                 Also, by default, if not found, it falls back to the default resource bundle configured in virtualhost(s)/mount/sitemapitem (See the option 4).
                 You can disable it by setting 'fallbackToDefaultLocalizationContext' attribute to 'false' though. --%>
    <%--
    <hst:setBundle basename="org.onehippo.hst.demo.resources.MyProductResources" />
    --%>
  <%-- Option 3: The basename may contain multiple (comma/whitespace separated) resource bundle basenames.
                 If multiple resource bundle basenames are set, then each resource bundle will be looked up for the key as ordered.
                 Also, by default, if not found, it falls back to the default resource bundle configured in virtualhost(s)/mount/sitemapitem (See the option 4).
                 You can disable it by setting 'fallbackToDefaultLocalizationContext' attribute to 'false' though. --%>
    <%--
    <hst:setBundle basename="org.onehippo.hst.demo.resources.ProductResources, org.onehippo.hst.demo.resources.MyProductResources" />
    --%>
  <%-- Option 4: If you use the same resource bundle basename for all pages under a specific mount, then
                 you can configure 'hst:defaultresourcebundleid = "org.onehippo.hst.demo.resources.ProductResources"' or
                 'hst:defaultresourcebundleid = "org.onehippo.hst.demo.resources.MyProductResources"' or
                 'hst:defaultresourcebundleid = "org.onehippo.hst.demo.resources.ProductResources, org.onehippo.hst.demo.resources.MyProductResources"'
                 The first one is similar to the option 1, and the second one to the option 2.
                 Finally, the third one is similar to the option 3.
                 In this case, you don't have to put <hst:setBundle/> tag in each page. --%>
<%---------------------------------------------------------------------------------------------------------------------%>

<hst:headContribution keyHint="title"><title><c:out value="${document.title}" /></title></hst:headContribution>
<hst:element name="script" var="yui3Elem">
  <hst:attribute name="type" value="text/javascript" />
  <hst:attribute name="src" value="http://yui.yahooapis.com/3.2.0/build/yui/yui-min.js" />
</hst:element>
<hst:headContribution keyHint="yui3" element="${yui3Elem}" />

<div id="<hst:namespace/>detailPane" class="yui-u">
<form name="theForm">
  <h2><c:out value="${document.title}" /></h2>
  <p>
    <c:out value="${document.summary}" />
  </p>

  <p>
    <fmt:message key="brand" /> :
    <c:out value="${document.brand}" />
    (
    View via
    <a href='<hst:link path="/products/brand/${document.brand}/" mount="restservices"/>' target='_blank'
       title='Click to view this product in XML generated by a Plain JAX-RS Service.'>Plain REST</a>
    or
    <a href='<hst:link hippobean="${document}" mount="restapi"/>' target='_blank'
       title='Click to view this product in XML generated by a Context/Content-Aware JAX-RS Service.'>Context-Aware REST</a>
    )
  </p>
  <p>
    <fmt:message key="product" /> :
    <c:choose>
      <c:when test="${isPreview and not empty(hstRequest.userPrincipal)}">
        <input type='text' size='8' id='<hst:namespace/>product' value='<c:out value="${document.product}" />' />
      </c:when>
      <c:otherwise>
        <c:out value="${document.product}" />
      </c:otherwise>
    </c:choose>

    (
    <a href='<hst:link path="/products/search/" mount="restservices"><hst:param name="product" value="${document.product}"/></hst:link>' target='_blank'
       title='Click to search all products of the same product type in XML generated by a Plain JAX-RS Service.'>Search all products of the same product type</a>
    )
  </p>
  <p>
    <fmt:message key="type" /> :
    <c:choose>
      <c:when test="${isPreview and not empty(hstRequest.userPrincipal)}">
        <input type='text' size='8' id='<hst:namespace/>type' value='<c:out value="${document.type}" />' />
      </c:when>
      <c:otherwise>
        <c:out value="${document.type}" />
      </c:otherwise>
    </c:choose>
  </p>
  <p>
    <fmt:message key="color" /> :
    <c:choose>
      <c:when test="${isPreview and not empty(hstRequest.userPrincipal)}">
        <input type='text' size='8' id='<hst:namespace/>color' value='<c:out value="${document.color}" />' />
      </c:when>
      <c:otherwise>
        <c:out value="${document.color}" />
      </c:otherwise>
    </c:choose>
  </p>
  <p>
    <fmt:message key="price" /> :
    <c:choose>
      <c:when test="${isPreview and not empty(hstRequest.userPrincipal)}">
        <input type='text' size='8' id='<hst:namespace/>price' value='<c:out value="${document.price}" />' />
      </c:when>
      <c:otherwise>
        <c:out value="${document.price}" />
      </c:otherwise>
    </c:choose>
  </p>
  <p>
    <fmt:message key="tags" /> :
    <c:choose>
      <c:when test="${isPreview and not empty(hstRequest.userPrincipal)}">
        <input id="<hst:namespace/>tags"
               type="text" size="8"
               title="Please enter comma separated tags here."
               value="${fn:escapeXml(fn:join(document.tags, ', '))}"/>
      </c:when>
      <c:otherwise>
        <span id="<hst:namespace/>tags">${fn:escapeXml(fn:join(document.tags, ', '))}</span>
      </c:otherwise>
    </c:choose>
  </p>
</form>

  <p>
    <fmt:message key="image" />:
    <br/>
    <c:if test="${not empty document.image}">
      <a href="<hst:link hippobean="${document.image.original}"><hst:param name="t" value="<%=Long.toString(System.currentTimeMillis())%>"/></hst:link>" target="_blank">
        <img id="productImg" src="<hst:link hippobean="${document.image.thumbnail}"><hst:param name="t" value="<%=Long.toString(System.currentTimeMillis())%>"/></hst:link>" border="0"/>
      </a>
    </c:if>
    <br/>
    <c:if test="${isPreview and empty(hstRequest.userPrincipal)}">
      <span><em>(Authentication required to upload image.)</em></span>
    </c:if>
    <form id="uploadForm" method="POST" enctype="multipart/form-data">
      <input type="file" id="<hst:namespace/>uploadFile" name="file" style="DISPLAY: none" />
      <input type="button" id="<hst:namespace/>uploadButton" value="Upload" style="DISPLAY: none" />
    </form>
  </p>
</div>

<c:choose>
  <c:when test="${isPreview}">
    <p>
      <a href="<hst:link hippobean="${document}" mountType="live"/>">View in Live</a>
    </p>
    <p>
      <a href="#" id="<hst:namespace/>updateViaContent" title='Click to update this product document by a Context/Content-Aware JAX-RS Service.'>Update via Content-Aware REST</a>
      |
      <a href="#" id="<hst:namespace/>updateViaPlain" title='Click to update this product document by a Plain JAX-RS Service.'>Update via Plain REST</a>
    </p>
    <p>
      <a href="#" id="<hst:namespace/>deleteViaContent" href="#" title='Click to remove this product document by a Context/Content-Aware JAX-RS Service.'>Delete via Content-Aware REST</a>
      |
      <a href="#" id="<hst:namespace/>deleteViaPlain" href="#" title='Click to remove this product document by a Plain JAX-RS Service.'>Delete via Plain REST</a>
    </p>
    <c:choose>
      <c:when test="${document.published}">
        <p>
          <a href="#" id="<hst:namespace/>requestDepublishViaContent" href="#" title='Click to request depublication this product document by a Context/Content-Aware JAX-RS Service.'>Request Depublication via Content-Aware REST</a>
          |
          <a href="#" id="<hst:namespace/>requestDepublishViaPlain" href="#" title='Click to request depublication this product document by a Plain JAX-RS Service.'>Request Depublication via Plain REST</a>
        </p>
        <p>
          <a href="#" id="<hst:namespace/>depublishViaContent" href="#" title='Click to depublish this product document by a Context/Content-Aware JAX-RS Service.'>Depublish via Content-Aware REST</a>
          |
          <a href="#" id="<hst:namespace/>depublishViaPlain" href="#" title='Click to depublish this product document by a Plain JAX-RS Service.'>Depublish via Plain REST</a>
        </p>
      </c:when>
      <c:otherwise>
        <p>
          <a href="#" id="<hst:namespace/>requestPublishViaContent" href="#" title='Click to request publication this product document by a Context/Content-Aware JAX-RS Service.'>Request Publication via Content-Aware REST</a>
          |
          <a href="#" id="<hst:namespace/>requestPublishViaPlain" href="#" title='Click to request publication this product document by a Plain JAX-RS Service.'>Request Publication via Plain REST</a>
        </p>
        <p>
          <a href="#" id="<hst:namespace/>publishViaContent" href="#" title='Click to publish this product document by a Context/Content-Aware JAX-RS Service.'>Publish via Content-Aware REST</a>
          |
          <a href="#" id="<hst:namespace/>publishViaPlain" href="#" title='Click to publish this product document by a Plain JAX-RS Service.'>Publish via Plain REST</a>
        </p>
      </c:otherwise>
    </c:choose>
  </c:when>
  <c:otherwise>
    <p>
      <a href="<hst:link hippobean="${document}" mountType="preview"/>">Edit in Preview</a>
    </p>
  </c:otherwise>
</c:choose>

<script language="javascript">

YUI().use('io-upload-iframe', 'json', 'node',
function(Y) {

  var updateViaContentLink = Y.one("#<hst:namespace/>updateViaContent");
  var updateViaPlainLink = Y.one("#<hst:namespace/>updateViaPlain");
  var deleteViaContentLink = Y.one("#<hst:namespace/>deleteViaContent");
  var deleteViaPlainLink = Y.one("#<hst:namespace/>deleteViaPlain");
  var requestPublishViaContentLink = Y.one("#<hst:namespace/>requestPublishViaContent");
  var requestPublishViaPlainLink = Y.one("#<hst:namespace/>requestPublishViaPlain");
  var publishViaContentLink = Y.one("#<hst:namespace/>publishViaContent");
  var publishViaPlainLink = Y.one("#<hst:namespace/>publishViaPlain");
  var requestDepublishViaContentLink = Y.one("#<hst:namespace/>requestDepublishViaContent");
  var requestDepublishViaPlainLink = Y.one("#<hst:namespace/>requestDepublishViaPlain");
  var depublishViaContentLink = Y.one("#<hst:namespace/>depublishViaContent");
  var depublishViaPlainLink = Y.one("#<hst:namespace/>depublishViaPlain");

  var uploadForm = Y.one("#uploadForm");
  var uploadFile = Y.one("#<hst:namespace/>uploadFile");
  var uploadButton = Y.one("#<hst:namespace/>uploadButton");

  var onUpdateComplete = function(id, o, args) {
    if (o.status >= 400) {
      var msg = "Update failed with " + o.status + " error.";
      if (o.responseText) {
        msg += "\n\n(" + o.responseText + ")";
      }
      alert(msg);
      return;
    }

    var id = id;
    var data = o.responseText;
    var dataOut = null;

    try {
      dataOut = Y.JSON.parse(data);
      if (!dataOut) {
        Y.log("Error: no data found.");
        return;
      }

      Y.one("#<hst:namespace/>color").set("value", "");
      Y.one("#<hst:namespace/>product").set("value", "");
      Y.one("#<hst:namespace/>type").set("value", "");
      Y.one("#<hst:namespace/>price").set("value", "");
      Y.one("#<hst:namespace/>tags").set("value", "");

      Y.one("#<hst:namespace/>color").set("value", dataOut["color"]);
      Y.one("#<hst:namespace/>product").set("value", dataOut["product"]);
      Y.one("#<hst:namespace/>type").set("value", dataOut["type"]);
      Y.one("#<hst:namespace/>price").set("value", dataOut["price"]);
      Y.one("#<hst:namespace/>tags").set("value", dataOut["tags"].join(", "));

      alert("Update completed.\nYou may need to refresh the page if you requested any workflow action like (request) publishing/depublishing.");
    } catch (e) {
      Y.log("Error: " + e.message);
      return;
    }
  };

  var updateVia = function(uri) {
    var data = {};

    data["color"] = Y.one("#<hst:namespace/>color").get("value");
    data["product"] = Y.one("#<hst:namespace/>product").get("value");
    data["type"] = Y.one("#<hst:namespace/>type").get("value");
    data["price"] = Y.one("#<hst:namespace/>price").get("value");

    var tags = Y.one("#<hst:namespace/>tags").get("value").replace(/^\s+/, "").replace(/\s+$/, "").split(/,/);
    for (var i = 0; i < tags.length; i++) {
      tags[i] = tags[i].replace(/^\s+/, "").replace(/\s+$/, "");
    }
    data["tags"] = tags;

    var cfg = {
          on: { complete: onUpdateComplete },
          arguments: {},
          method: "PUT",
          headers: {
              "Accept": "application/json, text/javascript, text/html, application/xhtml+xml, application/xml, text/xml",
              "Content-Type": "application/json"
                  },
          data: Y.JSON.stringify(data)
    };
    var request = Y.io(uri, cfg);
  };

  var updateViaContent = function(e) {
	updateVia('<hst:link path="${hstRequest.requestContext.resolvedSiteMapItem.pathInfo}" mount="restapi"><hst:param name="_type" value="json"/></hst:link>');
	e.halt();
  };

  var updateViaPlain = function(e) {
    updateVia('<hst:link path="/products/brand/${document.brand}/" mount="restservices"><hst:param name="_type" value="json"/></hst:link>');
    e.halt();
  };

  var requestPublishViaContent = function(e) {
    updateVia('<hst:link path="${hstRequest.requestContext.resolvedSiteMapItem.pathInfo}" mount="restapi"><hst:param name="_type" value="json"/><hst:param name="wfaction" value="requestPublication"/></hst:link>');
    e.halt();
  };

  var requestPublishViaPlain = function(e) {
    updateVia('<hst:link path="/products/brand/${document.brand}/" mount="restservices"><hst:param name="_type" value="json"/><hst:param name="wfaction" value="requestPublication"/></hst:link>');
    e.halt();
  };

  var publishViaContent = function(e) {
    updateVia('<hst:link path="${hstRequest.requestContext.resolvedSiteMapItem.pathInfo}" mount="restapi"><hst:param name="_type" value="json"/><hst:param name="wfaction" value="publish"/></hst:link>');
    e.halt();
  };

  var publishViaPlain = function(e) {
    updateVia('<hst:link path="/products/brand/${document.brand}/" mount="restservices"><hst:param name="_type" value="json"/><hst:param name="wfaction" value="publish"/></hst:link>');
    e.halt();
  };

  var requestDepublishViaContent = function(e) {
    updateVia('<hst:link path="${hstRequest.requestContext.resolvedSiteMapItem.pathInfo}" mount="restapi"><hst:param name="_type" value="json"/><hst:param name="wfaction" value="requestDepublication"/></hst:link>');
    e.halt();
  };

  var requestDepublishViaPlain = function(e) {
    updateVia('<hst:link path="/products/brand/${document.brand}/" mount="restservices"><hst:param name="_type" value="json"/><hst:param name="wfaction" value="requestDepublication"/></hst:link>');
    e.halt();
  };

  var depublishViaContent = function(e) {
    updateVia('<hst:link path="${hstRequest.requestContext.resolvedSiteMapItem.pathInfo}" mount="restapi"><hst:param name="_type" value="json"/><hst:param name="wfaction" value="depublish"/></hst:link>');
    e.halt();
  };

  var depublishViaPlain = function(e) {
    updateVia('<hst:link path="/products/brand/${document.brand}/" mount="restservices"><hst:param name="_type" value="json"/><hst:param name="wfaction" value="depublish"/></hst:link>');
    e.halt();
  };

  var refreshProductImage = function() {
    var productImg = Y.one("#productImg");
	if (productImg) {
	  var src = "" + productImg.get("src");
      if (src.indexOf('?') != -1) {
        src = src.substring(0, src.indexOf('?'));
      }
      src += "?t=" + new Date().getTime();
      productImg.set("src", src);
	}
  };

  var onUploadImageComplete = function(id, o, args) {
    refreshProductImage();
  };

<c:if test="${not empty document.image}">
  var uploadImageForm = function(e) {
    var cfg = {
          on: { complete: onUploadImageComplete },
          arguments: {},
          method: 'POST',
		  form: {
		    id: uploadForm,
		    upload: true
          }
    };

    var uri = '<hst:link hippobean="${document.image}" mount="restapi-gallery" subPath="original/content" />';
    var request = Y.io(uri, cfg);

    e.halt();
  };
</c:if>

  var onDeleteViaComplete = function(id, o, args) {
    if (o.status >= 400) {
      alert("You are not authorized to edit the tags.");
      return;
    } else if (o.status == 200) {
      alert("The product content has been removed.\nYou will get 404 on refresh for the current document.");
      return;
    }
  };

  var deleteVia = function(uri) {
    if (!confirm("Are you sure to remove this product document?\nYou will get 404 error from the GET this resource.")) {
      return;
    }
    var cfg = {
          on: { complete: onDeleteViaComplete },
          arguments: {},
          method: 'DELETE'
    };

    var request = Y.io(uri, cfg);
  };

  var deleteViaContent = function(e) {
    deleteVia('<hst:link hippobean="${document}" mount="restapi"/>');
    e.halt();
  };

  var deleteViaPlain = function(e) {
    deleteVia('<hst:link path="/products/brand/${document.brand}/" mount="restservices"/>');
    e.halt();
  };

<c:if test="${isPreview and not(empty(hstRequest.userPrincipal))}">
  uploadFile.setStyle("display", "");
  uploadButton.setStyle("display", "");
</c:if>

<c:if test="${not empty document.image}">
  uploadButton.on("click", uploadImageForm);
</c:if>

  if (updateViaContentLink) {
	updateViaContentLink.on("click", updateViaContent);
  }
  if (updateViaPlainLink) {
	updateViaPlainLink.on("click", updateViaPlain);
  }
  if (deleteViaContentLink) {
	deleteViaContentLink.on("click", deleteViaContent);
  }
  if (deleteViaPlainLink) {
    deleteViaPlainLink.on("click", deleteViaPlain);
  }
  if (requestPublishViaContentLink) {
	requestPublishViaContentLink.on("click", requestPublishViaContent);
  }
  if (requestPublishViaPlainLink) {
	requestPublishViaPlainLink.on("click", requestPublishViaPlain);
  }
  if (publishViaContentLink) {
	publishViaContentLink.on("click", publishViaContent);
  }
  if (publishViaPlainLink) {
	publishViaPlainLink.on("click", publishViaPlain);
  }
  if (requestDepublishViaContentLink) {
	requestDepublishViaContentLink.on("click", requestDepublishViaContent);
  }
  if (requestDepublishViaPlainLink) {
	requestDepublishViaPlainLink.on("click", requestDepublishViaPlain);
  }
  if (depublishViaContentLink) {
	depublishViaContentLink.on("click", depublishViaContent);
  }
  if (depublishViaPlainLink) {
	depublishViaPlainLink.on("click", depublishViaPlain);
  }
});

</script>
