{{#data.photos.length}}
<ul class="thumbnails photos">
    {{#data.photos}}
    <li class="span2">
        <a  id="{{id}}" class="thumbnail" href="#/album/{{data.id}}/{{id}}" title="{{name}}">
            <span class="picture" style="background-image:url('{{thumbnailUrl}}')"></span>
            <span style="display:none;">{{url}}</span>
        </a>
    </li>
    {{/data.photos}}
</ul>
{{/data.photos.length}}
