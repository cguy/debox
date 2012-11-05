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
app.post('#/administration/sync', function() {
    $("#synchronization input[type=submit]").button("loading");
    $.ajax({
        url: "administration/sync",
        type : "post",
        data : $("#synchronization").serializeArray(),
        success: function() {
            $("#synchronization input[type=submit]").button("reset");
            manageSync({
                sync:{
                    percent:0
                }
            });
        },
        error: function(xhr) {
            $("#synchronization input[type=submit]").button("reset");
            $("#synchronization p.error").addClass("alert alert-error");
            if (xhr.status == 409) {
                $("#synchronization p.error").text("Veuillez commencer par définir la configuration générale (dont les répertoires de travail) avant de lancer la première synchronisation.");
            } else {
                $("#synchronization p.error").text("Erreur de communication avec le serveur.");
            }
        }
    });
    return false;
});
