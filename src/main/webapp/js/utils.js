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
        url: computeUrl("tpl"),
        success: function(data) {
            templates = {};
            $.each(data.templates, function (name, template) {
                if (template) {
                    templates[name] = Hogan.compile(template);
                }
            });
            
            _config = data.config;
            if (_config) {
                _config.providers = data.providers;
                initHeader(data.config);
            }
            templatesLoaded = true;
            
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

function _(nodeId) {
    return $(document.getElementById(nodeId));
}

var _config = {
    administrator : false
};

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
    data.config = _config;
    data.i18n = lang;
    
    if (!templates[templateId]) {
        templateId = "404";
        callback = null;
    }
    
    var _defaultSelector = "body > .container-fluid";
    var html = templates[templateId].render(data, templates);
    if (!selector) {
        selector = _defaultSelector;
    }
    $(selector).html(html);
    
    if (callback) {
        callback();
    }
    
    var tooltipNodes = null;
    if (selector == _defaultSelector) {
        tooltipNodes = $("body > .container-fluid *[data-toggle=tooltip]");
    } else if (templateId == "header") {
        tooltipNodes = $(".navbar a[data-toggle=tooltip]");
    }
    
    if (tooltipNodes) {
        tooltipNodes.tooltip("destroy");
        tooltipNodes.tooltip();
        tooltipNodes.click(function() {
            $(this).tooltip('hide');
        });
    }
    
    if (templateId != "header") {
        setTimeout('_("loading").addClass("hide");', 100);
    }
}

function ajax(object) {
    if (!object.error) {
        object.error = function(xhr) {
            var status = xhr.status;
            if (status == 500) {
                var data = $.parseJSON(xhr.responseText);
                if (data && data.error == "ThirdPartyError") {
                    window.location = data.url;
                }
                
            } else if (status == 404) {
                loadTemplate(status);
            } else if (status == 403) {
                loadTemplate(status);
                if ($(".brand").length > 0) {
                    loadTemplate("header", {
                        "username" : null,
                        "title" : $("a.brand").html()
                    }, ".navbar .container-fluid");
                }
            } else {
                console.log(xhr.status, xhr.responseText);
            }
        }
    }
    if (!object.type || object.type == "get") {
        _("loading").removeClass("hide");
    }
    object.cache = false;
    $.ajax(object);
}

function addTransitionListener(node, callback) {
    $(node).bind("webkitTransitionEnd mozTransitionEnd oTransitionEnd msTransitionEnd transitionend", callback);
}

function computeUrl(url) {
    if (location.pathname != "/") {
        var token = location.pathname.substr(location.pathname.lastIndexOf("/") + 1, location.pathname.length - 1);
        var separator = url.indexOf("?") == -1 ? "?" : "&";
        if (token) {
            return url + separator + "token=" + token;
        } else {
            return url;
        }
    }
    return url;    
}

function editTitle(title) {
    document.title = title;
}

function initHeader(data) {
    editTitle(data.title + " - Accueil");
    loadTemplate("header", data, ".navbar .container-fluid");
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
    var alertBlocks = $(this).find("p.alert-error");
    alertBlocks.text("");
    alertBlocks.removeClass("alert alert-error");
    $(this).each(function(){
        this.reset();
    });
}

function hideAlbumChoose() {
    $("button.choose-cover-cancel").fadeOut(250, function() {
        $("button.choose-cover").fadeIn(250);
    });
    _("cover-photos").addClass("hide");
    $("#cover-photos *[data-toggle=tooltip]").tooltip('hide');
    _("photos-edition").removeClass("hide");
}

function loadComment(comment) {
    comment.date = moment(comment.date).format("DD/MM/YYYY à HH:mm:ss");
    comment.deletable = function() {
        return _config.administrator || (_config.authenticated && _config.userId == comment.user.id);
    };
    return comment;
}

