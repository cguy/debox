{{#photos.length}}
<ul class="thumbnails photos">
    {{#photos}}
    <li class="span2">
        <a  id="{{id}}" class="thumbnail" href="#/album/{{albumId}}/{{id}}" title="{{name}}">
            <span class="picture" style="background-image:url('{{thumbnailUrl}}')"></span>
            <span style="display:none;">{{url}}</span>
        </a>
    </li>
    {{/photos}}
</ul>
{{/photos.length}}
