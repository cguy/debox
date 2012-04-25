<h1 id="{{data.id}}" class="page-header">
    <a href="#/album/{{data.name}}">{{data.name}}</a>
    <small>
        {{data.photosCount}}
        {{#data.isInterval}}
            du {{data.beginDate}} au {{data.endDate}}
        {{/data.isInterval}}
        {{^data.isInterval}}
            le {{data.beginDate}}
        {{/data.isInterval}}
    </small>
</h1>

<div class="btn-toolbar" style="margin-top: 18px;">
    <div class="btn-group">
    {{#data.parent}}
        <a href="#/album/{{data.parent.id}}" class="btn"><i class="icon-list-alt"></i>&nbsp;Retour à l'album : {{data.parent.name}}</a>
    {{/data.parent}}
    {{^data.parent}}
        <a href="#/" class="btn"><i class="icon-list-alt"></i>&nbsp;Retour à la liste des albums</a>
    {{/data.parent}}
    </div>
    {{#data.photos.length}}{{#data.downloadable}}
    <div class="btn-group">
        <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon-download"></i>&nbsp;Télécharger les photos de cet album&nbsp;<span class="caret"></span></a>
        <ul class="dropdown-menu">
            <li><a target="_blank" href="{{data.minDownloadUrl}}">Taille réduite des photos (1600px)</a></li>
            <li><a target="_blank" href="{{data.downloadUrl}}">Taille originale des photos</a></li>
        </ul>
    </div>
    {{/data.downloadable}}{{/data.photos.length}}
    {{> album.admin.edit}}
</div>

<hr style="clear:both;" />

<div id="photos">
    {{#data.subAlbums.length}}
        {{#data.photos.length}}<h2>Sous-albums</h2>{{/data.photos.length}}
        {{> album.detail}}
    {{/data.subAlbums.length}}

    {{#data.photos.length}}
        {{#data.subAlbums.length}}<h2>{{data.photos.length}} photos</h2>{{/data.subAlbums.length}}
        {{> photo.thumbnails}}
    {{/data.photos.length}}

    {{^data.subAlbums}}
    {{^data.photos}}
    <p class="alert alert-danger">Il n'y a aucune photo disponible pour cet album.</p>
    {{/data.photos}}
    {{/data.subAlbums}}
</div>
<div id="cover-photos" class="hide">
    {{> photo.thumbnails.admin}}
</div>

<a id="top"></a>