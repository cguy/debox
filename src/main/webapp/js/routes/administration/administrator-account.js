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
    $("#account form input[type=submit]").button('loading');

    $("#account form p").addClass("hide");
    $("#account form p").removeClass("alert alert-error alert-success");
    $("#account form p").html("");

    $.ajax({
        url: "account/" + this.params['accountId'],
        type : "post",
        data : $("#account form").serializeArray(),
        success: function(data) {
            loadTemplate("header", {
                "username" : data.username,
                "title" : $("a.brand").html()
            }, ".navbar .container-fluid", headerTemplateLoaded);

            $("#account form p").text("Identifiants de connexion modifiés avec succès !");
            $("#account form input[type=submit]").button('reset');
            $("#account form p").addClass("alert alert-success");
            $("#account form p").removeClass("hide");
        },
        error: function(xhr) {
            if (xhr.status == 401) {
                $("#account form p").text("Erreur durant l'opération, les identifiants rentrés ne correspondent pas.");
            } else {
                $("#account form p").text("Erreur pendant l'opération.");
            }

            $("#account form input[type=submit]").button('reset');
            $("#account form p").addClass("alert alert-error");
            $("#account form p").removeClass("hide");
        }
    });
    return false;
});