<div class="page-header album">
    <a href="#/{{#albumParent}}album/{{albumParent.id}}{{/albumParent}}" data-placement="right" rel="tooltip" 
       {{#albumParent}}title="{{i18n.album.back2album}}: {{albumParent.name}}"{{/albumParent}}
       {{^albumParent}}title="{{i18n.album.back2albums}}"{{/albumParent}}
       class="back"><i class="icon-circle-arrow-left"></i></a>

    {{#config.isAdmin}}
    <a href="#/album/{{album.id}}/edition" data-placement="left" rel="tooltip" title="{{i18n.album.admin.edit.modify_this}}" class="pull-right edit-album {{#inEdition}}hide{{/inEdition}}"><i class="icon-cog"></i></a>
    <a href="#/album/{{album.id}}" data-placement="left" rel="tooltip" title="{{i18n.album.admin.edit.close_notif_zone}}" class="pull-right edit-album-cancel {{^inEdition}}hide{{/inEdition}}"><i class="icon-remove"></i></a>
    {{/config.isAdmin}}

    <a href="#/album/{{album.id}}" data-placement="left" rel="tooltip" class="pull-right comments">
        {{#album.comments.length}}
            <span class="badge badge-info">{{album.comments.length}}</span>
        {{/album.comments.length}}
        <i class="icon-comment"></i>
    </a>

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

<div id="album-content">
    <div id="album-comments">
        {{^comments.length}}
            <div class="alert alert-heading no-comments">{{i18n.album.comments.empty}}</div>
        {{/comments.length}}
        {{#comments.length}}
            {{#comments}}
                {{> comment}}
            {{/comments}}
        {{/comments.length}}
        
        <form id="new-album-comment" method="post" action="#/album/{{album.id}}/comments">
            <textarea name="content" required placeholder="{{i18n.album.comments.placeholder}}"></textarea>
            <div class="form-actions">
                <input type="submit" class="btn btn-primary btn-small" value="{{i18n.common.validate}}" />
            </div>
        </form>
    </div>

    <div class="album_description">
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec eros risus, lacinia ornare volutpat quis, lobortis luctus velit. Nam ornare tincidunt turpis quis tincidunt. Morbi hendrerit rutrum convallis. Quisque porta, lorem nec pellentesque porttitor, massa erat lacinia arcu, quis congue velit arcu bibendum mi. Maecenas in fermentum magna. Donec tempor iaculis nunc, ut blandit nulla pretium eu. Ut hendrerit sem eget arcu vehicula eleifend. Etiam tortor dui, volutpat a congue et, varius posuere enim. Suspendisse eu quam dui. Nam a justo sed dolor interdum dapibus sit amet mattis metus. Quisque faucibus, nunc sed congue posuere, dolor diam iaculis diam, et facilisis sapien ipsum ornare ligula. Nam porttitor scelerisque enim, at rutrum neque iaculis at.
    </div>
    {{#album.description}}
    <div class="album_description">
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec eros risus, lacinia ornare volutpat quis, lobortis luctus velit. Nam ornare tincidunt turpis quis tincidunt. Morbi hendrerit rutrum convallis. Quisque porta, lorem nec pellentesque porttitor, massa erat lacinia arcu, quis congue velit arcu bibendum mi. Maecenas in fermentum magna. Donec tempor iaculis nunc, ut blandit nulla pretium eu. Ut hendrerit sem eget arcu vehicula eleifend. Etiam tortor dui, volutpat a congue et, varius posuere enim. Suspendisse eu quam dui. Nam a justo sed dolor interdum dapibus sit amet mattis metus. Quisque faucibus, nunc sed congue posuere, dolor diam iaculis diam, et facilisis sapien ipsum ornare ligula. Nam porttitor scelerisque enim, at rutrum neque iaculis at.
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

</div>
<a id="top"></a>