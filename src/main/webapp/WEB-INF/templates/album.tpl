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
            <li><a target="_blank" href="{{data.minDownloadUrl}}">Taille réduite des photos (1600px)</a></li>
            <li><a target="_blank" href="{{data.downloadUrl}}">Taille originale des photos</a></li>
        </ul>
    </div>
    {{/data.album.downloadable}}{{/data.photos.length}}
    <button class="btn pull-right edit-album"><i class="icon-pencil"></i>&nbsp;Modifier cet album</button>
    <button class="btn btn-info pull-right edit-album-cancel hide"><i class="icon-remove"></i>&nbsp;Fermer la zone de modification</button>
</div>

<div id="edit_album">
    <p class="hide cover alert alert-success">La vignette de l'album a été modifiée avec succès.<a class="close" data-dismiss="alert" href="#">&times;</a></p>
    <p class="hide cover alert alert-danger">Une erreur est survenue lors de la modification de la vignette de cet album.<a class="close" data-dismiss="alert" href="#">&times;</a></p>
    <p class="hide edit alert alert-success">L'album a été modifié avec succès.<a class="close" data-dismiss="alert" href="#">&times;</a></p>
    <p class="hide edit alert alert-danger">Une erreur est survenue lors de la modification de l'album.<a class="close" data-dismiss="alert" href="#">&times;</a></p>
    <div class="row">
        <div class="span6">
            <form class="form-horizontal" action="#/album" method="post">
                <h2 class="page-header">Modifier l'album {{data.album.name}}</h2>
                <p></p>
                <div class="control-group">
                    <label class="control-label" for="name">Nom de l'album</label>
                    <div class="controls">
                        <input type="text" required class="input-large" id="name" name="name" value="{{data.album.name}}" />
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="visibility">Visibilité de l'album</label>
                    <div class="controls">
                        <select name="visibility" id="visibility">
                            <option value="public" {{#data.album.visibility}}selected{{/data.album.visibility}}>Public</option>
                            <option value="private" {{^data.album.visibility}}selected{{/data.album.visibility}}>Privé</option>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="downloadable">Téléchargement</label>
                    <div class="controls">
                        <label class="checkbox">
                            <input id="downloadable" type="checkbox" name="downloadable" {{#data.album.downloadable}}checked{{/data.album.downloadable}}> Les photos de cet album sont téléchargeables par les personnes ayant accès à cet album.
                        </label>
                    </div>
                </div>
                <div class="form-actions">
                    <input type="hidden" name="id" value="{{data.album.id}}" />
                    <input type="submit" class="btn btn-primary" data-loading-text="Modification en cours ..." value="Modifier" />
                </div>
            </form>
        </div>
        <div class="span4">
            <h2 class="page-header">Actions</h2>
            <button class="btn choose-cover"><i class="icon-camera"></i>&nbsp;Choisir une nouvelle vignette d'album</button>
            <button class="btn btn-danger hide choose-cover-cancel"><i class="icon-remove"></i>&nbsp;Annuler le choix d'une vignette</button>
        </div>
    </div>
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