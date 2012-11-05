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
app.post('#/photo/:id', function() {
    $.ajax({
        url: "photo/" + this.params["id"],
        type : "post",
        data : $("#edit-photo").serializeArray(),
        success: function() {
            $("#edit-photo").modal("hide");
        },
        error : function() {
        }
    });
    return false;
});

app.del('#/photo/:id', function() {
    var id = this.params["id"];
    $.ajax({
        url: "photo/" + id,
        type : "delete",
        success: function() {
            $("*[data-id=" + id + "]").parent().remove();
            $("#delete-photo").modal("hide");
        },
        error : function() {
        }
    });
    return false;
});

