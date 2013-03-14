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
app.post('#/accounts/:accountId', function() {
    var submitButton = $("#personaldata input[type=submit]");
    var msgNode = $("#personaldata p");
    submitButton.button('loading');
    msgNode.removeClass("alert alert-error alert-success").addClass("hide").html("");
    $.ajax({
        url: "accounts/" + this.params['accountId'],
        type : "post",
        data : _("personaldata").serializeArray(),
        success: function(data) {
            loadTemplate("header", {
                "username" : data.username,
                "title" : $("a.brand").text()
            }, ".navbar .container-fluid");

            msgNode.text("Vos informations personnelles ont été modifiées avec succès !").removeClass("hide").addClass("alert alert-success");
            submitButton.button('reset');
        },
        error: function(xhr) {
            msgNode.text("Erreur pendant l'opération.").removeClass("hide").addClass("alert alert-error");
            submitButton.button('reset');
        }
    });
    return false;
});

app.post('#/accounts/:accountId/credentials', function() {
    var submitButton = $("#credentials input[type=submit]");
    var msgNode = $("#credentials p");
    submitButton.button('loading');
    msgNode.addClass("hide").removeClass("alert alert-error alert-success").html("");
    $.ajax({
        url: "accounts/" + this.params['accountId'] + "/credentials",
        type : "post",
        data : _("credentials").serializeArray(),
        success: function() {
            msgNode.text("Mot de passe modifié avec succès !").addClass("alert alert-success").removeClass("hide");
            submitButton.button('reset');
        },
        error: function(xhr) {
            if (xhr.status == 401) {
                msgNode.text("Erreur durant l'opération, le mot de passe saisi est incorrect.");
            } else {
                msgNode.text("Erreur pendant l'opération.");
            }
            msgNode.addClass("alert alert-error").removeClass("hide");
            submitButton.button('reset');
        }
    });
    return false;
});

app.post('#/accounts/:accountId/settings', function() {
    $.ajax({
        url: "accounts/" + this.params['accountId'] + "/settings",
        type : "post",
        data : _("accountSettings").serializeArray(),
        success: function(data) {
            alert("ok")
        },
        error: function(xhr) {
            alert("error")
        }
    });
    return false;
});
