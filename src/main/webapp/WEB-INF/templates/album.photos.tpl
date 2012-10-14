{{#photos.length}}
<ul class="thumbnails photos {{#inEdition}}hide{{/inEdition}}">
    {{#photos}}
    <li class="span2">
        <a class="thumbnail"
           href="#/album/{{albumId}}/{{id}}"
           data-id="{{id}}"
           data-date="{{date}}"
           data-title="{{title}}"
           data-url="{{url}}"
           data-thumbnail="{{thumbnailUrl}}">
            <span class="picture" style="background-image:url('{{thumbnailUrl}}')"></span>
            <span class="filter"><i class="icon-plus-sign"></i></span>
        </a>
    </li>
    {{/photos}}
</ul>
{{/photos.length}}
