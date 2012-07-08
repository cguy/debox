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
app.del("#/third-party-account", function() {
    var id = $("#delete-third-party-account .third-party-account-id").val();
    ajax({
        url: "third-party-account/" + id,
        type: "delete",
        success: function() {
            $("#" + id).remove();
            if ($("#thirdparty_accounts tr").length == 1) {
                $("#thirdparty_accounts").remove();
            }
            $("#delete-third-party-account").modal("hide");
        }
    });
});

app.put('#/token', function() {
    $.ajax({
        url: "token?label=" + encodeURIComponent(this.params["label"]),
        type : "put",
        success: function(data) {
            $("#administration_tokens").removeClass("hide");
            $("#tokens p.alert-warning").addClass("hide");

            var treeChildren = [];
            prepareDynatree(allAlbums, [], treeChildren, null);

            data.i18n = lang;
            data.url = window.location.protocol + "//" + window.location.host + window.location.pathname + data.id;
            var html = templates["administration.tokens.row"].render(data, templates);
            $("#administration_tokens tbody").append(html);

            initDynatree(data.id, treeChildren);

            $("#form-token-create input[type=text]").val("");
            loadFunctions("tokens");
        }
    });
    return false;
});

app.post('#/token/reinit', function() {
    var id = $("#modal-token-reinit input[type=hidden]").val();
    $.ajax({
        url: "token/reinit/" + id,
        type: "post",
        success: function(data) {
            $("#" + id).attr("id", data.id);
            $("#" + data.id + " .access_link a").attr("href", data.id + "#/");
            $("#" + data.id + " .access_link .alert-success").show();

            var form = $("#" + data.id + " .album-access-form");
            form.attr("action", form.attr("action").replace(id, data.id));
            $("#modal-token-reinit").modal("hide");
            loadFunctions("tokens");
        },
        error : function(xhr) {
            $("#modal-token-reinit input[type=submit]").button('reset');
            $("#modal-token-reinit p:first-of-type").addClass("alert alert-error");
            console.log(lang.administration.tokens.reinit.error)
            var errorMessage;
            if (xhr.status == 404) {
                errorMessage = lang.administration.tokens.reinit.error404
            } else {
                errorMessage = lang.administration.tokens.reinit.error
            }
            $("#modal-token-reinit p:first-of-type").html(errorMessage);
        }
    });
    return false;
});

app.post('#/token/:token', function() {
    var token = this.params["token"];
    $("#" + token + " .albums .alert-success").hide();
    $("#" + token + " .albums .alert-error").hide();

    var tree = $("#" + token + " .album-access-form .albums-access").dynatree("getTree");

    var albums = tree.serializeArray();
    var tokenData = $("#" + token + " .album-access-form").serializeArray();
    var data = albums.concat(tokenData);

    function processIgnoreList(children, ignoreList) {
        if (children == null) {
            return;
        }
        for (var i = 0 ; i < children.length ; i++) {
            var child = children[i];
            if (child.childList) {
                processIgnoreList(child.childList, ignoreList);
            } else if (child.data.isLazy) {
                ignoreList.push({name:"ignore", value:child.data.key});
            }
        }
    }
    var children = tree.getRoot().childList;
    processIgnoreList(children, data);

    $.ajax({
        url: "token/" + token,
        type : "post",
        data: data,
        success: function(data) {
            $("#" + data.id + " .access_label").text(data.label);
            $("#" + data.id + " .album-access-form .albums-access").hide();
            $("#" + data.id + " .album-access-form button.btn").show();
            $("#" + data.id + " .albums .alert-success").show();
            $("#" + data.id + " .album-access-form span").hide();
        },
        error : function() {
            $("#" + token + " .albums .alert-error").show();
        }
    });
    return false;
});

app.post('#/token', function() {
    $.ajax({
        url: "token",
        type : "post",
        data: $("#edit_token").serializeArray(),
        success: function(data) {
            $("#" + data.id + " .access_label").text(data.label);
            $("#edit_token").modal("hide");
        }
    });
    return false;
});

app.del('#/token', function() {
    var id = $("#modal-token-delete input[type=hidden]").val();
    $.ajax({
        url: "token/" + id,
        type: "delete",
        success: function() {
            $("#" + id).remove();
            if ($("#administration_tokens").find("tbody tr").length == 0) {
                $("#administration_tokens").addClass("hide");
                $("#tokens p.alert-warning").removeClass("hide");
            }
            $("#modal-token-delete").modal("hide");
        },
        error : function(xhr) {
            $("#modal-token-delete input[type=submit]").button('reset');
            $("#modal-token-delete p:first-of-type").addClass("alert alert-error");
            $("#modal-token-delete p:first-of-type").html("Erreur pendant la suppression de l'accès, veuillez réessayer ultérieurement.");
        }
    });
    return false;
});
