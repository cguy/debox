{{#medias.length}}
<ul class="thumbnails photos {{#inEdition}}hide{{/inEdition}}">
    {{#medias}}
    <li class="span2">
        <a class="thumbnail"
           href="#/albums/{{albumId}}/{{id}}"
           data-id="{{id}}"
           data-date="{{date}}"
           data-title="{{title}}"
           data-thumbnailUrl="{{thumbnailUrl}}"

        {{#photo}}
           data-photo="true"
           data-url="{{url}}">
            <span class="picture" style="background-image:url('{{thumbnailUrl}}')"></span>
            <span class="filter"><i class="icon-plus-sign"></i></span>
        {{/photo}}

        {{#video}}
           data-video="true"
           data-oggUrl="{{oggUrl}}"
           data-h264Url="{{h264Url}}"
           data-webmUrl="{{webmUrl}}"
           >
            <span class="picture video" style="background-image:url('{{squareThumbnailUrl}}')">
                <i class="icon-film"></i>
            </span>
            <span class="filter"><i class="icon-play-circle"></i></span>
        {{/video}}
        
        </a>
    </li>
    {{/medias}}
</ul>
{{/medias.length}}
