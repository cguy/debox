<h1 class="page-header">Administration</h1>

<div id="sync-progress" class="alert alert-info hide">
    <h3 class="alert-heading" style="line-height:50px;">
        <span id="progress-label">Synchronisation en cours</span>&hellip;&nbsp;<span id="progress-percentage"></span>
        <button class="btn btn-warning pull-right" style="margin: 5px -20px 0 20px;">Annuler la synchronisation</button>
    </h3>
    <div class="progress progress-info progress-striped active" style="margin:0 -20px 10px 0">
        <div class="bar"></div>
    </div>
</div>

<ul class="thumbnails admin">
    <li class="span4">
        <a href="#modal-configuration" data-toggle="modal" class="thumbnail">
            <img src="{{data.baseUrl}}img/folder-settings.png" alt="" title="" />
            Configuration générale
        </a>
    </li>
    <li class="span4">
        <a href="#modal-sync" data-toggle="modal" class="thumbnail">
            <img src="{{data.baseUrl}}img/folder-script.png" alt="" title="" />
            Synchroniser les répertoires
        </a>
    </li>
    <li class="span4">
        <a href="#albums" class="thumbnail">
            <img src="{{data.baseUrl}}img/folder-images.png" alt="" title="" />
            Gestion des albums photos
        </a>
    </li>
    <li class="span4">
        <a href="#tokens" class="thumbnail">
            <img src="{{data.baseUrl}}img/folder-web.png" alt="" title="" />
            Gestion des accès visiteurs
        </a>
    </li>
    <li class="span4">
        <a data-toggle="modal" href="#modal-account" class="thumbnail">
            <img src="{{data.baseUrl}}img/folder-user.png" alt="" title="" />
            Modifier mes identifiants de connexion
        </a>
    </li>    
</ul>