function loadAlbum(data, callback, mode) {
    // Process album
    createAlbum(data.album);
    var subAlbums = data.subAlbums;
    if (subAlbums) {
        for (var i = 0 ; i < subAlbums.length ; i++) {
            var album = subAlbums[i];
            createAlbum(album);
        }
    }
                    
    // Process photos
    data.album.photos = data.medias;
    
    // Process comments
    if (data.comments) {
        for (var i = 0 ; i < data.comments.length ; i++) {
            data.comments[i] = loadComment(data.comments[i]);
        }
    }
    
    loadTemplate("album", data, null, function() {
        onBodyScroll();
        manageRegenerationProgress(data);
        editTitle($("a.brand").text() + " - " + data.album.name);
        handleAlertsClose();
                        
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
            $("#photos-edition").addClass("hide");
            $('#cover-photos *[rel|=tooltip]').tooltip('hide');
            $("#cover-photos").removeClass("hide");
            $(document.body).animate({
                scrollTop: $('#cover-photos').offset().top - 50
            }, 250);
            $('#cover-photos .thumbnail').click(function() {
                var id;
                if ($(this).hasClass("thumbnail")) {
                    id = $(this).attr("data-id");
                } else {
                    id = $(this).parents(".thumbnail").attr("data-id");
                }
                ajax({
                    url: "albums/" + data.album.id + "/cover",
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
        $("button.regenerate-thumbnails").click(function() {
            ajax({
                url: "albums/" + data.album.id + "/regeneratethumbnails",
                type : "post",
                success: function() {
                    manageRegenerationProgress({
                        regeneration: {
                            percent: 0
                        },
                        album: data.album
                    });
                },
                error: function(xhr) {
                }
            });
        });
        $("select#visibility").change(function() {
            if ($(this).val() == "false") {
                _("authorizedTokensGroup").removeClass("hide")
            } else {
                _("authorizedTokensGroup").addClass("hide")
            }
        });
        if (callback && $.isFunction(callback)) {
            callback(mode);
        }
    });
    _("top").click(function() {
        $("html, body").animate({
            scrollTop: 0
        });
        return false;
    })
    _("top").hover(
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
    $(".chzn-select").select2(); 
    
    _("album-comments").mCustomScrollbar({
        scrollInertia: 500,
        mouseWheel: 50,
        advanced:{
            updateOnContentResize: true
        }
    });
}

function albumLoaded(mode) {
    if (!mode || mode == "edition" || mode == "comments") {
        $("#alerts .alert").hide();
                
        hideAlbumChoose();
        if (mode == "edition") {
            _("edit_album").addClass("visible");
            $(".edit-album").addClass("hide");
            $(".edit-album-cancel").removeClass("hide");
                    
            $("#photos-edition").removeClass("hide");
            $("#photos").addClass("hide");
                    
        } else {
            _("edit_album").removeClass("visible");
            $(".edit-album").removeClass("hide");
            $(".edit-album-cancel").addClass("hide");
                    
            $("#photos-edition").addClass("hide");
            $("#photos, .photos").removeClass("hide");
        }
                
        var oldHref = $(".page-header .comments").attr("href");
        $('.page-header .comments').tooltip('destroy');
        if (mode == "comments") {
            $("#album-content").addClass("comments");
            $(".page-header .comments").addClass("active");
            $(".page-header .comments").attr("href", oldHref.replace("/comments", ""));
                    
            $(".page-header .comments").attr("title", lang.comments.hide);
                    
        } else {
            if (!/\/comments$/.test(oldHref)) {
                $(".page-header .comments").attr("href", oldHref + "/comments");
            }
            $("#album-content").removeClass("comments");
            $(".page-header .comments").removeClass("active");
            $(".page-header .comments").attr("title", lang.comments.show);
        }
        $('.page-header .comments').tooltip();
    }
            
    // Album deletion binding
    $(".delete").unbind("click");
    $(".delete").click(function() {
        $("#delete-album-modal").modal();
    });
    
    // Photo deletion binding
    $(".delete-media").unbind("click");
    $(".delete-media").click(function() {
        var mediaId = $(this).parents("div").attr("data-id");
        var isVideo = !!$(this).parents("div").attr("data-video");
        var path = "photos";
        if (isVideo) {
            path = "videos";
        }
        $("#delete-media").attr("action", "#/" + path + "/"+mediaId);
        $("#delete-media").modal();
        return false;
    });

    // Photo edition binding
    $(".edit-media").unbind("click");
    $(".edit-media").click(function() {
        var mediaId = $(this).parents("div").attr("data-id");
        var isVideo = !!$(this).parents("div").attr("data-video");
        var path = "photos";
        if (isVideo) {
            path = "videos";
        }
        $("#edit-media").attr("action", "#/" + path + "/"+mediaId);
                
        var refTitleNode = $(this).parents("div").children(".title");
        $("#edit-media #mediaTitle").val(refTitleNode.text());
        $("#edit-media").modal();
        return false;
    });
    
    bindAlbumCommentDeletion();
}

function bindAlbumCommentDeletion() {
    var removeButton = $("#album-comments .comment .remove");
    removeButton.tooltip("destroy");
    removeButton.tooltip();
    removeButton.unbind("click");
    removeButton.click(function() {
        var commentId = $(this).parents(".comment").attr("id");
        var modal = _("remove-comment");
        var oldUrl = modal.attr("data-action");
        modal.attr("action", oldUrl.substring(0, oldUrl.lastIndexOf("/") + 1) + commentId);
        modal.modal();
        return false;
    });
}

function bindPhotoCommentDeletion() {
    var removeButton = $("#slideshow-comments .comment .remove");
    removeButton.tooltip("destroy");
    removeButton.tooltip();
    removeButton.unbind("click");
    removeButton.click(function() {
        var commentId = $(this).parents(".comment").attr("id");
        var modal = _("remove-comment");
        modal.attr("action", "#/photos/" + s.items[s.index].id + "/comments/" + commentId);
        modal.modal();
        return false;
    });
}

function manageRegenerationProgress(data) {
    if (data.regeneration) {
        _("regeneration-progress").removeClass("alert-success alert-danger").addClass("alert-info").show();
        $("#regeneration-progress .progress").removeClass("progress-success progress-danger").addClass("progress-info active");
        $("#regeneration-progress h3 #progress-label").text("Regénération en cours...");
            
        var refreshProgressBar = function(data) {
            $("#regeneration-progress h3 #progress-percentage").html(data.percent + "&nbsp;%");
            $("#regeneration-progress .bar").css("width", data.percent+"%");
            if (data.percent < 100) {
                generationTimeout = setTimeout(getGenerationStatus, 3000);
            } else {
                generationTimeout = null;
                _("regeneration-progress").removeClass("alert-info").addClass("alert-success");
                $("#regeneration-progress .progress").removeClass("progress-info active").addClass("progress-success");
                $("#regeneration-progress h3 #progress-label").text("Regénération terminée");
            }
        }
                        
        var getGenerationStatus = function() {
            $.ajax({
                url: "albums/" + data.album.id + "/regeneratethumbnails",
                success: function(data) {
                    refreshProgressBar(data);
                }
            });
        }
        refreshProgressBar(data.regeneration);
        
    } else {
        _("regeneration-progress").hide();
    }
}

var generationTimeout = null;

function onBodyScroll() {
    var top = _("top");
    if (top) {
        if ($(window).scrollTop() > 200) {
            top.animate({
                opacity: 0.3
            }, 0); /* Initial setup for IE < 9 */
            top.fadeIn();
        } else {
            top.fadeOut();
        }
    }
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
            hideCheckbox: allAlbums[i]['publicAlbum'],
            isLazy : !!allAlbums[i]['subAlbumsCount']
        };
        
        if (allAlbums[i]['publicAlbum']) {
            p.addClass = "public";
            p.title += lang.account.tokens.public_album;
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
                            if (currentToken.left.id == tokenId) {
                                for (var tokenAlbumsIndex = 0 ; tokenAlbumsIndex < currentToken.right.length ; tokenAlbumsIndex++) {
                                    if (currentToken.right[tokenAlbumsIndex].id == currentAlbum.id) {
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
                            hideCheckbox: currentAlbum['publicAlbum'],
                            isLazy : !!currentAlbum['subAlbumsCount']
                        };
                        
                        childrenObject.push(currentChild);
                    }
                    
                    node.setLazyNodeStatus(DTNodeStatus_Ok);
                    node.addChild(childrenObject);
                },
                error : function(data) {
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

function loadTab(id, data, container, cb) {
    if (!container) {
        container = "administration";
    }
    if (_(container).length == 0) {
        loadTemplate(container, null, null, function() {
            loadTab(id, data, container, cb);
        });
    } else {
        preprocessTabLoading(id, data);
        loadTemplate(container + "." + id, data, "#" + container, function() {
            $(".account li").removeClass("active");
            $(".account li." + id).addClass("active");
            if (_("account").length) {
                _("account")[0].className = id;
            }
            loadFunctions(id);
            afterTabLoading(id, data);
            if (cb) {
                cb();
            }
        });
    }
}

function preprocessTabLoading(id, data) {
    if (id == "configuration") {
        data.thirdPartyActivation = data.thirdPartyActivation == "true";
        data.workingDirectory = data["working.directory"];
        
    } else if (id == "comments") {
        if (data.comments) {
            for (var i = 0 ; i < data.comments.length ; i++) {
                data.comments[i] = loadComment(data.comments[i]);
            }
        }
    } else if (id == "albums") {
        for (var i = 0 ; i < data.albums.length ; i++) {
            var album = data.albums[i];
            album = createAlbum(album);
        }
    } else if (id == "tokens") {
        for (i = 0 ; i < data.tokens.length ; i++) {
            data.tokens[i].key.url = window.location.protocol + "//" + window.location.host + window.location.pathname + data.tokens[i].key.id;
        }
    }
}

function afterTabLoading(id, data) {
    if (id == "personaldata") {
        _('delete-account-confirm').on('hidden', function () {
            location.hash = "#/account";
        });
        
    } else if (id == "comments") {
        if ($("#admin-comments").length) {
            var datatable = $('.table').dataTable({
                "oLanguage": lang.account.comments.table,
                "iDisplayLength" : 50
            });
            datatable.fnSort([[0,'desc']]);

            $("#admin-comments .btn-danger").click(function() {
                var node = _("modal-comment-delete");
                node.attr("action", node.attr("data-action") + $(this).parents("tr").attr("id"));
            });
        }
    } else if (id == "albums") {
        var clickCallback = function() {
            var button = $(this); 
            var albumId = button.parents("a").attr("id");
            button.button('loading');
            var data = {};
            if (button.hasClass("private")) {
                data.visibility = "false";
            } else if (button.hasClass("public")) {
                data.visibility = "true";
            } else if (button.hasClass("downloadable")) {
                data.downloadable = "true";
            } else if (button.hasClass("undownloadable")) {
                data.downloadable = "false";
            }
            $.ajax({
                url: "albums/" + albumId,
                type : "post",
                data : data,
                success : function(data) {
                    var container = button.parents("li");
                    loadTemplate("account.albums.album", data.album, container, function() {
                        container.find(".btn").click(clickCallback);
                    });
                    button.button('reset');
                },
                error : function() {
                    alert(lang.common.error);
                    button.button('reset');
                }
            });

            return false; 
        };
        $(".album.admin.thumbnail .btn").click(clickCallback);
    
    } else if (id == "configuration") {
        
    } else if (id == "tokens") {
        allAlbums = data.albums;
        allTokens = data.tokens;
        
        // Generates trees for tokens management
        var tokens = data.tokens;
        for (var tokenIndex = 0 ; tokenIndex < tokens.length ; tokenIndex++) {
            var treeChildren = [];
            prepareDynatree(data.albums, tokens[tokenIndex].value, treeChildren, null);
            initDynatree(tokens[tokenIndex].key.id, treeChildren);
        }
        
        $("a.delete-third-party-account").click(function() {
            var id = $(this).parents("tr").attr("id");
            $("#delete-third-party-account .third-party-account-id").val(id);
        });
        
    } else if (id == "upload") {
        $("#fileupload input").attr("disabled", true);
        $("#fileupload .btn").addClass("disabled");

        var albums = [];
        for (var i = 0 ; i < data.albums.length ; i++) {
            var current = data.albums[i];
            var p = {
                title: current.name, 
                key: current.id, 
                isFolder: true,
                select: false,
                isLazy : !!current['subAlbumsCount']
            };
            albums.push(p);
        }
        
        var root = {
            title: lang.home.title, 
            key: "", 
            isFolder: true,
            select: true,
            activate: true,
            children: albums
        };
        
        var dynatreeInit = {
            onLazyRead : function(node) {
                node.removeChildren(false, true);
                node.setLazyNodeStatus(DTNodeStatus_Loading);
                var eventType = "nodeLoaded.dynatree." + node.tree.$tree.attr("id") + "." + node.data.key;
                $.ajax({
                    url: "albums?parentId=" + node.data.key,
                    success : function(data) {
                        var prevPhase = node.tree.phase;
                        node.tree.phase = "init";

                        var albums = data.albums;
                        var childrenObject = [];
                        for (var i = 0 ; i < albums.length ; i++) {
                            var currentAlbum = albums[i];
                            var currentChild = {
                                title: currentAlbum.name,
                                key: currentAlbum.id,
                                isFolder: true,
                                isLazy : !!currentAlbum['subAlbumsCount']
                            };
                            childrenObject.push(currentChild);
                        }
                        node.addChild(childrenObject, null);
                        node.tree.phase = "postInit";

                        //fire event nodeLoaded
                        node.tree.$tree.trigger(eventType, [node, true]);
                        node.tree.phase = prevPhase;
                        node.setLazyNodeStatus(DTNodeStatus_Ok);
                    },
                    
                    error : function(xhr) {
                        node.tree.$tree.trigger(eventType, [node, false]);
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
            classNames: {
                checkbox: "dynatree-radio"
            },
            selectMode: 1, // 1:single, 2:multi, 3:multi-hier
            fx: {
                height: "toggle", 
                duration: 200
            },
            noLink: true,
            debugLevel: 0
        };
        
        var albumIdDynatreeInit = $.extend({
            children: albums,
            onSelect: function(checked, node) {
                if (checked) {
                    setTargetAlbum(node.data.key, node.data.title);
                } else {
                    resetTargetAlbum();
                }
            }
        }, dynatreeInit);
        
        var parentIdDynatreeInit = $.extend({
            children: root,
            onSelect: function(checked, node) {
                $("#parentId").val("");
                if (checked) {
                    $("#parentId").val(node.data.key);
                }
            }
        
        }, dynatreeInit);
        
        $(".dynatree.albumId").dynatree(albumIdDynatreeInit);
        $(".dynatree.parentId").dynatree(parentIdDynatreeInit);
        
        _('fileupload').fileupload();
        _('fileupload').fileupload('option', {
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
    }
}

function setTargetAlbum(id, name) {
    _("albumId").val(id);
    var targetAlbumLink = $("#targetAlbum a");
    var base = targetAlbumLink.attr("data-href");
    targetAlbumLink.attr("href", base + id).text(name);
    _("targetAlbum").slideDown(500);
    $("#fileupload input").removeAttr("disabled");
    $("#fileupload .btn").removeClass("disabled");
}

function resetTargetAlbum() {
    _("albumId").val("");
    $("#targetAlbum strong").text("");
    _("targetAlbum").slideUp(500);
    $("#fileupload input").attr("disabled", true);
    $("#fileupload .btn").addClass("disabled");
}

function updateAlbumTreeAfterAlbumCreation(parentPath, item) {
    $(".alert.noAlbums").slideUp(500);
    var tree = $(".dynatree.albumId").dynatree("getTree");
    tree.visit(function(node) {
        if (node.isLazy()) {
            node.resetLazy();
        } else {
            node.expand(false);
        }
        node.select(false);
    });
    // if the new album is added to the root
    if (!parentPath) {
        var parentNode = tree.getRoot();
        parentNode.addChild(item);
        tree.selectKey(item.key);
        
    } else {
        // set the lazyness of the parent album if it is an root album
        var segments = parentPath.split(tree.options.keyPathSeparator);
        var parentId = null;
        if (segments.length == 1) {
            parentId = segments[0];

        } else if (segments.length == 2 && segments[0] == "") {
            parentId = segments[1];
        }
        if (parentId) {
            tree.getNodeByKey(parentId).data.isLazy = true;
        }

        // show and select the newly created album
        tree.loadKeyPath(parentPath + "/" + item.key, function(node, status) {
            if (status == "ok") {
                // 'node' is the end node of our path.
                node.makeVisible();
                node.select();
            }
        });
    }
}

function updateParentTreeAfterAlbumCreation(parentId, item) {
    var tree = $(".dynatree.parentId").dynatree("getTree");
    // add the newly created album to its parent
    tree.getNodeByKey(parentId).addChild(item);
    // select the root
    tree.selectKey("");
    tree.visit(function(node) {
        node.expand(false);
    });
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
        var form = $(this).parents("form");
        form.find("input[type=hidden]").val(true);
        form.parents("form");
    });
}

function loadTokensTabFunctions() {
    $("#administration_tokens .albums button.show-tree").unbind('click').click(function() {
        $(this).hide();
        var albums = $(this).parents(".albums");
        albums.find("span, .albums-access").show();
        albums.find(".alert-success").hide();
        albums.find(".alert-error").hide();
    });

    $("#administration_tokens .albums button.cancel").unbind('click').click(function() {
        var albums = $(this).parents(".albums");
        albums.find(".alert-success").hide();
        albums.find(".alert-error").hide();
        albums.find(".albums-access").hide();
        albums.find("button.btn").show();
        $(this).parents("span").hide();
    });

    $("#administration_tokens button.edit").unbind('click').click(function() {
        var id = $(this).parents("tr").attr("id");
        $.ajax({
            url: "token/" + id,
            success: function(data) {
                $("#edit_token input[type=hidden]").val(data.token.id);
                $("#edit_token #label").val(data.token.label);
                $("#edit_token #albums option").removeAttr("selected");

                if (data.token.albums) {
                    for (var i = 0 ; i < data.token.albums.length ; i++) {
                        $("#edit_token #albums option[value=" + data.token.albums[i].id + "]").attr("selected", "selected");
                    }
                }

                $("#edit_token").modal();
            }
        });
    });

    // Need to refresh binding because of DOM operations
    $("button[type=reset]").unbind('click').click(hideModal);
    $('form.modal').unbind('hidden').on('hidden', resetModalForm);

    $("#administration_tokens button.delete").unbind('click').click(function() {
        var id = $(this).parents("tr").attr("id");
        var name = $(this).parents("tr").find(".access_label").text();
        $("#modal-token-delete input[type=hidden]").val(id);
        $("#modal-token-delete p strong").text(name);
    });
    
    $("#administration_tokens button.reinit").unbind('click').click(function() {
        var id = $(this).parents("tr").attr("id");
        $("#" + id + " .access_link .alert-success").hide();
        var name = $(this).parents("tr").find(".access_label").text();
        $("#modal-token-reinit input[type=hidden]").val(id);
        $("#modal-token-reinit p strong").text(name);
    });
    
    $(".accessShare").unbind('click').click(function() {
        $(this).select();
    });
    
    $(".accessShare").unbind('keydown').keydown(function(event) {
        // do nothing if the user did not press ctrl+c
        if (!event.ctrlKey || event.which != 67) {
            event.preventDefault();
        }
    });

    $(".accessShare").unbind('change').change(function() {
        $(this).val($(this).attr("data-original"));
    });
}

var syncTimeout = null;
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
                _("cancel-sync").hide();
                _("sync-progress").removeClass("alert-info").addClass("alert-danger");
                $("#sync-progress .progress").removeClass("progress-info active").addClass("progress-danger");
            },
            error: function() {
                alert("Erreur pendant l'annulation de la synchronisation");
            }
        });
    });
}
    
function manageSync(data) {
    if (data.sync) {
        _("sync-progress").show().removeClass("alert-success alert-danger").addClass("alert-info");
        _("cancel-sync").show();
        $("#sync-progress .progress").removeClass("progress-success progress-danger").addClass("progress-info active");
        _("progress-label").text("Synchronisation en cours...");
        $("#synchronization input").attr("disabled", "disabled");
            
        var refreshProgressBar = function(data) {
            $("#sync-progress h3 #progress-percentage").html(data.percent + "&nbsp;%");
            $("#sync-progress .bar").css("width", data.percent+"%");
            if (data.percent < 100) {
                syncTimeout = setTimeout(getSyncStatus, 3000);
            } else {
                clearTimeout(syncTimeout);
                syncTimeout = null;
                $("#synchronization input").removeAttr("disabled");
                _("sync-progress").removeClass("alert-info");
                _("progress-label").text("Synchronisation terminée");
                $("#sync-progress .progress").removeClass("progress-info active").addClass("progress-success");
                _("sync-progress").addClass("alert-success");
                _("cancel-sync").hide();
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
    
