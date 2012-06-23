<h1 id="{{album.id}}" class="page-header">
    <a href="#/album/{{album.id}}">{{album.name}}</a>
    <small>
        {{album.photosCount}}
        {{#album.hasSeveralTotalPhotos}}
            {{i18n.common.photos}}
        {{/album.hasSeveralTotalPhotos}}
        {{^album.hasSeveralTotalPhotos}}
            {{i18n.common.photo}}
        {{/album.hasSeveralTotalPhotos}}
        
        {{#album.isInterval}}
            {{i18n.album.from_date}} {{album.beginDate}} {{i18n.album.to_date}} {{album.endDate}}
        {{/album.isInterval}}
        {{^album.isInterval}}
            {{i18n.album.on_date}} {{album.beginDate}}
        {{/album.isInterval}}
    </small>
</h1>

<div class="btn-toolbar" style="margin-top: 18px;">
    <div class="btn-group">
    {{#albumParent}}
        <a href="#/album/{{albumParent.id}}" class="btn"><i class="icon-list-alt"></i>&nbsp;{{i18n.album.back2album}}: {{albumParent.name}}</a>
    {{/albumParent}}
    {{^albumParent}}
        <a href="#/" class="btn"><i class="icon-list-alt"></i>&nbsp;{{i18n.album.back2albums}}</a>
    {{/albumParent}}
    </div>
    {{#photos.length}}{{#album.downloadable}}
    <div class="btn-group">
        <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon-download"></i>&nbsp;{{i18n.album.download}}&nbsp;<span class="caret"></span></a>
        <ul class="dropdown-menu">
            <li><a target="_blank" href="{{album.minDownloadUrl}}">{{i18n.album.reduced_size}} (1600px)</a></li>
            <li><a target="_blank" href="{{album.downloadUrl}}">{{i18n.album.original_size}}</a></li>
        </ul>
    </div>
    {{/album.downloadable}}{{/photos.length}}
    {{> album.admin.edit}}
</div>

<hr style="clear:both;" />

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
        {{> photo.thumbnails}}
    {{/photos.length}}

    {{^subAlbums}}
    {{^photos}}
    <p class="alert alert-danger">{{i18n.album.no_photos}}</p>
    {{/photos}}
    {{/subAlbums}}
</div>
<div id="cover-photos" class="hide">
    {{> photo.thumbnails.admin}}
</div>

<a id="top"></a>