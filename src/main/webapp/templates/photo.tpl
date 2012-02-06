<h1 class="page-header">{{data.photo.name}}<small>{{data.album.name}}</small></h1>

<div class="btn-toolbar" style="margin-top: 18px;">
    <div class="btn-group">
        <a href="#/album/{{data.album.name}}" class="btn"><i class="icon folder-open"></i>&nbsp;Retour à l'album</a>
    </div>
    <div class="btn-group">
        <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon download"></i>&nbsp;Télécharger la photo&nbsp;<span class="caret"></span></a>
        <ul class="dropdown-menu">
            <li><a target="_blank" href="download/photo/{{data.photo.id}}/min">Taille réduite (1600px)</a></li>
            <li><a target="_blank" href="download/photo/{{data.photo.id}}">Taille originale</a></li>
        </ul>
    </div>
</div>

<hr />

<div class="media-grid" style="text-align: center;">
    <a class="thumbnail" href="photo/{{data.photo.id}}" style="display:inline-block; float:none;">
        <img src="photo/{{data.photo.id}}" style="max-height:700px;max-width:100%;" />
    </a>
</div>

