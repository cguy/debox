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
app.post('#/account/:accountId', function() {
    $("#personaldata input[type=submit]").button('loading');
    $("#personaldata p").addClass("hide");
    $("#personaldata p").removeClass("alert alert-error alert-success");
    $("#personaldata p").html("");
    $.ajax({
        url: "account/" + this.params['accountId'],
        type : "post",
        data : $("#personaldata").serializeArray(),
        success: function(data) {
            loadTemplate("header", {
                "username" : data.username,
                "title" : $("a.brand").text()
            }, ".navbar .container-fluid", headerTemplateLoaded);

            $("#personaldata p").text("Vos informations personnelles ont été modifiées avec succès !");
            $("#personaldata input[type=submit]").button('reset');
            $("#personaldata p").addClass("alert alert-success");
            $("#personaldata p").removeClass("hide");
        },
        error: function(xhr) {
            $("#personaldata p").text("Erreur pendant l'opération.");
            $("#personaldata input[type=submit]").button('reset');
            $("#personaldata p").addClass("alert alert-error");
            $("#personaldata p").removeClass("hide");
        }
    });
    return false;
});

app.post('#/account/:accountId/credentials', function() {
    $("#credentials input[type=submit]").button('loading');
    $("#credentials p").addClass("hide");
    $("#credentials p").removeClass("alert alert-error alert-success");
    $("#credentials p").html("");
    $.ajax({
        url: "account/" + this.params['accountId'] + "/credentials",
        type : "post",
        data : $("#credentials").serializeArray(),
        success: function() {
            $("#credentials p").text("Mot de passe modifié avec succès !");
            $("#credentials input[type=submit]").button('reset');
            $("#credentials p").addClass("alert alert-success");
            $("#credentials p").removeClass("hide");
        },
        error: function(xhr) {
            if (xhr.status == 401) {
                $("#personaldata p").text("Erreur durant l'opération, le mot de passe saisi est incorrect.");
            } else {
                $("#personaldata p").text("Erreur pendant l'opération.");
            }
            
            $("#credentials input[type=submit]").button('reset');
            $("#credentials p").addClass("alert alert-error");
            $("#credentials p").removeClass("hide");
        }
    });
    return false;
});