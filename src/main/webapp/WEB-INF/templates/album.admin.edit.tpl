    <button class="btn pull-right edit-album"><i class="icon-pencil"></i>&nbsp;Modifier cet album</button>
    <button class="btn btn-info pull-right edit-album-cancel hide"><i class="icon-remove"></i>&nbsp;Fermer la zone de modification</button>
</div>

<div id="sync-progress" class="alert alert-info hide">
    <h3 class="alert-heading" style="line-height:50px;">
        <span id="progress-label">Synchronisation en cours&hellip;</span>&nbsp;<span id="progress-percentage"></span>
    </h3>
    <div class="progress progress-info progress-striped active" style="margin:0 -20px 10px 0">
        <div class="bar"></div>
    </div>
</div>

<div id="alerts">
    <p class="hide cover alert alert-success">La vignette de l'album a été modifiée avec succès.<span class="close">&times;</span></p>
    <p class="hide cover alert alert-danger">Une erreur est survenue lors de la modification de la vignette de cet album.<span class="close">&times;</span></p>
    <p class="hide edit alert alert-success">L'album a été modifié avec succès.<span class="close">&times;</span></p>
    <p class="hide edit alert alert-danger">Une erreur est survenue lors de la modification de l'album.<span class="close">&times;</span></p>
</div>

<div id="edit_album">
    
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
            <button class="btn regenerate-thumbnails"><i class="icon-repeat"></i>&nbsp;Regénérer les vignettes de cet album</button>
        </div>
    </div>