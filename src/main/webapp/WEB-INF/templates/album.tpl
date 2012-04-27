<h1 id="{{data.id}}" class="page-header">
    <a href="#/album/{{data.id}}">{{data.name}}</a>
    <small>
        {{data.photosCount}}
        {{#data.hasSeveralTotalPhotos}}
            {{i18n.common.photos}}
        {{/data.hasSeveralTotalPhotos}}
        {{^data.hasSeveralTotalPhotos}}
            {{i18n.common.photo}}
        {{/data.hasSeveralTotalPhotos}}
        
        {{#data.isInterval}}
            {{i18n.album.from_date}} {{data.beginDate}} {{i18n.album.to_date}} {{data.endDate}}
        {{/data.isInterval}}
        {{^data.isInterval}}
            {{i18n.album.on_date}} {{data.beginDate}}
        {{/data.isInterval}}
    </small>
</h1>

<div class="btn-toolbar" style="margin-top: 18px;">
    <div class="btn-group">
    {{#data.parent}}
        <a href="#/album/{{data.parent.id}}" class="btn"><i class="icon-list-alt"></i>&nbsp;{{i18n.album.back2album}}: {{data.parent.name}}</a>
    {{/data.parent}}
    {{^data.parent}}
        <a href="#/" class="btn"><i class="icon-list-alt"></i>&nbsp;{{i18n.album.back2albums}}</a>
    {{/data.parent}}
    </div>
    {{#data.photos.length}}{{#data.downloadable}}
    <div class="btn-group">
        <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon-download"></i>&nbsp;{{i18n.album.download}}&nbsp;<span class="caret"></span></a>
        <ul class="dropdown-menu">
            <li><a target="_blank" href="{{data.minDownloadUrl}}">{{i18n.album.reduced_size}} (1600px)</a></li>
            <li><a target="_blank" href="{{data.downloadUrl}}">{{i18n.album.original_size}}</a></li>
        </ul>
    </div>
    {{/data.downloadable}}{{/data.photos.length}}
    {{> album.admin.edit}}
</div>

<hr style="clear:both;" />

<div id="photos">
    {{#data.subAlbums.length}}
        {{#data.photos.length}}<h2>{{i18n.album.subalbums}}</h2>{{/data.photos.length}}
        {{> album.detail}}
    {{/data.subAlbums.length}}

    {{#data.photos.length}}
        {{#data.subAlbums.length}}
            <h2>{{data.photos.length}} 
                {{#data.hasSeveralPhotos}}{{i18n.common.photos}}{{/data.hasSeveralPhotos}}
                {{^data.hasSeveralPhotos}}{{i18n.common.photo}}{{/data.hasSeveralPhotos}}
            </h2>{{/data.subAlbums.length}}
        {{> photo.thumbnails}}
    {{/data.photos.length}}

    {{^data.subAlbums}}
    {{^data.photos}}
    <p class="alert alert-danger">{{i18n.album.no_photos}}</p>
    {{/data.photos}}
    {{/data.subAlbums}}
</div>
<div id="cover-photos" class="hide">
    {{> photo.thumbnails.admin}}
</div>

<a id="top"></a>