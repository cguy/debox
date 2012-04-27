<h2>{{i18n.album.admin.edit.choose_cover.title}}</h2>
{{#data.subAlbums.length}}
    <h3>{{i18n.album.subalbums}}</h3>
    <ul class="thumbnails albums">
        {{#data.subAlbums}}
        <li>
            <div id="a.{{id}}" class="thumbnail cover" rel="tooltip" title="{{i18n.album.admin.edit.choose_cover.tooltip}}" style="background-image:url('{{coverUrl}}')">
                <span class="container"></span>
            </div>
        </li>
        {{/data.subAlbums}}
    </ul>
{{/data.subAlbums.length}}

{{#data.photos.length}}
{{#data.subAlbums.length}}<h3>{{i18n.album.admin.edit.choose_cover.photos}}</h3>{{/data.subAlbums.length}}
<ul class="thumbnails photos">
    {{#data.photos}}
    <li class="span2">
        <div id="{{id}}" class="thumbnail" rel="tooltip" title="{{i18n.album.admin.edit.choose_cover.tooltip}}" style="background-color:#ddd;background-image:url('{{thumbnailUrl}}')">
            <span class="container"></span>
        </div>
    </li>
    {{/data.photos}}
</ul>
{{/data.photos.length}}
