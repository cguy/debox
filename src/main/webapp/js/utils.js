/*
 * #%L
 * debox-photos
 * %%
 * Copyright (C) 2012 Debox
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
var templatesToLoad = new Array();
var templatesLoaded = false;
var templates = {};
function loadTemplates(callback) {
    $.ajax({
        url: "tpl",
        success: function(data) {
            templates = {};
            $.each(data.templates, function (name, template) {
                templates[name] = Hogan.compile(template);
            });
            templatesLoaded = true;
            initHeader(data.config.title, data.config.username);
            for (var i = 0 ; i < templatesToLoad.length ; i++) {
                var id = templatesToLoad[i].id;
                var model = templatesToLoad[i].data;
                var selector = templatesToLoad[i].selector;
                var tplCallback = templatesToLoad[i].callback;
                loadTemplate(id, model, selector, tplCallback);
            }
            delete templatesToLoad;
            if (callback) {
                callback();
            }
        }
    });
}

function loadTemplate(templateId, data, selector, callback) {
    if (!templatesLoaded) {
        templatesToLoad.push({
            "id" : templateId, 
            "data" : data, 
            "selector" : selector, 
            "callback" : callback
        });
        return;
    }
    
    if (!data) {
        data = {};
    }
    data.i18n = lang;
    
    if (!templates[templateId]) {
        throw "Template \""+"\" doesn't exist.";
    }
    var html = templates[templateId].render(data, templates);
    if (!selector) {
        selector = "body > .container-fluid";
    }
    $(selector).html(html);
    
    if (callback) {
        callback();
    }
    
    if (templateId != "header") {
        setTimeout('$("#loading").addClass("hide");', 150);
    }
}

function ajax(object) {
    if (!object.error) {
        object.error = function(xhr) {
            var status = xhr.status;
            if (status == 404) {
                loadTemplate(status);
            } else if (status == 403) {
                loadTemplate(status);
                if ($(".brand").length > 0) {
                    loadTemplate("header", {
                        "username" : null,
                        "title" : $("a.brand").html()
                    }, ".navbar .container-fluid", headerTemplateLoaded);
                }
            } else {
//                alert(xhr.status + " : " + xhr.responseText);
            }
        }
    }
    if (!object.type || object.type == "get") {
        $("#loading").removeClass("hide");
    }
    $.ajax(object);
}
    
function computeUrl(url) {
    if (location.pathname != "/") {
        var token = location.pathname.substr(location.pathname.lastIndexOf("/") + 1, location.pathname.length - 1);
        var separator = url.indexOf("?") == -1 ? "?" : "&";
        return url + separator + "token=" + token;
    }
    return url;
}

function editTitle(title) {
    document.title = title;
}

function initHeader(title, username) {
    editTitle(title + " - Accueil");
    loadTemplate("header", {
        "title": title, 
        "username" : username
    }, ".navbar .container-fluid", headerTemplateLoaded);
}

function hideModal() {
    $(this).parents(".modal").modal("hide");
}

function handleAlertsClose() {
    $(".alert span.close").click(function(evt) {
        $(this).parents(".alert").fadeOut(250);
    });
}

function resetModalForm() {
    $(this).find("p.alert-error").text("");
    $(this).find("p.alert-error").removeClass("alert alert-error");
    $(this).each(function(){
        this.reset();
    });
}

function handleArchiveUpload() {
    $.ajax({
        type : "GET",
        url: "uploadProgress",
        success: function(progression){
            if (!progression) {
                setTimeout(handleArchiveUpload, 250);
                return;
            }
            var bytesRead = progression.bytesRead;
            var contentLength = progression.contentLength;
            var percent = Math.floor(bytesRead * 100 / contentLength);
            $("#upload-progress h3 span").html(percent + "&nbsp;%");
            $("#upload-progress .bar").css("width", percent+"%");
            if (!bytesRead || !contentLength || bytesRead != contentLength) {
                $("#upload-progress").show();
                setTimeout(handleArchiveUpload, 250);
            } else {
                $("#upload-progress").removeClass("alert-info");
                $("#upload-progress").addClass("alert-success");
                $("#upload-progress .progress").removeClass("progress-info active");
                $("#upload-progress .progress").addClass("progress-success");
            }
        }
    });
}

function loadAlbum(data, callback) {
    createAlbum(data.album);
    var subAlbums = data.subAlbums;
    if (subAlbums) {
        for (var i = 0 ; i < subAlbums.length ; i++) {
            var album = subAlbums[i];
            createAlbum(album);
        }
    }
                    
    var beginDate = new Date(data.album.beginDate).at("0:00am");
    var endDate = new Date(data.album.endDate).at("0:00am");
                    
    data.album.isInterval = !beginDate.equals(endDate);
    data.album.beginDate = beginDate.toString("dd MMMM yyyy");
    data.album.endDate = endDate.toString("dd MMMM yyyy");
                    
    data.album.minDownloadUrl = computeUrl("download/album/" + data.album.id + "/min");
    data.album.downloadUrl = computeUrl("download/album/" + data.album.id);
    
    loadTemplate("album", data, null, function() {
        onBodyScroll();
        manageRegenerationProgress(data);
        editTitle($("a.brand").text() + " - " + data.album.name);
        $("button.edit-album, button.edit-album-cancel").click(function() {
            $("#alerts .alert").hide();
            $("#edit_album").toggleClass("visible");
            $("button.edit-album").toggleClass("hide");
            $("button.edit-album-cancel").toggleClass("hide");
            hideAlbumChoose();
        });
                        
        handleAlertsClose();
                        
        var hideAlbumChoose = function() {
            $("button.choose-cover-cancel").fadeOut(250, function() {
                $("button.choose-cover").fadeIn(250);
            });
            $("#cover-photos").fadeOut(250, function() {
                $('#cover-photos *[rel|=tooltip]').tooltip('hide');
                $("#photos").fadeIn(250);
            });
        };
                        
        $("button.choose-cover-cancel").click(function() {
            hideAlbumChoose();
            $("#alerts .cover.alert-success").fadeOut(250);
            $("#alerts .cover.alert-danger").fadeOut(250);
        });
                        
        $("button.choose-cover").click(function() {
            $("#alerts .cover.alert-success").fadeOut(250);
            $("#alerts .cover.alert-danger").fadeOut(250);
            $("button.choose-cover").fadeOut(250, function() {
                $("button.choose-cover-cancel").fadeIn(250);
            });
            $("#photos").fadeOut(250, function() {
                $('#cover-photos *[rel|=tooltip]').tooltip('hide');
                $("#cover-photos").fadeIn(250, function(){
                    $(document.body).animate({
                        scrollTop: $('#cover-photos').offset().top - 50
                    }, 250);
                });
                $('#cover-photos .thumbnail').click(function() {
                    var id;
                    if ($(this).hasClass("thumbnail")) {
                        id = $(this).attr("id");
                    } else {
                        id = $(this).parents(".thumbnail").attr("id");
                    }
                    ajax({
                        url: "album/" + data.album.id + "/cover",
                        type : "post",
                        data : {
                            objectId:id
                        },
                        success: function() {
                            hideAlbumChoose();
                            $("#alerts .cover.alert-success").fadeIn(250);
                        },
                        error: function() {
                            $("#alerts .cover.alert-danger").fadeIn(250);
                        }
                    });
                });
            });
        });
        $("button.regenerate-thumbnails").click(function() {
            ajax({
                url: "album/" + data.album.id + "/regeneratethumbnails",
                type : "post",
                success: function() {
                    console.log("Thumbnails regeneration in progress");
                    manageRegenerationProgress({
                        regeneration: {
                            percent: 0
                        },
                        album: data.album
                    });
                },
                error: function(xhr) {
                    console.log("Thumbnails regeneration failed");
                }
            });
        });
        $("select#visibility").change(function() {
            if ($(this).val() == "false") {
                $("#authorizedTokensGroup").removeClass("hide")
            } else {
                $("#authorizedTokensGroup").addClass("hide")
            }
        });
        if (callback && $.isFunction(callback)) {
            callback();
        }
    });
    $("#top").click(function() {
        $("html, body").animate({
            scrollTop: 0
        });
        return false;
    })
    $("#top").hover(
        function () {
            $(this).animate({
                opacity: 0.6
            });
        },
        function () {
            $(this).animate({
                opacity: 0.3
            });
        }
    );
    $(".chzn-select").chosen(); 
}

function manageRegenerationProgress(data) {
    console.log("manageRegenerationProgress : " + data.regeneration)
    if (data.regeneration) {
        $("#regeneration-progress").show();
        $("#regeneration-progress").addClass("alert-info");
        $("#regeneration-progress .progress").addClass("progress-info active");
        $("#regeneration-progress").removeClass("alert-success alert-danger");
        $("#regeneration-progress .progress").removeClass("progress-success progress-danger");
        $("#regeneration-progress h3 #progress-label").text("Regénération en cours...");
            
        var refreshProgressBar = function(data) {
            $("#regeneration-progress h3 #progress-percentage").html(data.percent + "&nbsp;%");
            $("#regeneration-progress .bar").css("width", data.percent+"%");
            if (data.percent < 100) {
                generationTimeout = setTimeout(getGenerationStatus, 3000);
            } else {
                generationTimeout = null;
                $("#regeneration-progress").removeClass("alert-info");
                $("#regeneration-progress h3 #progress-label").text("Regénération terminée");
                $("#regeneration-progress .progress").removeClass("progress-info active");
                $("#regeneration-progress").addClass("alert-success");
                $("#regeneration-progress .progress").addClass("progress-success");
            }
        }
                        
        var getGenerationStatus = function() {
            $.ajax({
                url: "album/" + data.album.id + "/regeneratethumbnails",
                success: function(data) {
                    refreshProgressBar(data);
                }
            });
        }
        refreshProgressBar(data.regeneration);
        
    } else {
        $("#regeneration-progress").hide();
    }
}

var generationTimeout = null;

function onBodyScroll() {
    var top = $("#top");
    if (top) {
        if ($(window).scrollTop() > 200) {
            top.animate({opacity: 0.3}, 0); /* Initial setup for IE < 9 */
            top.fadeIn();
        } else {
            top.fadeOut();
        }
    }
}

