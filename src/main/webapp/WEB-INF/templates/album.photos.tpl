{{#photos.length}}
<ul class="thumbnails photos">
    {{#photos}}
    <li class="span2">
        <a id="{{id}}" class="thumbnail" href="#/album/{{albumId}}/{{id}}" title="{{name}}" fullScreenUrl="{{url}}">
            <span class="picture" style="background-image:url('{{thumbnailUrl}}')"></span>
        </a>
    </li>
    {{/photos}}
</ul>
{{/photos.length}}
