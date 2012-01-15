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
    <h1>${photo}<small>${album}</small></h1>
</div>
<ul>
    <li><a href=".">Retour à l'album</a></li>
</ul>
<div class="media-grid" style="text-align: center;">
    <a href="/deploy/photo/${album}/${photo}" id="${photo}" style="display:inline-block; float:none;">
        <img class="thumbnail" src="/deploy/photo/${album}/${photo}" style="max-height:700px;max-width:100%;" />
    </a>
</div>

<c:import url="includes/footer.jsp" />
