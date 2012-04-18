<h2 class="page-header">Liste des albums</h2>

{{#data.albums.length}}
<ul class="thumbnails">
    {{#data.albums}}
    <li>
        <a href="#/album/{{id}}" class="album admin thumbnail" style="background-image:url('{{coverUrl}}')">
            <ul class="unstyled" style="font-size:16px;">
                <li><strong>{{name}}</strong></li>
                <li>Nombre de photos : {{photosCount}}</li>
                {{#downloadable}}
                <li style="color:#2E9E32;">
                    <i class="icon-download-alt"></i>&nbsp;Téléchargeable
                </li>
                {{/downloadable}}
                {{^downloadable}}
                <li style="color:#C42323;">
                    <i class="icon-download-alt"></i>&nbsp;N'est pas téléchargeable
                </li>
                {{/downloadable}}
                {{#visibility}}
                <li style="color:#2E9E32;">
                    <i class="icon-ok" style="color:#2E9E32;"></i>&nbsp;Public
                </li>
                {{/visibility}}
                {{^visibility}}
                <li style="color:#C42323;">
                    <i class="icon-ban-circle" style="color:#C42323;"></i>&nbsp;Privé
                </li>
                {{/visibility}}
            </ul>
        </a>
    </li>
    {{/data.albums}}
</ul>
{{/data.albums.length}}
{{^data.albums}}
<p class="alert">Aucun album n'a été créé pour le moment !</p>
{{/data.albums}}