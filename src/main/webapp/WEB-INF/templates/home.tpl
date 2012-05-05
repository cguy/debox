<h1 class="page-header">{{i18n.home.title}}</h1>

{{#albums.length}}
<ul class="thumbnails albums">
    {{#albums}}
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
    {{/albums}}
</ul>
{{/albums.length}}
{{^albums}}
    <p class="alert"><strong>{{i18n.common.warning}}: </strong>{{i18n.common.no_album}}</p>
{{/albums}}
