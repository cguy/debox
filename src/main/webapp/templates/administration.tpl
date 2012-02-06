<h1 class="page-header">Administration</h1>

<h2 class="page-header">Configuration générale</h2>
<div id="sync-progress" class="alert alert-info hide">
    <h3 class="alert-heading">Synchronisation en cours&hellip;&nbsp;<span></span></h3>
    <div class="progress progress-info progress-striped active">
        <div class="bar"></div>
    </div>
</div>
<form class="form-vertical" action="#/administration/configuration" method="post">
    <div class="control-group">
        <label for="sourceDirectory">Répertoire source (contenant les photos au format original) :</label>
        <div class="controls">
            <input class="span5" type="text" required id="sourceDirectory" name="sourceDirectory" placeholder="Exemple : /home/user/photos/" />
        </div>
    </div>
    <div class="control-group">
        <label for="targetDirectory">Répertoire de travail (qui contiendra notamment les vignettes des photos) :</label>
        <div class="controls">
            <input class="span5" type="text" required id="targetDirectory" name="targetDirectory" placeholder="Exemple : /home/user/thumbnails/" />
        </div>
    </div>
    <div class="form-actions">
        <input type="submit" class="btn btn-primary" value="Valider" />
        <input type="reset" class="btn" value="Annuler" />
    </div>
</form>

<h2 class="page-header">Liste des albums</h2>
{{#data.albums.length}}
<table class="table table-striped table-bordered table-condensed">
    <thead>
        <tr>
            <th>Nom de l'album</th>
            <th>Répertoire source</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
    {{#data.albums}}
        <tr>
            <td>{{name}}</td>
            <td>{{sourcePath}}</td>
            <td></td>
        </tr>
    {{/data.albums}}
    </tbody>
</table>
{{/data.albums.length}}

{{^data.albums}}
<p class="alert">Aucun album n'a été créé pour le moment !</p>
{{/data.albums}}
