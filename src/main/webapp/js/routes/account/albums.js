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
app.post('#/albums/:albumId', function() {
    $("#alerts .edit.alert-success").fadeOut(250);
    $("#alerts .edit.alert-danger").fadeOut(250);
    $.ajax({
        url: "albums/" + this.params["albumId"],
        type : "post",
        data : _("edit_album form").serializeArray(),
        success: function(data) {
            data.inEdition = true;
            loadAlbum(data, function(){
                $("#alerts .edit.alert-success").fadeIn(250);
            }, "edition");
        },
        error : function() {
            $("#alerts .edit.alert-danger").fadeIn(250);
        }
    });
    return false;
});

app.del('#/albums/:albumId', function() {
    var context = this;
    ajax({
        url: "albums/" + this.params["albumId"],
        type : "delete",
        success: function() {
            _("delete-album-modal").modal("hide");
            context.redirect("#/");
        },
        error : function() {
            _("delete-album-modal").modal("hide");
            $("#alerts .delete.alert-danger").fadeIn(250);
        }
    });
    return false;
});
        
