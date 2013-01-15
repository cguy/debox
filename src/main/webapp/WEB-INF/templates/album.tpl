<div class="page-header album">
    <a href="#/{{#albumParent}}album/{{albumParent.id}}{{/albumParent}}" data-placement="right" rel="tooltip" 
       {{#albumParent}}title="{{i18n.album.back2album}}: {{albumParent.name}}"{{/albumParent}}
       {{^albumParent}}title="{{i18n.album.back2albums}}"{{/albumParent}}
       class="back"><i class="icon-circle-arrow-left"></i></a>

    {{#config.administrator}}
    <a href="#/album/{{album.id}}/edition" data-placement="left" rel="tooltip" title="{{i18n.album.admin.edit.modify_this}}" class="pull-right edit-album {{#inEdition}}hide{{/inEdition}}"><i class="icon-cog"></i></a>
    <a href="#/album/{{album.id}}" data-placement="left" rel="tooltip" title="{{i18n.album.admin.edit.close_notif_zone}}" class="pull-right edit-album-cancel {{^inEdition}}hide{{/inEdition}}"><i class="icon-remove"></i></a>
    {{/config.administrator}}

    {{#config.authenticated}}
    <a href="#/album/{{album.id}}" data-placement="left" rel="tooltip" class="pull-right comments">
        <span class="badge badge-info {{^comments.length}}hide{{/comments.length}}">{{comments.length}}</span>
        <i class="icon-comment"></i>
    </a>
    {{/config.authenticated}}

    {{#medias.length}}{{#album.downloadable}}
    <div class="dropdown pull-right">
        <a href="#" data-placement="bottom" data-toggle="dropdown" rel="tooltip" title="{{i18n.album.download}}" class="dropdown-toggle"><i class="icon-download-alt"></i></a>
        <ul class="dropdown-menu">
            <li><a target="_blank" href="{{album.minDownloadUrl}}">{{i18n.album.reduced_size}} (1600px)</a></li>
            <li><a target="_blank" href="{{album.downloadUrl}}">{{i18n.album.original_size}}</a></li>
        </ul>
    </div>
    {{/album.downloadable}}{{/medias.length}}
    <h1 id="{{album.id}}">
        {{album.name}}
    </h1>
    <div class="information">
        {{#album.photosCount}}
            {{album.photosCount}}
            {{#album.hasSeveralTotalPhotos}}
                {{i18n.common.photos}}
            {{/album.hasSeveralTotalPhotos}}
            {{^album.hasSeveralTotalPhotos}}
                {{i18n.common.photo}}
            {{/album.hasSeveralTotalPhotos}}
            {{#album.videosCount}}
                {{i18n.common.and}}
            {{/album.videosCount}}
        {{/album.photosCount}}
        {{#album.videosCount}}
            {{album.videosCount}}
            {{#album.hasSeveralTotalVideos}}
                {{i18n.common.videos}}
            {{/album.hasSeveralTotalVideos}}
            {{^album.hasSeveralTotalVideos}}
                {{i18n.common.video}}
            {{/album.hasSeveralTotalVideos}}
        {{/album.videosCount}}
        {{#album.hasMedias}}
            {{#album.beginDate}}
                {{#album.isInterval}}
                    {{#album.endDate}}
                        {{i18n.album.from_date}} {{album.beginDate}} {{i18n.album.to_date}} {{album.endDate}}
                    {{/album.endDate}}
                {{/album.isInterval}}
                {{^album.isInterval}}
                    {{i18n.album.on_date}} {{album.beginDate}}
                {{/album.isInterval}}
            {{/album.beginDate}}
        {{/album.hasMedias}}
        {{^album.photosCount}}{{^album.videosCount}}
            {{i18n.common.noPhotos}}
        {{/album.videosCount}}{{/album.photosCount}}
    </div>
</div>

<div id="album-content">
    {{#config.authenticated}}
    <div id="album-comments">
        <div class="alert alert-heading no-comments {{#comments.length}}hide{{/comments.length}}">{{i18n.comments.empty.album}}</div>
        {{#comments.length}}
            {{#comments}}
                {{> comment}}
            {{/comments}}
        {{/comments.length}}
        
        <form id="new-album-comment" method="post" action="#/album/{{album.id}}/comments">
            <textarea name="content" required placeholder="{{i18n.comments.placeholder}}"></textarea>
            <div class="form-actions">
                <input type="submit" class="btn btn-primary btn-small" value="{{i18n.common.validate}}" />
            </div>
        </form>
    </div>
    {{/config.authenticated}}

    {{#album.description}}
    <div class="album_description">
        {{album.description}}
    </div>
    {{/album.description}}

    {{> administration.album.edit}}

    <div id="photos">
        {{#subAlbums.length}}
        {{#medias.length}}<h2>{{i18n.album.subalbums}}</h2>{{/medias.length}}
        <ul class="thumbnails albums">
            {{#subAlbums}}
            <li>
                <a class="thumbnail" href="#/album/{{id}}">
                    <span class="picture" style="background-image:url('{{coverUrl}}')"></span>
                    <span class="title" title="{{name}}"><span>{{name}}</span></span>
                    <span class="filter">
                        <i class="icon-plus-sign"></i>
                        <span class="date">
                            <i class="icon-calendar"></i>
                            {{beginDate}}
                        </span>
                        {{#videosCount}}
                        <span class="videos count">
                            {{videosCount}}
                            {{#hasSeveralTotalVideos}}{{i18n.common.videos}}{{/hasSeveralTotalVideos}}
                            {{^hasSeveralTotalVideos}}{{i18n.common.video}}{{/hasSeveralTotalVideos}}
                            <i class="icon-film"></i>
                        </span>
                        {{/videosCount}}
                        <span class="photos count">
                            {{photosCount}}
                            {{#hasSeveralTotalPhotos}}{{i18n.common.photos}}{{/hasSeveralTotalPhotos}}
                            {{^hasSeveralTotalPhotos}}{{i18n.common.photo}}{{/hasSeveralTotalPhotos}}
                            <i class="icon-picture"></i>
                        </span>
                    </span>
                </a>
            </li>
            {{/subAlbums}}
        </ul>
        {{/subAlbums.length}}
        {{#medias.length}}
        {{#subAlbums.length}}
        <h2>{{medias.length}} 
            {{#album.hasSeveralPhotos}}{{i18n.common.photos}}{{/album.hasSeveralPhotos}}
            {{^album.hasSeveralPhotos}}{{i18n.common.photo}}{{/album.hasSeveralPhotos}}
        </h2>{{/subAlbums.length}}
        {{> album.medias}}
        {{/medias.length}}

        {{^subAlbums}}
        {{^medias}}
        <p class="alert alert-danger">{{i18n.album.no_photos}}</p>
        {{/medias}}
        {{/subAlbums}}
    </div>

    {{> administration.album.cover}}
    {{> administration.album.medias}}

</div>
<a id="top"></a>

<form id="remove-comment" class="modal hide fade" action="#/albums/{{album.id}}/comments/" data-action="#/albums/{{album.id}}/comments/" method="delete">
    <div class="modal-header">
        <h3>{{i18n.common.deletion}}</h3>
    </div>
    <div class="modal-body">
        {{i18n.comments.confirm}}
    </div>
    <div class="modal-footer">
        <input type="submit" class="btn btn-danger" data-loading-text="{{i18n.common.deletion_in_progress}}" value="{{i18n.common.delete}}" />
        <button type="reset" class="btn" data-dismiss="modal">{{i18n.common.cancel}}</button>
    </div>
</form>
