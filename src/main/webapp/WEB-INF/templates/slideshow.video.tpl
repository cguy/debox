<video id="{{id}}" class="undisplayed" controls preload="none" poster="{{thumbnailurl}}">
    {{#oggurl}}<source src="{{oggurl}}" type="video/ogg"/>{{/oggurl}}
    {{#webmurl}}<source src="{{webmurl}}" type="video/webm" />{{/webmurl}}
    {{#h264url}}<source src="{{h264url}}" type="video/mp4" />{{/h264url}}
</video>
