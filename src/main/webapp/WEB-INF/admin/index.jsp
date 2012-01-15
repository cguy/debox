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

<c:import url="../includes/header.jsp" />

<div class="page-header">
    <h1>Administration</h1>
</div>

<ul>
    <li><a href="#" data-controls-modal="add-album-modal" data-backdrop="true">Ajouter un album photo</a></li>
</ul>

<div id="add-album-modal" class="modal hide fade">
    <div class="modal-header">
        <a href="#" class="close">&times;</a>
        <h3>Ajouter un album photo</h3>
    </div>
    <form method="post" action="<c:url value="/admin/album" />">
        <div class="modal-body">

        </div>
        <div class="modal-footer">
            <a href="#" id="add-album-cancel" class="btn secondary">Retour</a>
            <input type="submit" class="btn primary" value="Valider" />
        </div>
    </form>
</div>

<c:import url="../includes/footer.jsp" />