function headerTemplateLoaded() {
    $('#login-dropdown').on('click', '*', function(event){
        /* stop propagation otherwise the dropdown hides */
        event.stopPropagation();
        /* return true if the submit is clicked, false otherwise */
        return event.target && event.target.id == "connect";
    });
    var resetForm = function () {
        $("#login p").html("").removeClass("alert alert-error");
        $("#login :text, #login :password").val('');
    };
    $('#login a').on('click', resetForm);
    $('html').on('click.dropdown.data-api', resetForm);

}

function prepareDynatree(allAlbums, accessibleAlbumsWithCurrentToken, targetData, parentId) {
    if (!allAlbums) {
        return;
    }
    for (var i = 0 ; i < allAlbums.length ; i++) {
        var found = false;
        for (var tokenAlbumsIndex = 0 ; tokenAlbumsIndex < accessibleAlbumsWithCurrentToken.length ; tokenAlbumsIndex++) {
            if (accessibleAlbumsWithCurrentToken[tokenAlbumsIndex].id == allAlbums[i].id) {
                found = true;
                break;
            }
        }
                                    
        var p = {
            title:allAlbums[i].name, 
            key: allAlbums[i].id, 
            isFolder: true,
            select: found,
            hideCheckbox: allAlbums[i]['public'],
            isLazy : !!allAlbums[i]['subAlbumsCount']
        };
        
        if (allAlbums[i]['public']) {
            p.addClass = "public";
            p.title += fr.administration.tokens.public_album;
        }
                                    
        prepareDynatree(allAlbums[i].subAlbums, accessibleAlbumsWithCurrentToken, p.children, allAlbums[i].id);
        if (parentId == allAlbums[i].parentId) {
            targetData.push(p);
        }
    }
}

