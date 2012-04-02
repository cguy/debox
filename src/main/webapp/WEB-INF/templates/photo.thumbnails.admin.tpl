<h2>Choix d'une photo de couverture</h2>
{{#data.albums.length}}
    <h3>Sous-albums</h3>
    <ul class="thumbnails">
        {{#data.albums}}
        <li>
            <div id="a.{{id}}" class="thumbnail cover" rel="tooltip" title="Cliquez sur la photo pour qu'elle devienne la couverture de cette album" style="background-image:url('{{coverUrl}}')">
                <span class="container"></span>
            </div>
        </li>
        {{/data.albums}}
    </ul>
{{/data.albums.length}}

{{#data.photos.length}}
{{#data.albums.length}}<h3>Photos</h3>{{/data.albums.length}}
<ul class="thumbnails photos">
    {{#data.photos}}
    <li class="span2">
        <div id="{{id}}" class="thumbnail" rel="tooltip" title="Cliquez sur la photo pour qu'elle devienne la couverture de cette album" style="background-color:#ddd;background-image:url('{{thumbnailUrl}}')">
            <span class="container"></span>
        </div>
    </li>
    {{/data.photos}}
</ul>
{{/data.photos.length}}
