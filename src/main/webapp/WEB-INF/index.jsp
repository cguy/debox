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

        <script type="text/javascript">
            var baseUrl = "<c:url value="/" />";
            var templates = {};
            
            function loadTemplate(tplId, data, selector, callback) {
                function render(strTemplate, data, selector, callback) {
                    if (data) {
                        data.baseUrl = baseUrl;
                    }
                    var html = Mustache.render(strTemplate, {
                        data : data
                    });
                    $(selector).html(html);
                    if (callback) {
                        callback();
                    }
                }
                
                if (!selector) {
                    selector = ".container .content";
                }
                
                if (!templates[tplId]) {
                    $.ajax({
                        url: "/templates/" + tplId + ".tpl?t="+new Date().getTime(),
                        success: function(tpl) {
                            templates[tplId] = tpl;
                            render(templates[tplId], data, selector, callback);
                        }
                    });
                } else {
                    render(templates[tplId], data, selector, callback);
                }
            }
    
            function ajax(object) {
                if (!object.error) {
                    object.error = function(xhr) {
                        if (xhr.status == 404) {
                            loadTemplate(xhr.status);
                        }
                    }
                }
                $.ajax(object);
            }
    
            function handleAdmin() {
                $("#administration_albums button").click(function() {
                    var id = $(this).parent().parent().attr("id");
                    $.ajax({
                        url: baseUrl + "album/" + id,
                        success: function(data) {
                            console.log(data);
                            $("#edit_album input[type=hidden]").val(data.id);
                            $("#edit_album #name").val(data.name);
                            $("#edit_album #visibility option").removeAttr("selected");
                            $("#edit_album #visibility option[value=" + data.visibility.toLowerCase() + "]").attr("selected", "selected");
                            $("#edit_album").modal();
                        }
                    });
                });
        
                $("#administration_tokens button").click(function() {
                    var id = $(this).parent().parent().attr("id");
                    $.ajax({
                        url: baseUrl + "token/" + id,
                        success: function(data) {
                            $("#edit_token input[type=hidden]").val(data.token.id);
                            $("#edit_token #label").val(data.token.label);
                            $("#edit_token #albums option").removeAttr("selected");
                            
                            for (var i = 0 ; i < data.token.albums.length ; i++) {
                                $("#edit_token #albums option[value=" + data.token.albums[i].id + "]").attr("selected", "selected");
                            }
                    
                            $("#edit_token").modal();
                        }
                    });
                });
        
                $("button[type=reset]").click(function() {
                    $(this).parent().parent().modal("hide");
                });
            }
            
            $.getDocHeight = function(){
                return Math.max(
                    $(document).height(),
                    $(window).height(),
                    /* For opera: */
                    document.documentElement.clientHeight
                );
            };
            
            function exitFullscreen() {
                var elt = document.getElementById("fullscreenContainer");
                document.body.removeChild(elt);
                var controls = document.getElementById("rs-controls-slideshow-div");
                if (controls) {
                    document.body.removeChild(controls);
                }
                console.log(location.hash.substring(0, location.hash.lastIndexOf("/")));
                location.hash = location.hash.substring(0, location.hash.lastIndexOf("/"));
            }
            
            function createBg() {
                var container = document.createElement("div");
                container.id = "fullscreenContainer";
                container.style.height = $.getDocHeight() + "px";
                
                var elt = document.createElement("div");
                elt.id = "slideshow-div";
                elt.className = "rs-slideshow";
                elt.style.position = "fixed";
                elt.style.top = "0px";
                elt.style.left = "0px";
                elt.style.height = Math.round(window.innerHeight) + "px";
                elt.onclick = exitFullscreen;

                container.appendChild(elt);
                return container;
            }
            
            function fullscreen(index, data) {
                var elt = createBg();
                document.body.appendChild(elt);
                $('#slideshow-div').rsfSlideshow({
                    autostart : false,
                    transition: 500,
                    slides: data,
                    controls: {
                        previousSlide: {
                            auto: true
                        },    //    auto-generate a "previous slide" control
                        nextSlide: {
                            auto: true
                        }    //    auto-generate a "next slide" control
                    },
                    effect: 'fade'
                });
                $('#slideshow-div').rsfSlideshow(
                    'goToSlide', index
                );
            }
            
            function computeUrl(url) {
                if (location.pathname != "/") {
                    var token = location.pathname.substring(1, location.pathname.length - 1);
                    var separator = url.indexOf("?") == -1 ? "?" : "&";
                    return url + separator + "token=" + token;
                }
                return url;
            }
            
            $(document).ready(function() {
                <shiro:guest>loadTemplate("header", null, ".navbar .container");</shiro:guest>
                <shiro:authenticated>loadTemplate("header", {username:"<shiro:principal />"}, ".navbar .container");</shiro:authenticated>
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
