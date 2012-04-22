<h1 id="{{data.album.id}}" class="page-header">
    <a href="#/album/{{data.album.name}}">{{data.album.name}}</a>
    <small>
        {{data.album.photosCount}}
        {{#data.album.isInterval}}
            du {{data.album.beginDate}} au {{data.album.endDate}}
        {{/data.album.isInterval}}
        {{^data.album.isInterval}}
            le {{data.album.beginDate}}
        {{/data.album.isInterval}}
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
    {{#data.photos.length}}{{#data.album.downloadable}}
    <div class="btn-group">
        <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon-download"></i>&nbsp;Télécharger les photos de cet album&nbsp;<span class="caret"></span></a>
        <ul class="dropdown-menu">
            <li><a target="_blank" href="{{data.minDownloadUrl}}">Taille réduite des photos (1600px)</a></li>
            <li><a target="_blank" href="{{data.downloadUrl}}">Taille originale des photos</a></li>
        </ul>
    </div>
    {{/data.album.downloadable}}{{/data.photos.length}}
    {{> album.admin.edit}}
</div>

<hr style="clear:both;" />

<div id="photos">
    {{#data.albums.length}}
        {{#data.photos.length}}<h2>Sous-albums</h2>{{/data.photos.length}}
        {{> album.detail}}
    {{/data.albums.length}}

    {{#data.photos.length}}
        {{#data.albums.length}}<h2>{{data.photos.length}} photos</h2>{{/data.albums.length}}
        {{> photo.thumbnails}}
    {{/data.photos.length}}

    {{^data.albums}}
    {{^data.photos}}
    <p class="alert alert-danger">Il n'y a aucune photo disponible pour cet album.</p>
    {{/data.photos}}
    {{/data.albums}}
</div>
<div id="cover-photos" class="hide">
    {{> photo.thumbnails.admin}}
</div>