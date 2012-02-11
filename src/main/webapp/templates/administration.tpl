<h1 class="page-header">Administration</h1>

<div id="sync-progress" class="alert alert-info hide">
    <h3 class="alert-heading">Synchronisation en cours&hellip;&nbsp;<span></span></h3>
    <div class="progress progress-info progress-striped active">
        <div class="bar"></div>
    </div>
</div>

<ul class="thumbnails admin">
    <li class="span6">
        <a href="#modal-configuration" data-toggle="modal" class="thumbnail">
            <img src="{{data.baseUrl}}img/folder-settings.png" alt="" title="" />
            Configuration générale
        </a>
    </li>
    <li class="span6">
        <a href="#" class="thumbnail">
            <img src="{{data.baseUrl}}img/folder-images.png" alt="" title="" />
            Gestion des albums photos
        </a>
    </li>
    <li class="span6">
        <a href="#" class="thumbnail">
            <img src="{{data.baseUrl}}img/folder-web.png" alt="" title="" />
            Gestion des accès visiteurs
        </a>
    </li>
    <li class="span6">
        <a data-toggle="modal" href="#modal-account" class="thumbnail">
            <img src="{{data.baseUrl}}img/folder-user.png" alt="" title="" />
            Modifier mes identifiants de connexion
        </a>
    </li>    
</ul>

