<h2>{{i18n.administration.albums.title}}</h2>

{{#albums.length}}
<ul class="thumbnails">
    {{#albums}}
    <li>
        <a href="#/album/{{id}}/edition" class="album admin thumbnail" style="background-image:url('{{coverUrl}}')">
            <ul class="unstyled">
                <li title="{{name}}"><strong>{{name}}</strong></li>
                <li>{{i18n.common.date}}: {{beginDate}}</li>
                <li>{{i18n.administration.albums.photo_number}}: {{photosCount}}</li>
                {{#downloadable}}
                <li style="color:#2E9E32;">
                    <i class="icon-download-alt"></i>&nbsp;{{i18n.administration.albums.downloadable}}
                </li>
                {{/downloadable}}
                {{^downloadable}}
                <li style="color:#C42323;">
                    <i class="icon-download-alt"></i>&nbsp;{{i18n.administration.albums.not_downloadable}}
                </li>
                {{/downloadable}}
                {{#public}}
                <li style="color:#2E9E32;">
                    <i class="icon-ok" style="color:#2E9E32;"></i>&nbsp;{{i18n.common.public}}
                </li>
                {{/public}}
                {{^public}}
                <li style="color:#C42323;">
                    <i class="icon-ban-circle" style="color:#C42323;"></i>&nbsp;{{i18n.common.private}}
                </li>
                {{/public}}
            </ul>
        </a>
    </li>
    {{/albums}}
</ul>
{{/albums.length}}
{{^albums}}
<p class="alert">{{i18n.common.no_album}}</p>
{{/albums}}