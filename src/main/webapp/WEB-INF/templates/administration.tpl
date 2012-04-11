<h1 class="page-header">Administration</h1>

<div id="administration">
    <div id="sync-progress" class="alert alert-info hide">
        <h3 class="alert-heading" style="line-height:50px;">
            <span id="progress-label">Synchronisation en cours&hellip;</span>&nbsp;<span id="progress-percentage"></span>
            <button class="btn btn-warning pull-right" style="margin: 5px -20px 0 20px;">Annuler la synchronisation</button>
        </h3>
        <div class="progress progress-info progress-striped active" style="margin:0 -20px 10px 0">
            <div class="bar"></div>
        </div>
    </div>

    <ul class="nav nav-tabs">
        <li class="active">
            <a href="#/administration/configuration" data-target="#configuration"><i class="icon-cogs"></i>&nbsp;Configuration générale</a>
        </li>
        <li>
            <a href="#/administration/sync" data-target="#sync"><i class="icon-refresh"></i>&nbsp;Synchroniser les répertoires</a>
        </li>
        <li>
            <a href="#/administration/albums" data-target="#albums"><i class="icon-camera-retro"></i>&nbsp;Gestion des albums photos</a>
        </li>
        <li>
            <a href="#/administration/tokens" data-target="#tokens"><i class="icon-user"></i>&nbsp;Gestion des accès visiteurs</a>
        </li>
        <li>
            <a href="#/administration/account" data-target="#account"><i class="icon-pencil"></i>&nbsp;Modifier mes identifiants de connexion</a>
        </li>   
    </ul>

    <div class="tab-content">

        {{! Configuration form }}
        <div id="configuration" class="tab-pane active">
            <form  class="form-vertical" action="#/administration/configuration" method="post">
                <h2 class="page-header">Configuration générale</h2>
                <p class="alert hide"></p>
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
                <div class="form-actions">
                    <input type="hidden" name="force" />
                    <button type="button" class="btn btn-danger" data-loading-text="Traitement en cours ...">Enregistrer et synchroniser les répertoires</button>
                    <input type="submit" class="btn btn-primary" data-loading-text="Traitement en cours ..." value="Enregistrer" />
                </div>
            </form>
        </div>
        {{! End of configuration form }}

        {{! Synchronization form }}
        <div id="sync" class="tab-pane">
            <form class="form-vertical" action="#/administration/sync" method="post">
                <h2 class="page-header">Synchroniser les répertoires</h2>
                <div class="control-group">
                    <p class="error"></p>
                    <div class="control-group">
                        <label class="control-label">Veuillez choisir le mode de synchronisation et confirmer la demande :</label>
                        <div class="controls">
                            <label class="radio"><input type="radio" name="mode" value="fast" /><strong>Le plus rapide :</strong> Aucune pré-génération des vignettes. Les vignettes seront générées lors de leur premier accès.</label>
                            <label class="radio"><input type="radio" name="mode" value="normal" checked /><strong>Normal :</strong> Pré-génération des vignettes pour les nouvelles photos.</label>
                            <label class="radio">
                                <input type="radio" name="mode" value="slow" /><strong>Le plus long :</strong> Regénération des vignettes existantes + création des vignettes pour les nouvelles photos.
                            </label>
                            <div class="alert alert-warning"><span class="label label-warning">Attention</span>&nbsp;Ce dernier mode de synchronisation supprimera toutes les vignettes existantes avant de les regénérer.</div>
                        </div>
                    </div>
                </div>
                <div class="form-actions">
                    <input type="submit" class="btn btn-primary" data-loading-text="Traitement en cours ..." value="Lancer la synchronisation" />
                </div>
            </form>
        </div>
        {{! End of synchronization form }}

        {{! Albums }}
        <div id="albums" class="tab-pane">
            {{> admin.albums}}
        </div>
        {{! End of albums }}

        {{! Tokens }}
        <div id="tokens" class="tab-pane">
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
                        <td><a href="{{id}}#/">Lien</a></td>
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
                <input type="text" required name="label" class="input" placeholder="Nom du nouvel accès">
                <button type="submit" class="btn btn-primary">Créer l'accès</button>
            </form>
        </div>
        {{! End of tokens }}

        {{! Account information form }}
        <div id="account" class="tab-pane">
            <form class="form-horizontal" action="#/administration/credentials" method="post">
                <h2 class="page-header">Modifier mes identifiants de connexion</h2>
                <p class="hide"><a class="close">&times;</a></p>
                <div class="control-group">
                    <label class="control-label" for="username">Nouveau nom d'utilisateur :</label>
                    <div class="controls">
                        <input type="text" required class="input-large" id="username" name="username" value="{{data.username}}" />
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="oldPassword">Ancien mot de passe :</label>
                    <div class="controls">
                        <input type="password" required class="input-large" id="oldPassword" name="oldPassword" />
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="password">Nouveau mot de passe :</label>
                    <div class="controls">
                        <input type="password" required class="input-large" id="password" name="password" />
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="confirm">Confirmation du nouveau mot de passe :</label>
                    <div class="controls">
                        <input type="password" required class="input-large" id="confirm" name="confirm" />
                    </div>
                </div>
                <div class="form-actions">
                    <input type="submit" class="btn btn-primary" data-loading-text="Modification en cours ..." value="Valider" />
                </div>
            </form>
        </div>
        {{! End of account information form}}
    </div>
    {{! End of tabs}}

</div>
{{! End of administration block }}

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
        <input type="submit" class="btn btn-primary" data-loading-text="Modification en cours ..." value="Modifier" />
        <button type="reset" class="btn">Annuler</button>
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
        <form class="form-horizontal" method="post" action="uploadThumbnails" target="uploadFrame" enctype="multipart/form-data" onsubmit="handleArchiveUpload()">
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
        <input type="submit" class="btn btn-primary" data-loading-text="Modification en cours ..." value="Modifier" />
        <button type="reset" class="btn">Annuler</button>
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
        <input type="submit" class="btn btn-danger" data-loading-text="Suppression en cours ..." value="Supprimer" />
        <button type="reset" class="btn">Annuler</button>
    </div>
</form>

{{! ========================================================== }}
{{! POPUP MODAL - EDIT ACCOUNT INFORMATION }}
{{! ========================================================== }}

