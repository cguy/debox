<%--
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
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!DOCTYPE html>
<html lang="fr">
    <head>
        <meta charset="utf-8">
        
        <title></title>

        <script type="text/javascript" src="<c:url value="/js/jquery-1.7.1.min.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/jquery.rs.slideshow.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/bootstrap-modal.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/bootstrap-dropdown.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/bootstrap-button.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/bootstrap-transition.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/sammy-0.7.1.min.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/mustache.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/routes.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/slideshow.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/utils.js" />"></script>

        <link rel="stylesheet/less" href="<c:url value="/less/bootstrap.less" />" >
        <script type="text/javascript" src="<c:url value="/js/less-1.2.1.min.js" />"></script>
        <link rel="stylesheet" href="<c:url value="/css/style.css" />" />

        <script type="text/javascript">
            var baseUrl = "<c:url value="/" />";
            $(document).ready(function() {
                initHeader(${title}, ${username});
            });
        </script>
    </head>
    <body>
        <div class="navbar navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container">
                    <!-- Fill in with loadTemplate("header", user, ".navbar .container"); call -->
                </div>
            </div>
        </div>

        <div class="container">
            <div class="content">
                <!-- Fill in with loadTemplate(); call -->
            </div>
            <p class="footer">
                &COPY;&nbsp;
                Toutes les photos sont soumises au droit d'auteur. Il est interdit de les réutiliser sans l'accord explicite de leur auteur.
            </p>
        </div>

        <form id="login" class="modal hide fade form-horizontal" action="#/authenticate" method="post">
            <div class="modal-header">
                <a class="close" data-dismiss="modal">&times;</a>
                <h3>Connexion</h3>
            </div>
            <div class="modal-body">
                <p></p>
                <div class="control-group">
                    <label class="control-label" for="username">Nom d'utilisateur</label>
                    <div class="controls">
                        <input type="text" required class="input-large" id="username" name="username" />
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="password">Mot de passe</label>
                    <div class="controls">
                        <input type="password" required class="input-large" id="password" name="password" />
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <a href="#" class="btn">Annuler</a>
                <input type="submit" class="btn btn-primary" data-loading-text="Connexion en cours ..." value="Connexion" />
            </div>
        </form>
    </body>
</html>
