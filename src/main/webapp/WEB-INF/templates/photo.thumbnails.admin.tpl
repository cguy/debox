{{#data.photos.length}}
<h2>Choix d'une photo de couverture</h2>
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
