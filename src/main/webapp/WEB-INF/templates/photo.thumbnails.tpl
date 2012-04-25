{{#data.photos.length}}
<ul class="thumbnails photos">
    {{#data.photos}}
    <li class="span2">
        <a  id="{{id}}" class="thumbnail" href="#/album/{{data.id}}/{{id}}" title="{{name}}" style="background-image:url('{{thumbnailUrl}}')">
            <span class="container"></span>
            <span style="display:none;">{{url}}</span>
        </a>
    </li>
    {{/data.photos}}
</ul>
{{/data.photos.length}}
