<h1 class="page-header">{{i18n.home.title}}</h1>

{{#data.albums.length}}
<ul class="thumbnails albums">
    {{#data.albums}}
    <li>
        <a class="thumbnail cover" href="#/album/{{id}}" style="background-image:url('{{coverUrl}}')">
            <span class="container">
                <span class="title"><span>{{name}}</span></span>
                <span class="count">
                    {{photosCount}}
                    {{#hasSeveralPhotos}}{{i18n.common.photos}}{{/hasSeveralPhotos}}
                    {{^hasSeveralPhotos}}{{i18n.common.photo}}{{/hasSeveralPhotos}}
                </span>
            </span>
        </a>
    </li>
    {{/data.albums}}
</ul>
{{/data.albums.length}}
{{^data.albums}}
    <p class="alert"><strong>{{i18n.common.warning}} : </strong>{{i18n.home.no_album}}</p>
{{/data.albums}}
