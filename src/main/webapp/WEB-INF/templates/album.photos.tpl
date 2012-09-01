{{#photos.length}}
<ul class="thumbnails photos {{#inEdition}}hide{{/inEdition}}">
    {{#photos}}
    <li class="span2">
        <a data-id="{{id}}" class="thumbnail" href="#/album/{{albumId}}/{{id}}" fullScreenUrl="{{url}}">
            <span class="picture" style="background-image:url('{{thumbnailUrl}}')"></span>
        </a>
    </li>
    {{/photos}}
</ul>
{{/photos.length}}