function initDynatree(id, children) {
    $("#" + id + " .albums-access").dynatree({
        onSelect : function(checked, node) {
            var parent = node.parent;
            if (checked) {
                while (parent != null) {
                    $(parent.span).addClass("dynatree-selected");
                    parent.bSelected = true;
                    parent = parent.parent;
                }
            } else if ($(node.span).parent("li").find(".dynatree-selected").length > 0) {
                $(node.span).parent("li").find(".dynatree-selected").removeClass("dynatree-selected");
                function disableChildren(childrenArray) {
                    if (!childrenArray) return;
                    for (var i = 0 ; i < childrenArray.length ; i++) {
                        childrenArray[i].bSelected = false;
                        disableChildren(childrenArray[i].childList);
                    }
                }
                disableChildren(node.childList);
            }
        },
        onLazyRead : function(node) {
            $.ajax({
                url: "albums?parentId=" + node.data.key,
                success : function(data) {
                    
                    var albums = data.albums;
                    var childrenObject = [];
                    for (var i = 0 ; i < albums.length ; i++) {
                        var currentAlbum = albums[i];
                        
                        var found = false;
                        for (var tokenIndex = 0 ; tokenIndex < allTokens.length ; tokenIndex++) {
                            var currentToken = allTokens[tokenIndex];
                            var tokenId = $(node.li).parents("tr").attr("id");
                            if (currentToken.id == tokenId) {
                                for (var tokenAlbumsIndex = 0 ; tokenAlbumsIndex < currentToken.albums.length ; tokenAlbumsIndex++) {
                                    if (currentToken.albums[tokenAlbumsIndex].id == currentAlbum.id) {
                                        found = true;
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        
                        var currentChild = {
                            title: currentAlbum.name, 
                            key: currentAlbum.id, 
                            isFolder: true,
                            select: found,
                            hideCheckbox: currentAlbum['public'],
                            isLazy : !!currentAlbum['subAlbumsCount']
                        };
                        
                        childrenObject.push(currentChild);
                    }
                    
                    node.setLazyNodeStatus(DTNodeStatus_Ok);
                    node.addChild(childrenObject);
                },
                error : function(xhr) {
                    node.setLazyNodeStatus(DTNodeStatus_Error, {
                        tooltip: data.faultDetails,
                        info: data.faultString
                    });
                }
            });
        },
        autoCollapse: false,
        persist: false,
        imagePath: "/skin-vista",
        checkbox: true, // Show checkboxes.
        selectMode: 2, // 1:single, 2:multi, 3:multi-hier
        fx: {
            height: "toggle", 
            duration: 200
        }, // Animations, e.g. null or { height: "toggle", duration: 200 }
        noLink: true,
        children: children,
        debugLevel: 0
    });
}

function loadAdministrationTab(id, data) {
    if ($("#administration").length == 0) {
        loadTemplate("administration", null, null, function() {
            loadAdministrationTab(id, data);
        });
    } else {
        preprocessAdministrationTabLoading(id, data);
        loadTemplate("administration." + id, data, "#" + id, function() {
            $(".nav-tabs a[data-target|=\"#" + id + "\"]").tab("show");
            loadFunctions(id);
            afterAdministrationTabLoading(id, data);
        });
    }
}

function preprocessAdministrationTabLoading(id, data) {
    if (id == "albums") {
        for (var i = 0 ; i < data.albums.length ; i++) {
            var album = data.albums[i];
            album = createAlbum(album);
        }
    }
}

function afterAdministrationTabLoading(id, data) {
    if (id == "tokens") {
        allAlbums = data.albums;
        allTokens = data.tokens;
                        
        // Generates trees for tokens management
        var tokens = data.tokens;
        for (var tokenIndex = 0 ; tokenIndex < tokens.length ; tokenIndex++) {
            var treeChildren = [];
            prepareDynatree(data.albums, tokens[tokenIndex].albums, treeChildren, null);
            initDynatree(tokens[tokenIndex].id, treeChildren);
        }
    } else if (id == "upload") {
        $('#fileupload').fileupload();

        // Demo settings:
        $('#fileupload').fileupload('option', {
            maxFileSize: 20000000,
            acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
            process: [
            {
                action: 'load',
                fileTypes: /^image\/(gif|jpeg|png)$/,
                maxFileSize: 20000000 // 20MB
            },
            {
                action: 'save'
            }
            ]
        });
        $(".chzn-select").chosen({no_results_text: "No results matched"});
    }
}

function loadFunctions(partId) {
    if (partId == "configuration") {
        loadSettingsTabFunctions();
    } else if (partId == "tokens") {
        loadTokensTabFunctions();
    }
}

function loadSettingsTabFunctions() {
    $("#configuration .btn-danger").click(function() {
        $(this).parents("form").find("input[type=hidden]").val(true);
        $(this).parents("form").submit();
    });
}

function loadTokensTabFunctions() {
    $("#administration_tokens .albums button.show-tree").unbind('click');
    $("#administration_tokens .albums button.show-tree").click(function() {
        $(this).hide();
        $(this).parents(".albums").find("span, .albums-access").show();
        $(this).parents(".albums").find(".alert-success").hide();
        $(this).parents(".albums").find(".alert-error").hide();
    });

    $("#administration_tokens .albums button.cancel").unbind('click');
    $("#administration_tokens .albums button.cancel").click(function() {
        $(this).parents(".albums").find(".alert-success").hide();
        $(this).parents(".albums").find(".alert-error").hide();
        $(this).parents(".albums").find(".albums-access").hide();
        $(this).parents(".albums").find("button.btn").show();
        $(this).parents("span").hide();
    });

    $("#administration_tokens button.edit").unbind('click');
    $("#administration_tokens button.edit").click(function() {
        var id = $(this).parents("tr").attr("id");
        $.ajax({
            url: "token/" + id,
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

    // Need to refresh binding because of DOM operations
    $("button[type=reset]").unbind('click');
    $("button[type=reset]").click(hideModal);
    $('form.modal').unbind('hidden');
    $('form.modal').on('hidden', resetModalForm);

    $("#administration_tokens button.delete").unbind('click');
    $("#administration_tokens button.delete").click(function() {
        var id = $(this).parents("tr").attr("id");
        var name = $(this).parents("tr").find(".access_label").text();
        $("#modal-token-delete input[type=hidden]").val(id);
        $("#modal-token-delete p strong").text(name);
    });
}

function handleAdmin() {
    $("#cancel-sync").click(function() {
        $.ajax({
            url: "administration/sync",
            type: "delete",
            success: function() {
                if (syncTimeout != null) {
                    clearTimeout(syncTimeout);
                    syncTimeout = null;
                }
                $("#synchronization input").removeAttr("disabled");
                $("#cancel-sync").hide();
                $("#sync-progress").removeClass("alert-info");
                $("#sync-progress .progress").removeClass("progress-info active");
                $("#sync-progress").addClass("alert-danger");
                $("#sync-progress .progress").addClass("progress-danger");
            },
            error: function() {
                alert("Erreur pendant l'annulation de la synchronisation");
            }
        });
    });
}
    
function manageSync(data) {
    if (data.sync) {
        $("#sync-progress").show();
        $("#sync-progress").addClass("alert-info");
        $("#sync-progress .progress").addClass("progress-info active");
        $("#sync-progress").removeClass("alert-success alert-danger");
        $("#sync-progress .progress").removeClass("progress-success progress-danger");
        $("#progress-label").text("Synchronisation en cours...");
        $("#synchronization input").attr("disabled", "disabled");
        $("#cancel-sync").show();
            
        var refreshProgressBar = function(data) {
            $("#sync-progress h3 #progress-percentage").html(data.percent + "&nbsp;%");
            $("#sync-progress .bar").css("width", data.percent+"%");
            if (data.percent < 100) {
                syncTimeout = setTimeout(getSyncStatus, 3000);
            } else {
                syncTimeout = null;
                $("#synchronization input").removeAttr("disabled");
                $("#sync-progress").removeClass("alert-info");
                $("#progress-label").text("Synchronisation terminée");
                $("#sync-progress .progress").removeClass("progress-info active");
                $("#sync-progress").addClass("alert-success");
                $("#sync-progress .progress").addClass("progress-success");
                $("#cancel-sync").hide();
            }
        }
                        
        var getSyncStatus = function() {
            $.ajax({
                url: "administration/sync",
                success: function(data) {
                    refreshProgressBar(data);
                }
            });
        }
        refreshProgressBar(data.sync);
    }
}
    
