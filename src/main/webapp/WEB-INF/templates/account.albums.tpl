<h2 class="subtitle">{{i18n.account.albums.title}}</h2>

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