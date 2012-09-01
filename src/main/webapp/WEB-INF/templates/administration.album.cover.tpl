<div id="cover-photos" class="hide">
    <h2>{{i18n.album.admin.edit.choose_cover.title}}</h2>
    {{#subAlbums.length}}
    <h3>{{i18n.album.subalbums}}</h3>
    <ul class="thumbnails albums">
        {{#subAlbums}}
        <li>
            <div data-id="a.{{id}}" class="thumbnail cover" rel="tooltip" title="{{i18n.album.admin.edit.choose_cover.tooltip}}" style="background-image:url('{{coverUrl}}')">
                <span class="container"></span>
            </div>
        </li>
        {{/subAlbums}}
    </ul>
    {{/subAlbums.length}}

    {{#photos.length}}
    {{#subAlbums.length}}<h3>{{i18n.album.admin.edit.choose_cover.photos}}</h3>{{/subAlbums.length}}
    <ul class="thumbnails photos">
        {{#photos}}
        <li class="span2">
            <div data-id="{{id}}" class="thumbnail" rel="tooltip" title="{{i18n.album.admin.edit.choose_cover.tooltip}}" style="background-color:#ddd;background-image:url('{{thumbnailUrl}}')">
                <span class="container"></span>
            </div>
        </li>
        {{/photos}}
    </ul>
    {{/photos.length}}
</div>
