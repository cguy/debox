<h1 class="page-header"><a href="#/album/{{data.album.name}}">{{data.album.name}}</a></h1>

<div class="btn-toolbar" style="margin-top: 18px;">
    <div class="btn-group">
    {{#data.parent}}
        <a href="#/album/{{data.parent.name}}" class="btn"><i class="icon folder-open"></i>&nbsp;Retour à l'album : {{data.parent.name}}</a>
    {{/data.parent}}
    {{^data.parent}}
        <a href="#/" class="btn"><i class="icon folder-open"></i>&nbsp;Retour à la liste des albums</a>
    {{/data.parent}}
    </div>
    {{#data.photos.length}}
    <div class="btn-group">
        <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon download"></i>&nbsp;Télécharger les photos de cet album&nbsp;<span class="caret"></span></a>
        <ul class="dropdown-menu">
            <li><a target="_blank" href="download/album/{{data.album.id}}/min">Taille réduite des photos (1600px)</a></li>
            <li><a target="_blank" href="download/album/{{data.album.id}}">Taille originale des photos</a></li>
        </ul>
    </div>
    {{/data.photos.length}}
</div>

<hr />

{{#data.albums.length}}
    <h2>Sous-albums</h2>
    <ul class="thumbnails">
    {{#data.albums}}
        <li class="span3">
            <i class="icon folder-open"></i>&nbsp;<a href="#/album/{{name}}">{{name}}</a>
            <a class="thumbnail" href="#/album/{{name}}"><img class="album" src="album/{{id}}/cover" alt="{{name}}" style="background-color:#ddd;width:210px;"/></a>
        </li>
    {{/data.albums}}
    </ul>
{{/data.albums.length}}

{{#data.photos.length}}
    {{#data.albums.length}}<h2>Photos</h2>{{/data.albums.length}}
    <ul class="thumbnails photos">
        {{#data.photos}}
        <li class="span2">
            <!--<a class="thumbnail" href="#/photo/{{id}}">-->
            <a  id="{{id}}" class="thumbnail" href="#/album/{{data.album.name}}/{{id}}">
                <img class="photo" src="thumbnail/{{id}}" alt="{{name}}" title="{{name}}" style="background-color:#ddd;width:210px;"/>
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