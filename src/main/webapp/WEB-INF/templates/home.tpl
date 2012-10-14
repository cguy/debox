<div class="page-header"><h1>{{i18n.home.title}}</h1></div>

{{#albums.length}}
<ul class="thumbnails albums">
    {{#albums}}
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
                <span class="count">
                    <i class="icon-picture"></i> {{photosCount}}
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
