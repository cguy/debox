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
<!DOCTYPE html>
<html lang="fr">
    <head>
        <meta charset="utf-8">
        <title>Galerie photos</title>

        <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->

        <script type="text/javascript" src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
        <script type="text/javascript" src="https://github.com/quirkey/sammy/raw/master/lib/min/sammy-latest.min.js"></script>
        <script type="text/javascript" src="<c:url value="/js/bootstrap-modal.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/routing.js" />"></script>

        <link rel="stylesheet" href="<c:url value="/css/bootstrap.min.css" />" />
        <link rel="stylesheet" href="<c:url value="/css/style.css" />" />
    </head>
    <body>
        <div class="topbar">
            <div class="fill">
                <div class="container">
                    <a class="brand" href="#/">Galerie photos</a>
                    <ul class="nav">
                        <li><a href="#/">Liste des albums</a></li>
                        <li><a href="#/administration">Administration</a></li>
                    </ul>
                    <p class="pull-right">
                        <a href="#" data-controls-modal="login-modal" data-backdrop="true">Connexion</a>
                    </p>
                </div>
            </div>
        </div>
        <div class="container">
            <div class="content">