{{#data.albums.length}}
<ul class="thumbnails">
    {{#data.albums}}
    <li class="span3">
        <a class="thumbnail cover" href="#/album/{{name}}" style="background-image:url('{{data.baseUrl}}{{coverUrl}}')">
            <span class="container">
                <span class="title"><span>{{name}}</span></span>
                <span class="count">{{photosCount}}</span>
            </span>
        </a>
    </li>
    {{/data.albums}}
</ul>
{{/data.albums.length}}
