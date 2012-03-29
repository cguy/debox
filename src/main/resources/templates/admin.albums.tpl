{{#data.albums.length}}
<ul class="thumbnails">
    {{#data.albums}}
    <li>
        <a href="#/administration/album/{{name}}" class="album admin thumbnail" style="background-image:url('{{data.baseUrl}}{{coverUrl}}')">
            <ul class="unstyled">
                <li><strong>{{name}}</strong></li>
                <li>Nombre de photos : {{photosCount}}</li>
                <li>
                    {{#downloadable}}
                        <i class="icon-ok"></i>&nbsp;Téléchargeable
                    {{/downloadable}}
                    {{^downloadable}}
                        <i class="icon-ban-circle"></i>&nbsp;N'est pas téléchargeable
                    {{/downloadable}}
                </li>
                <li>
                    {{#visibility}}
                        <i class="icon-ok"></i>&nbsp;Public
                    {{/visibility}}
                    {{^visibility}}
                        <i class="icon-ban-circle"></i>&nbsp;Privé
                    {{/visibility}}
                </li>
            </ul>
        </a>
    </li>
    {{/data.albums}}
</ul>
{{/data.albums.length}}
