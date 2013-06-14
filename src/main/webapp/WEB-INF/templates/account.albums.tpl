<div class="page-header"><h1>{{i18n.account.albums.title}}</h1></div>

{{#albums.length}}
<ul class="thumbnails settings">
    {{#albums}}
    <li>{{> account.albums.album}}</li>
    {{/albums}}
</ul>
{{/albums.length}}
{{^albums}}
<p class="alert">{{i18n.common.no_album}}</p>
{{/albums}}