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

<h1 class="page-header"></h1>
<div id="albums"></div>
<div id="photos"></div>
<div id="photo"></div>
<div id="administration">
    <h2 class="page-header">Configuration générale</h2>
    <form class="form-stacked" method="post" action="#/administration/configuration">
        <div class="clearfix">
            <label for="sourceDirectory">Répertoire source (contenant les photos au format original) :</label>
            <div class="input">
                <input class="span5" value="${sourceDirectory}" type="text" required id="sourceDirectory" name="sourceDirectory" placeholder="Exemple : /home/user/photos/" />
            </div>
        </div>
        <div class="clearfix">
            <label for="targetDirectory">Répertoire de travail (qui contiendra notamment les vignettes des photos) :</label>
            <div class="input">
                <input class="span5" value="${targetDirectory}" type="text" required id="targetDirectory" name="targetDirectory" placeholder="Exemple : /home/user/thumbnails/" />
            </div>
        </div>
        <div class="actions">
            <input type="submit" class="btn primary" value="Valider" />
            <button type="reset" class="btn">Annuler</button>
        </div>
    </form>
    <div id="admin_albums"></div>
</div>

<c:import url="includes/footer.jsp" />