<div id="albums" class="hide admin_part">
    <h2 class="page-header">Liste des albums</h2>
    {{#data.albums.length}}
    <table id="administration_albums" class="table table-striped table-bordered table-condensed">
        <thead>
            <tr>
                <th>Nom de l'album</th>
                <th>Répertoire source</th>
                <th style="width:65px;text-align:center;">Téléchargeable</th>
                <th style="width:65px;text-align:center;">Visibilité</th>
                <th style="width:275px;">Actions</th>
            </tr>
        </thead>
        <tbody>
        {{#data.albums}}
            <tr id="{{id}}">
                <td class="name"><strong>{{name}}</strong></td>
                <td class="relativePath">{{relativePath}}</td>
                <td class="downloadable" style="text-align: center;">
                    {{#downloadable}}
                        <i class="icon-ok"></i>&nbsp;Oui
                    {{/downloadable}}
                    {{^downloadable}}
                        <i class="icon-ban-circle"></i>&nbsp;Non
                    {{/downloadable}}
                </td>
                <td class="visibility">
                    {{#visibility}}
                        <i class="icon-ok"></i>&nbsp;Public
                    {{/visibility}}
                    {{^visibility}}
                        <i class="icon-ban-circle"></i>&nbsp;Privé
                    {{/visibility}}
                </td>
                <td>
                    <div class="btn-group">
                        <button class="btn actions"><i class="icon-cog"></i>&nbsp;Actions</button>
                        <button class="btn edit"><i class="icon-pencil"></i>&nbsp;Modifier</button>
                        <a class="btn" target="_blank" href="download/album/{{id}}"><i class="icon-download-alt"></i>&nbsp;Télécharger</a>
                    </div>
                </td>
            </tr>
        {{/data.albums}}
        </tbody>
    </table>
    {{/data.albums.length}}

    {{^data.albums}}
    <p class="alert">Aucun album n'a été créé pour le moment !</p>
    {{/data.albums}}
</div>

<div id="tokens" class="hide admin_part">
    <h2 class="page-header">Gestion des accès visiteurs</h2>
    <h3>Liste des groupes</h3>
    <table id="administration_tokens" class="table table-striped table-bordered table-condensed{{^data.tokens}} hide{{/data.tokens}}">
        <thead>
            <tr>
                <th>Libellé</th>
                <th>Albums</th>
                <th style="width:120px;">Lien à partager</th>
                <th style="width:185px;">Actions</th>
            </tr>
        </thead>
        <tbody>
        {{#data.tokens}}
            <tr id="{{id}}">
                <td class="access_label">{{label}}</td>
                <td class="albums">
                    {{^albums}}
                        Aucun album n'est visible pour cet accès
                    {{/albums}}
                    {{#albums}}
                        <a href="#/album/{{name}}">{{name}}</a><br />
                    {{/albums}}
                </td>
                <td><a href="{{id}}/#/">Lien</a></td>
                <td>
                    <div class="btn-group">
                        <button class="btn btn-info"><i class="icon-pencil icon-white"></i>&nbsp;Modifier</button>
                        <button href="#modal-token-delete" data-toggle="modal" class="btn btn-danger"><i class="icon-remove icon-white"></i>&nbsp;Supprimer</button>
                    </div>
                </td>
            </tr>
        {{/data.tokens}}
        </tbody>
    </table>
    <p class="alert alert-warning{{#data.tokens.length}} hide{{/data.tokens.length}}">Aucun accès visiteur n'a été créé !</p>

    <h3>Créer un nouveau accès visiteur</h3>
    <form id="form-token-create" class="well form-search" action="#/token" method="put">
        <input type="text" required name="label" class="input" placeholder="Nom du nouveau accès">
        <button type="submit" class="btn btn-primay">Créer l'accès</button>
    </form>
</div>

{{! ========================================================== }}
{{! POPUP MODAL - EDIT OVERALL CONFIGURATION }}
{{! ========================================================== }}
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
        </div>
    </div>
    <div class="modal-footer">
        <input type="hidden" name="force" />
        <input type="submit" class="btn btn-danger" style="float:left;" data-loading-text="Traitement en cours ..." value="Enregistrer et synchroniser" />
        <button type="reset" class="btn">Annuler</button>
        <input type="submit" class="btn btn-primary" data-loading-text="Traitement en cours ..." value="Enregistrer et fermer" />
    </div>
</form>

{{! ========================================================== }}
{{! POPUP MODAL - LAUNCH SYNC }}
{{! ========================================================== }}
<form id="modal-sync"  class="modal hide fade form-vertical" action="#/administration/sync" method="post">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>Synchroniser les répertoires</h3>
    </div>
    <div class="modal-body">
        <div class="control-group">
            <p class="error"></p>
            <div class="control-group">
                <label class="control-label">Veuillez choisir le mode de synchronisation et confirmer la demande :</label>
                <div class="controls">
                    <label class="radio"><input type="radio" name="mode" value="fast" /><strong>Le plus rapide :</strong> Aucune pré-génération des vignettes. Les vignettes seront générées lors de leur premier accès.</label>
                    <label class="radio"><input type="radio" name="mode" value="normal" checked /><strong>Normal :</strong> Pré-génération des vignettes pour les nouvelles photos.</label>
                    <label class="radio"><input type="radio" name="mode" value="slow" /><strong>Le plus long :</strong> Regénération des vignettes existantes + création des vignettes pour les nouvelles photos.</label>
                </div>
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <button type="reset" class="btn">Annuler</button>
        <input type="submit" class="btn btn-primary" data-loading-text="Traitement en cours ..." value="Lancer la synchronisation" />
    </div>
</form>

{{! ========================================================== }}
{{! POPUP MODAL - EDIT AN ALBUM }}
{{! ========================================================== }}
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
        <div class="control-group">
            <label class="control-label" for="downloadable">Téléchargement</label>
            <div class="controls">
                <label class="checkbox">
                    <input id="downloadable" type="checkbox" name="downloadable"> Les photos de cet album sont téléchargeables par les personnes ayant accès à cet album.
                </label>
            </div>
        </div>
        
    </div>
    <div class="modal-footer">
        <input type="hidden" name="id" />
        <button type="reset" class="btn">Annuler</button>
        <input type="submit" class="btn btn-primary" data-loading-text="Modification en cours ..." value="Modifier" />
    </div>
</form>

{{! ========================================================== }}
{{! POPUP MODAL - PROCESS AN ACTION ON AN ALBUM }}
{{! ========================================================== }}
<div id="actions_album" class="modal fade">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>Actions sur un album</h3>
    </div>
    <div class="modal-body">
        <!--<h2 class="page-header">Régénération des vignettes par le serveur</h2>
        <button class="btn">Rénégérer toutes les vignettes de l'album</button>-->

        <h2 class="page-header">Choix de la couverture de l'album</h2>
        <button id="actions_album_random_cover" class="btn btn-info pull-right">Random</button>
        <div style="width: 430px; margin-bottom: 20px">
            <div style="margin: auto" id="actions_album_thumbnail" class="thumbnail cover"></div>
        </div>

        <h2 class="page-header">Chargement d'une archive de vignettes</h2>

        <iframe id="uploadFrame" name="uploadFrame" height="0" width="0" frameborder="0" scrolling="yes"></iframe>
        <form class="form-horizontal" method="post" action="{{data.baseUrl}}uploadThumbnails" target="uploadFrame" enctype="multipart/form-data" onsubmit="handleArchiveUpload()">
            <input type="hidden" name="albumId" />
            <div>
                    <input type="submit" class="btn btn-info pull-right" style="display:inline-block;" value="Charger"/>
                    <label for="file" style="font-weight: bold;">Archive (*.zip) : 
                    <input id="file" name="file" type="file" required style="width:300px;display:inline-block;" />&nbsp;
                    </label>
            </div>
        </form>

        <div id="upload-progress" class="alert alert-info hide">
            <h3>Chargement en cours&hellip;&nbsp;<span>0 %</span></h3>
            <div class="progress progress-info progress-striped active" style="margin:0 -20px 10px 0">
                <div class="bar"></div>
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <button type="reset" class="btn">Retour</button>
    </div>
    </form>
</div>

{{! ========================================================== }}
{{! POPUP MODAL - EDIT A TOKEN (VISITORS ACCESS) }}
{{! ========================================================== }}
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

{{! ========================================================== }}
{{! POPUP MODAL - DELETE A TOKEN (VISITORS ACCESS) }}
{{! ========================================================== }}
<form id="modal-token-delete" class="modal hide fade form-horizontal" action="#/token" method="delete">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>Supprimer un accès visiteur</h3>
    </div>
    <div class="modal-body">
        <p></p>
        <p>Êtes-vous sûr de vouloir supprimer l'accès visiteur <strong></strong>&nbsp;?</p>
    </div>
    <div class="modal-footer">
        <input type="hidden" name="id" />
        <button type="reset" class="btn">Annuler</button>
        <input type="submit" class="btn btn-danger" data-loading-text="Suppression en cours ..." value="Supprimer" />
    </div>
</form>

{{! ========================================================== }}
{{! POPUP MODAL - EDIT ACCOUNT INFORMATION }}
{{! ========================================================== }}
<form id="modal-account" class="modal hide fade form-horizontal" action="#/administration/credentials" method="post">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>Modifier mes identifiants de connexion</h3>
    </div>
    <div class="modal-body">
        <p></p>
        <div class="control-group">
            <label class="control-label" for="username">Nouveau nom d'utilisateur</label>
            <div class="controls">
                <input type="text" required class="input-large" id="username" name="username" value="{{data.username}}" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="oldPassword">Ancien mot de passe</label>
            <div class="controls">
                <input type="password" required class="input-large" id="oldPassword" name="oldPassword" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="password">Nouveau mot de passe</label>
            <div class="controls">
                <input type="password" required class="input-large" id="password" name="password" />
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="confirm">Confirmation du nouveau mot de passe</label>
            <div class="controls">
                <input type="password" required class="input-large" id="confirm" name="confirm" />
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <button type="reset" class="btn">Annuler</button>
        <input type="submit" class="btn btn-primary" data-loading-text="Modification en cours ..." value="Valider" />
    </div>
</form>
