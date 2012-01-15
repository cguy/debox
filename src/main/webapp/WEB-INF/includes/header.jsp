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
        <script type="text/javascript" src="http://twitter.github.com/bootstrap/1.4.0/bootstrap-modal.js"></script>
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
                    
                    this.get('/#/album/:album', function() {
                        $.ajax({
                            url: "<c:url value="/api/album/" />" + this.params['album'],
                            success: function(data) {
                                var list = data.list;
                                var html = '<div class="media-grid">';
                                for (var i = 0 ; i < list.length ; i++) {
                                    html += '\n\t<a href="#/photo/' + data.albumName + '/' + data.names[i] + '"><img src="' + list[i] + '" style="width:160px;" /></a>';
                                }
                                html += '</div>';
                                $('h1').html(data.albumName);
                                $('#albums').hide();
                                $('#photo').hide();
                                $('#photos').html(html);
                                $('#photos').fadeIn();
                            }
                        });
                    }); // End route
                        
                    this.get('/#/photo/:album/:photo', function() {
                        var path = this.path.substr(1, this.path.lastIndexOf('/') - 1);
                        path = path.replace("photo", "album");
                        $.ajax({
                            url: "<c:url value="/deploy/api/photo/" />" + this.params['album'] + "/" + this.params['photo'],
                            success: function(data) {
                                var html = '<ul><li><a href="' + path + '">Retour à l\'album</a></li></ul>';
                                html += '<div class="media-grid" style="text-align: center;">';
                                html += '<a href="' + context + 'deploy/photo/' + data.album + '/' + data.photo + '" id="' + data.photo + '" style="display:inline-block; float:none;">';
                                html += '<img class="thumbnail" src="' + context + 'deploy/photo/' + data.album + '/' + data.photo + '" style="max-height:700px;max-width:100%;" />';
                                html += '</a>';
                                html += '</div>';
                                $('h1').html(data.photo + "<small>" + data.album + "</small>");
                                $('#albums').hide();
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
                        $('#albums').fadeIn();
                    }); // End route
                    
                }).run();
            });
            
        </script>

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

            .modal form {
                margin-bottom: 0;
            }

        </style>

    </head>
    <body>
        <div class="topbar">
            <div class="fill">
                <div class="container">
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

        <div class="container">

            <div class="content">