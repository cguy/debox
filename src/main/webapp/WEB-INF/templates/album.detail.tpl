{{#data.albums.length}}
<ul class="thumbnails albums">
    {{#data.albums}}
    <li>
        <a class="thumbnail cover" href="#/album/{{id}}" style="background-image:url('{{coverUrl}}')">
            <span class="container">
                <span class="title"><span>{{name}}</span></span>
                <span class="count">{{photosCount}}</span>
            </span>
        </a>
    </li>
    {{/data.albums}}
</ul>
{{/data.albums.length}}

