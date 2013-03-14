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
app.post(/^#\/(photo|video)\/(.*)/, function() {
    $("#edit-media .alert").hide();
    var type = this.params['splat'][0];
    var id = this.params['splat'][1];
    $.ajax({
        url: type + "/" + id,
        type : "post",
        data : _("edit-media").serializeArray(),
        success: function() {
            _("edit-media").modal("hide");
        },
        error : function() {
            $("#edit-media .alert").show();
        }
    });
    return false;
});

app.del(/^#\/(photo|video)\/(.*)/, function() {
    $("#delete-media .alert").hide();
    var type = this.params['splat'][0];
    var id = this.params['splat'][1];
    $.ajax({
        url: type + "/" + id,
        type : "delete",
        success: function() {
            $("*[data-id=" + id + "]").parent().remove();
            _("delete-media").modal("hide");
        },
        error : function() {
            $("#delete-media .alert").show();
        }
    });
    return false;
});

