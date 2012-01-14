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
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>JSP Page</title>
        <link rel="stylesheet" href="http://twitter.github.com/bootstrap/1.4.0/bootstrap.min.css" />
        <style type="text/css">
            /* Override some defaults */
            html, body {
                background-color: #eee;
            }
            body {
                padding-top: 40px; /* 40px to make the container go all the way to the bottom of the topbar */
            }
            .container > footer p {
                text-align: center; /* center align it with the container */
            }

            /* The white background content wrapper */
            .content {
                background-color: #fff;
                padding: 20px;
                margin: 0 -20px; /* negative indent the amount of the padding to maintain the grid system */
                -webkit-border-radius: 0 0 6px 6px;
                -moz-border-radius: 0 0 6px 6px;
                border-radius: 0 0 6px 6px;
                -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.15);
                -moz-box-shadow: 0 1px 2px rgba(0,0,0,.15);
                box-shadow: 0 1px 2px rgba(0,0,0,.15);
            }

            /* Page header tweaks */
            .page-header {
                background-color: #f5f5f5;
                padding: 20px 20px 10px;
                margin: -20px -20px 20px;
            }

            .topbar .btn {
                border: 0;
            }
            
            h1 small {
                padding-left: 10px;
            }

        </style>
    </head>
    <body>
        <div class="topbar">
            <div class="fill">
                <div class="container">
                    <a class="brand" href="<c:url value="/" />">Galerie photos</a>
                    <ul class="nav">
                        <li><a href="<c:url value="/" />">Liste des albums</a></li>
                    </ul>
                    <form action="" class="pull-right">
                        <input class="input-small" type="text" placeholder="Username">
                        <input class="input-small" type="password" placeholder="Password">
                        <button class="btn" type="submit">Sign in</button>
                    </form>
                </div>
            </div>
        </div>

        <div class="container">

            <div class="content">
                <div class="page-header">
                    <h1>${photo}<small>${album}</small></h1>
                </div>
                <ul>
                    <li><a href=".">Retour à l'album</a></li>
                </ul>
                <div class="media-grid" style="text-align: center;">
                    <a href="/deploy/photo/${album}/${photo}" id="${photo}" style="display:inline-block; float:none;">
                        <img class="thumbnail" src="/deploy/photo/${album}/${photo}" style="max-height:700px;" />
                    </a>
                </div>
            </div>
        </div>
    </div> <!-- /container -->
</body>
</html>
