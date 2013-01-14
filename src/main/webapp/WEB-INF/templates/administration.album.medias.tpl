<div id="photos-edition" class="{{^inEdition}}hide{{/inEdition}}">
    {{#medias.length}}
    <ul class="thumbnails photos">
        {{#medias}}
        <li class="span2">
            <div data-id="{{id}}" class="thumbnail">
                {{#photo}}
                    <span class="picture" style="background-image:url('{{thumbnailUrl}}')"></span>
                {{/photo}}
                {{#video}}
                    <span class="picture" style="background-image:url('{{squareThumbnailUrl}}')"></span>
                {{/video}}
                <span class="title">{{title}}</span>
                <span class="actions">
                    <a href="#edit-photo" class="edit-photo"><i class="icon-edit"></i></a>
                    <a href="#delete-photo" class="delete-photo"><i class="icon-remove"></i></a>
                </span>
            </div>
        </li>
        {{/medias}}
    </ul>
    {{/medias.length}}
</div>

<form id="edit-photo" class="modal fade hide form-horizontal" method="post">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>{{i18n.photo.edit.title}}</h3>
    </div>
    <div class="modal-body">
        <div class="control-group">
            <label class="control-label" for="photoTitle">{{i18n.photo.title}}</label>
            <div class="controls">
                <input type="text" id="photoTitle" name="title" placeholder="{{i18n.photo.edit.placeholder}}">
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <input class="btn btn-primary" type="submit" value="{{i18n.common.validate}}"/>
        <button class="btn" data-dismiss="modal" type="button">{{i18n.common.cancel}}</button>
    </div>
</form>

<form id="delete-photo" class="modal fade hide" method="delete">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>{{i18n.photo.delete.title}}</h3>
    </div>
    <div class="modal-body">
        <p>{{i18n.photo.delete.confirm}}</p>
    </div>
    <div class="modal-footer">
        <input class="btn btn-danger" type="submit" value="{{i18n.common.delete}}"/>
        <button class="btn" data-dismiss="modal" type="button">{{i18n.common.cancel}}</button>
    </div>
</form>
