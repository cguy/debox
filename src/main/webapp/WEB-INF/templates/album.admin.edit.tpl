    <button class="btn pull-right edit-album {{#inEdition}}hide{{/inEdition}}"><i class="icon-pencil"></i>&nbsp;{{i18n.album.admin.edit.modify_this}}</button>
    <button class="btn btn-info pull-right edit-album-cancel {{^inEdition}}hide{{/inEdition}}"><i class="icon-remove"></i>&nbsp;{{i18n.album.admin.edit.close_notif_zone}}</button>
</div>

<div id="edit_album" class="{{#inEdition}}visible{{/inEdition}}">
    <div id="regeneration-progress" class="alert alert-info hide">
        <h3 class="alert-heading" style="line-height:50px;">
            <span id="progress-label">{{i18n.administration.sync.in_progress}}&hellip;</span>&nbsp;<span id="progress-percentage"></span>
        </h3>
        <div class="progress progress-info progress-striped active" style="margin:0 -20px 10px 0">
            <div class="bar"></div>
        </div>
    </div>

    <div id="alerts">
        <p class="hide cover alert alert-success">{{i18n.album.admin.edit.choose_cover.success}}<span class="close">&times;</span></p>
        <p class="hide cover alert alert-danger">{{i18n.album.admin.edit.choose_cover.error}}<span class="close">&times;</span></p>
        <p class="hide edit alert alert-success">{{i18n.album.admin.edit.success}}<span class="close">&times;</span></p>
        <p class="hide edit alert alert-danger">{{i18n.album.admin.edit.error}}<span class="close">&times;</span></p>
    </div>
    
    <div class="row">
        <div class="span6">
            <form class="form-horizontal" action="#/album/{{album.id}}" method="post">
                <h2 class="page-header">{{i18n.album.admin.edit.modify}} <span>{{album.name}}</span></h2>
                <p></p>
                <div class="control-group">
                    <label class="control-label" for="name">{{i18n.album.admin.edit.album_name}}</label>
                    <div class="controls">
                        <input type="text" required class="input-large" id="name" name="name" value="{{album.name}}" />
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="visibility">{{i18n.album.admin.edit.album_visibility}}</label>
                    <div class="controls">
                        <select name="visibility" id="visibility">
                            <option value="true" {{#album.public}}selected{{/album.public}}>{{i18n.common.public}}</option>
                            <option value="false" {{^album.public}}selected{{/album.public}}>{{i18n.common.private}}</option>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="downloadable">{{i18n.album.admin.edit.download}}</label>
                    <div class="controls">
                        <label class="checkbox">
                            <input id="downloadable" type="checkbox" name="downloadable" {{#album.downloadable}}checked{{/album.downloadable}}/> {{i18n.album.admin.edit.download_description}}
                        </label>
                    </div>
                </div>
                <div class="form-actions">
                    <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.common.modification_in_progress}}" value="{{i18n.common.modify}}" />
                </div>
            </form>
        </div>
        <div class="span6 actions">
            <h2 class="page-header">{{i18n.album.admin.edit.actions}}</h2>
            <button class="btn choose-cover"><i class="icon-camera"></i>&nbsp;{{i18n.album.admin.edit.choose_cover.button}}</button>
            <button class="btn btn-danger hide choose-cover-cancel"><i class="icon-remove"></i>&nbsp;{{i18n.album.admin.edit.cancel_cover_choice}}</button>
            <button class="btn regenerate-thumbnails"><i class="icon-repeat"></i>&nbsp;{{i18n.album.admin.edit.regenerate_thumbnails}}</button>
        </div>
    </div>