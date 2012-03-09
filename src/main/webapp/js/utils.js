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

function loadTemplates() {
    $.ajax({
        url: baseUrl + "tpl",
        success: function(templates) {
            $.each(templates, function (name, template) {
                ich.addTemplate(name, template);
            });
            templatesLoaded = true;
            for (var i = 0 ; i < templatesToLoad.length ; i++) {
                var id = templatesToLoad[i].id;
                var data = templatesToLoad[i].data;
                var selector = templatesToLoad[i].selector;
                var callback = templatesToLoad[i].callback;
                loadTemplate(id, data, selector, callback);
            }
            delete templatesToLoad;
        }
    });
}

function loadTemplate(tplId, data, selector, callback) {
    if (!templatesLoaded) {
        templatesToLoad.push({"id" : tplId, "data" : data, "selector" : selector, "callback" : callback});
        return;
    }
    
    if (!data) {
        data = {};
    }
    data.baseUrl = baseUrl;
    var html = ich[tplId]({"data": data});
    
    if (!selector) {
        selector = ".container .content";
        selector = "body > .container-fluid";
    }
    $(selector).html(html);
    
    if (callback) {
        callback();
    }
}
    
function ajax(object) {
    if (!object.error) {
        object.error = function(xhr) {
            var status = xhr.status;
            if (status == 404 || status == 403) {
                loadTemplate(status);
            }
        }
    }
    $.ajax(object);
}
    
function computeUrl(url) {
    if (location.pathname != "/") {
        var token = location.pathname.substring(baseUrl.length, location.pathname.length - 1);
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
    }, ".navbar .container-fluid");
}

function hideModal() {
    $(this).parents(".modal").modal("hide");
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
        url: baseUrl + "uploadProgress",
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
