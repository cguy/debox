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
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!DOCTYPE html>
<html lang="fr">
    <head>
        <meta charset="utf-8">
        <title>Galerie photos</title>

        <script type="text/javascript" src="<c:url value="/js/jquery-1.7.1.min.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/jquery.rs.slideshow.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/bootstrap-modal.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/bootstrap-dropdown.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/bootstrap-button.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/bootstrap-transition.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/sammy-0.7.1.min.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/mustache.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/routes.js" />"></script>

        <link rel="stylesheet/less" href="<c:url value="/less/bootstrap.less" />" >
        <script type="text/javascript" src="<c:url value="/js/less-1.2.1.min.js" />"></script>
        <link rel="stylesheet" href="<c:url value="/css/style.css" />" />
    </head>
    <body>
        <div class="navbar navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container">
                    <a class="brand" href="#/">Galerie photos</a>
                    <ul class="nav">
                        <li><a href="#/">Liste des albums</a></li>
                    <shiro:authenticated>
                        <li><a href="#/administration">Administration</a></li>
                    </shiro:authenticated>
                    </ul>
                    <p class="navbar-text pull-right">
                    <shiro:authenticated>
                        <strong><shiro:principal /></strong> (<a href="#/logout">Déconnexion</a>)
                    </shiro:authenticated>
                    <shiro:guest>
                        <a data-toggle="modal" href="#login">Connexion</a>
                    </shiro:guest>
                    </p>
                </div>
            </div>
        </div>
        
        <div class="container">
            <div class="content">
                
            </div>
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
