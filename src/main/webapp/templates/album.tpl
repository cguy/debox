<h1 class="page-header"><a href="#/album/{{data.album.name}}">{{data.album.name}}</a><small>{{data.album.photosCount}}</small></h1>

<div class="btn-toolbar" style="margin-top: 18px;">
    <div class="btn-group">
    {{#data.parent}}
        <a href="#/album/{{data.parent.name}}" class="btn"><i class="icon-list-alt"></i>&nbsp;Retour à l'album : {{data.parent.name}}</a>
    {{/data.parent}}
    {{^data.parent}}
        <a href="#/" class="btn"><i class="icon-list-alt"></i>&nbsp;Retour à la liste des albums</a>
    {{/data.parent}}
    </div>
    {{#data.photos.length}}{{#data.album.downloadable}}
    <div class="btn-group">
        <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon-download"></i>&nbsp;Télécharger les photos de cet album&nbsp;<span class="caret"></span></a>
        <ul class="dropdown-menu">
            <li><a target="_blank" href="download/album/{{data.album.id}}/min">Taille réduite des photos (1600px)</a></li>
            <li><a target="_blank" href="download/album/{{data.album.id}}">Taille originale des photos</a></li>
        </ul>
    </div>
    {{/data.album.downloadable}}{{/data.photos.length}}
</div>

<hr />

{{#data.albums.length}}
    {{#data.photos.length}}<h2>Sous-albums</h2>{{/data.photos.length}}
    <ul class="thumbnails">
    {{#data.albums}}
        <li class="span3">
            <a class="thumbnail cover" href="#/album/{{name}}" style="background-image:url('{{data.baseUrl}}{{coverUrl}}')">
                <span class="container">
                    <span class="title"><span>{{name}}</span></span>
                    <span class="count">{{photosCount}}</span>
                </span>
            </a>
        </li>
    {{/data.albums}}
    </ul>
{{/data.albums.length}}

{{#data.photos.length}}
    {{#data.albums.length}}<h2>{{data.photos.length}} photos</h2>{{/data.albums.length}}
    <ul class="thumbnails photos">
        {{#data.photos}}
        <li class="span2">
            <!--<a class="thumbnail" href="#/photo/{{id}}">-->
            <a  id="{{id}}" class="thumbnail" href="#/album/{{data.album.name}}/{{id}}">
                <img class="photo" src="{{data.baseUrl}}{{thumbnailUrl}}" alt="{{name}}" title="{{name}}" style="background-color:#ddd;width:210px;"/>
                <span style="display:none;">{{data.baseUrl}}{{url}}</span>
            </a>
        </li>
        {{/data.photos}}
    </ul>
{{/data.photos.length}}

{{^data.albums}}
{{^data.photos}}
<p class="alert alert-danger">Il n'y a aucune photo disponible pour cet album.</p>
{{/data.photos}}
{{/data.albums}}