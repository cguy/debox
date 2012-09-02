<div class="page-header album">
    <a href="#/{{#albumParent}}album/{{albumParent.id}}{{/albumParent}}" data-placement="right" rel="tooltip" 
       {{#albumParent}}title="{{i18n.album.back2album}}: {{albumParent.name}}"{{/albumParent}}
       {{^albumParent}}title="{{i18n.album.back2albums}}"{{/albumParent}}
       class="back"><i class="icon-circle-arrow-left"></i></a>

    {{#config.isAdmin}}
    <a href="#/album/{{album.id}}/edition" data-placement="left" rel="tooltip" title="{{i18n.album.admin.edit.modify_this}}" class="pull-right edit-album {{#inEdition}}hide{{/inEdition}}"><i class="icon-cog"></i></a>
    <a href="#/album/{{album.id}}" data-placement="left" rel="tooltip" title="{{i18n.album.admin.edit.close_notif_zone}}" class="pull-right edit-album-cancel {{^inEdition}}hide{{/inEdition}}"><i class="icon-remove"></i></a>
    {{/config.isAdmin}}

    {{#photos.length}}{{#album.downloadable}}
    <div class="dropdown pull-right">
        <a href="#" data-placement="left" data-toggle="dropdown" rel="tooltip" title="{{i18n.album.download}}" class="dropdown-toggle"><i class="icon-download-alt"></i></a>
        <ul class="dropdown-menu">
            <li><a target="_blank" href="{{album.minDownloadUrl}}">{{i18n.album.reduced_size}} (1600px)</a></li>
            <li><a target="_blank" href="{{album.downloadUrl}}">{{i18n.album.original_size}}</a></li>
        </ul>
    </div>
    {{/album.downloadable}}{{/photos.length}}
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
        {{/album.photosCount}}
        {{^album.photosCount}}
            {{i18n.common.noPhotos}}
        {{/album.photosCount}}
    </div>
</div>
{{#album.description}}
<div class="album_description">
    {{album.description}}
</div>
{{/album.description}}

{{> administration.album.edit}}

<div id="photos">
    {{#subAlbums.length}}
    {{#photos.length}}<h2>{{i18n.album.subalbums}}</h2>{{/photos.length}}
    <ul class="thumbnails albums">
        {{#subAlbums}}
        <li>
            <a class="thumbnail cover" href="#/album/{{id}}">
                <span class="picture" style="background-image:url('{{coverUrl}}')">
                    <span class="title"><span>{{name}}</span></span>
                    <span class="count">
                        {{photosCount}}
                        {{#hasSeveralTotalPhotos}}{{i18n.common.photos}}{{/hasSeveralTotalPhotos}}
                        {{^hasSeveralTotalPhotos}}{{i18n.common.photo}}{{/hasSeveralTotalPhotos}}
                    </span>
                </span>
            </a>
        </li>
        {{/subAlbums}}
    </ul>
    {{/subAlbums.length}}
    {{#photos.length}}
    {{#subAlbums.length}}
    <h2>{{photos.length}} 
        {{#album.hasSeveralPhotos}}{{i18n.common.photos}}{{/album.hasSeveralPhotos}}
        {{^album.hasSeveralPhotos}}{{i18n.common.photo}}{{/album.hasSeveralPhotos}}
    </h2>{{/subAlbums.length}}
    {{> album.photos}}
    {{/photos.length}}

    {{^subAlbums}}
    {{^photos}}
    <p class="alert alert-danger">{{i18n.album.no_photos}}</p>
    {{/photos}}
    {{/subAlbums}}
</div>

{{> administration.album.cover}}
{{> administration.album.photos}}

<a id="top"></a>