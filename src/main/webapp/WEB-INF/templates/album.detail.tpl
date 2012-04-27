{{#data.subAlbums.length}}
<ul class="thumbnails albums">
    {{#data.subAlbums}}
    <li>
        <a class="thumbnail cover" href="#/album/{{id}}" style="background-image:url('{{coverUrl}}')">
            <span class="container">
                <span class="title"><span>{{name}}</span></span>
                <span class="count">
                    {{photosCount}}
                    {{#hasSeveralTotalPhotos}}{{i18n.common.photos}}{{/hasSeveralTotalPhotos}}
                    {{^hasSeveralTotalPhotos}}{{i18n.common.photo}}{{/hasSeveralTotalPhotos}}
                </span>
            </span>
        </a>
    </li>
    {{/data.subAlbums}}
</ul>
{{/data.subAlbums.length}}

