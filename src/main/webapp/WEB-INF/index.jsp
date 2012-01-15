<!--
  #%L
  debox-photos
  %%
  Copyright (C) 2012 Debox
  %%
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU Affero General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  #L%
-->
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="includes/header.jsp" />

<div class="page-header">
    <h1></h1>
</div>
<ul id="albums">
    <c:forEach items="${list}" var="item">
        <c:if test="${item.isDirectory()}">
            <li><a href="#<c:url value="/album/${item.getName()}" />">${item.getName()}</a></li>
        </c:if>
    </c:forEach>
</ul>

<div id="photos"></div>
<div id="photo"></div>

<c:import url="includes/footer.jsp" />
