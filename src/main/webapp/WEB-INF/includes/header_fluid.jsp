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
        <title>JSP Page</title>

        <script type="text/javascript" src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
        <script type="text/javascript" src="<c:url value="/js/bootstrap-modal.js" />"></script>
        <script type="text/javascript" src="https://github.com/quirkey/sammy/raw/master/lib/min/sammy-latest.min.js"></script>

        <script type="text/javascript">
            var context = "<c:url value="/" />";
            String.prototype.startsWith = function(str) {return (this.match("^"+str)==str)}
            
            $(document).ready(function() {
                $("#login-cancel").click(function(){
                    $("#login-modal").modal('hide');
                });
                
                $("#add-album-cancel").click(function(){
                    $("#add-album-modal").modal('hide');
                });
                
                Sammy(function() {
                    
                    this.get('#/album/:album', function() {
                        $.ajax({
                            url: "<c:url value="/api/album/" />" + this.params['album'],
                            success: function(data) {
                                var list = data.list;
                                var html = '<ul class="thumbnails">';
                                for (var i = 0 ; i < list.length ; i++) {
                                    html += '<li class="span2">';
                                    html += '\n\t<a class="thumbnail" href="#/photo/' + data.albumName + '/' + data.names[i] + '"><img  src="' + list[i] + '" /></a>';
                                    html += '</li>';
                                }
                                html += '</div>';
                                $('h1').html(data.albumName);
                                $('#photo').hide();
                                $('#photos').fadeOut(function() {
                                    $('#photos').html(html);
                                    $('#photos').fadeIn();
                                });
                            }
                        });
                    }); // End route
                        
                    this.get('#/photo/:album/:photo', function() {
                        var path = this.path.substr(1, this.path.lastIndexOf('/') - 1);
                        path = path.replace("photo", "album");
                        $.ajax({
                            url: "<c:url value="/deploy/api/photo/" />" + this.params['album'] + "/" + this.params['photo'],
                            success: function(data) {
                                var html = '<ul><li><a href="' + path + '">Retour à l\'album</a></li></ul>';
                                html += '<div class="thumbnails" style="text-align: center;">';
                                html += '<a class="thumbnail" href="' + context + 'deploy/photo/' + data.album + '/' + data.photo + '" id="' + data.photo + '" style="display:inline-block; float:none;">';
                                html += '<img src="' + context + 'deploy/photo/' + data.album + '/' + data.photo + '" style="max-height:700px;max-width:100%;" />';
                                html += '</a>';
                                html += '</div>';
                                $('h1').html(data.photo + "<small>" + data.album + "</small>");
                                $('#photos').hide();
                                $('#photo').html(html);
                                $('#photo').fadeIn();
                            }
                        });
                    }); // End route
                    
                    this.get('/', function() {
                        $('h1').html("Accueil");
                        $('#photos').hide();
                        $('#photo').hide();
                    }); // End route
                    
                }).run();
            });
            
        </script>

        <link rel="stylesheet" href="<c:url value="/css/bootstrap.min.css" />" />
        <style type="text/css">
            body {
                padding-top: 60px;
            }
            
            h1 {
                margin-bottom: 10px;
            }
            
            .fluid-sidebar {
                margin-top: -20px;
                width:280px;
                
            }
            
            .fluid-sidebar .well {
                border-top: none;
                border-radius: 0;
            }
            
            .fluid-sidebar li {
                width: 240px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
            
            .sidebar-right {
                padding-right:300px;
            }
            
            .sidebar-right .fluid-sidebar{
                margin-right:-300px;
            }
        </style>
    </head>
    <body>
        <div class="navbar navbar-fixed">
            <div class="navbar-inner">
                <div class="fluid-container">
                    <a class="brand" href="#">Galerie photos</a>
                    <ul class="nav">
                        <li><a href="#">Liste des albums</a></li>
                        <li><a href="#<c:url value="/admin/" />">Administration</a></li>
                    </ul>
                    <p class="pull-right">
                        <a href="#" data-controls-modal="login-modal" data-backdrop="true">Connexion</a>
                    </p>
                </div>
            </div>
        </div>

        <div class="fluid-container sidebar-right">
            <div class="fluid-sidebar">
                <div class="well">
                    <h5>Albums</h5>
                    <ul id="albums" class="unstyled">
    <c:forEach items="${list}" var="item">
        <c:if test="${item.isDirectory()}">
            <li><a href="#<c:url value="/album/${item.getName()}" />">${item.getName()}</a></li>
        </c:if>
    </c:forEach>
                    </ul>
                </div>
            </div>
            <div class="fluid-content">
                