<h2 class="page-header">Liste des albums</h2>
{{#data.albums.length}}
<table id="administration_albums" class="table table-striped table-bordered table-condensed">
    <thead>
        <tr>
            <th>Nom de l'album</th>
            <th>Répertoire source</th>
            <th style="width:65px;text-align:center;">Visibilité</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody>
    {{#data.albums}}
        <tr id="{{id}}">
            <td>{{name}}</td>
            <td>{{sourcePath}}</td>
            <td>
                {{#visibility}}
                    <i class="icon-ok"></i>&nbsp;Public
                {{/visibility}}
                {{^visibility}}
                    <i class="icon-ban-circle"></i>&nbsp;Privé
                {{/visibility}}
            </td>
            <td>
                <button class="btn">Modifier</button>
            </td>
        </tr>
    {{/data.albums}}
    </tbody>
</table>
{{/data.albums.length}}

{{^data.albums}}
<p class="alert">Aucun album n'a été créé pour le moment !</p>
{{/data.albums}}

<h2 class="page-header">Gestion des groupes de visiteurs</h2>
{{#data.tokens.length}}
<h3>Liste des groupes</h3>
<table id="administration_tokens" class="table table-striped table-bordered table-condensed">
    <thead>
        <tr>
            <th>Libellé</th>
            <th>Albums</th>
            <th>Lien</th>
            <th style="width:155px;">Actions</th>
        </tr>
    </thead>
    <tbody>
    {{#data.tokens}}
        <tr id="{{id}}">
            <td>{{label}}</td>
            <td>
                {{^albums}}
                    Ce groupe n'a accès à aucun album
                {{/albums}}
                {{#albums}}
                    {{name}}<br />
                {{/albums}}
            </td>
            <td><a href="{{id}}/#/">Lien</a></td>
            <td>
                <button class="btn">Modifier</button>
                <button class="btn btn-danger">Supprimer</button>
            </td>
        </tr>
    {{/data.tokens}}
    </tbody>
</table>
{{/data.tokens.length}}

<h3>Créer un nouveau groupe de visiteurs</h3>
<form class="well form-search" action="#/group" method="put">
    <input type="text" required name="label" class="input" placeholder="Nom du nouveau groupe">
    <button type="submit" class="btn btn-primay">Créer le groupe</button>
</form>

<!-- ----- -->
<!-- Modal -->
<form id="modal-configuration"  class="modal hide fade form-vertical" action="#/administration/configuration" method="post">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>Configuration générale</h3>
    </div>
    <div class="modal-body">
        <div class="control-group">
            <label for="title">Titre de la galerie photos</label>
            <div class="controls">
                <input class="span5" type="text" required id="title" name="title" placeholder="Exemple : Galerie photos personnelle" value="{{data.configuration.title}}" />
            </div>
        </div>
        <div class="control-group">
            <label for="sourceDirectory">Répertoire source (contenant les photos au format original) :</label>
            <div class="controls">
                <input class="span5" type="text" required id="sourceDirectory" name="sourceDirectory" placeholder="Exemple : /home/user/photos/" value="{{data.configuration.source_path}}" />
            </div>
        </div>
        <div class="control-group">
            <label for="targetDirectory">Répertoire de travail (qui contiendra notamment les vignettes des photos) :</label>
            <div class="controls">
                <input class="span5" type="text" required id="targetDirectory" name="targetDirectory" placeholder="Exemple : /home/user/thumbnails/" value="{{data.configuration.target_path}}" />
            </div>
            <label class="checkbox">
                <input type="checkbox" name="force"> Forcer la regénération des vignettes de photos existantes
            </label>
        </div>
    </div>
    <div class="modal-footer">
        <button type="reset" class="btn">Annuler</button>
        <input type="submit" class="btn btn-primary" data-loading-text="Traitement en cours ..." value="Valider" />
    </div>
</form>


<form id="edit_album" class="modal hide fade form-horizontal" action="#/album" method="post">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>Modifier un album</h3>
    </div>
    <div class="modal-body">
        <p></p>
        <div class="control-group">
            <label class="control-label" for="name">Nom de l'album</label>
            <div class="controls">
                <input type="text" required class="input-large" id="name" name="name" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="visibility">Visibilité de l'album</label>
            <div class="controls">
                <select name="visibility" id="visibility">
                    <option value="public">Public</option>
                    <option value="private">Privé</option>
                </select>
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <input type="hidden" name="id" />
        <button type="reset" class="btn">Annuler</button>
        <input type="submit" class="btn btn-primary" data-loading-text="Modification en cours ..." value="Modifier" />
    </div>
</form>

<form id="edit_token" class="modal hide fade form-horizontal" action="#/token" method="post">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>Modifier un groupe de visiteurs</h3>
    </div>
    <div class="modal-body">
        <p></p>
        <div class="control-group">
            <label class="control-label" for="label">Libellé du groupe</label>
            <div class="controls">
                <input type="text" required class="span4" id="label" name="label" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="albums">Albums visibles</label>
            <div class="controls">
                <select name="albums" class="span4" id="albums" multiple="multiple" size="10">
                {{#data.albums}}
                    <option value="{{id}}">{{name}}</option>
                {{/data.albums}}
                </select>
                <p class="help-block">Maintenez la touche <code>ctrl</code> appuyée de votre clavier et cliquez sur les albums de votre choix pour permettre la sélection multiple.</p>
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <input type="hidden" name="id" />
        <button type="reset" class="btn">Annuler</button>
        <input type="submit" class="btn btn-primary" data-loading-text="Modification en cours ..." value="Modifier" />
    </div>
</form>

<form id="modal-account" class="modal hide fade form-horizontal" action="#/administration/credentials" method="post">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>Modifier mes identifiants de connexion</h3>
    </div>
    <div class="modal-body">
        <p></p>
        <div class="control-group">
            <label class="control-label" for="username">Nom d'utilisateur</label>
            <div class="controls">
                <input type="text" required class="input-large" id="username" name="username" value="{{data.username}}" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="oldPassword">Ancien mot de passe</label>
            <div class="controls">
                <input type="text" required class="input-large" id="oldPassword" name="oldPassword" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="password">Nouveau mot de passe</label>
            <div class="controls">
                <input type="text" required class="input-large" id="password" name="password" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="confirm">Confirmation du nouveau mot de passe</label>
            <div class="controls">
                <input type="text" required class="input-large" id="confirm" name="confirm" />
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <button type="reset" class="btn">Annuler</button>
        <input type="submit" class="btn btn-primary" data-loading-text="Modification en cours ..." value="Valider" />
    </div>
</form>
