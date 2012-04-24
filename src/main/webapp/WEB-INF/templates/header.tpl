<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> 
    <span class="icon-bar"></span> 
    <span class="icon-bar"></span> 
    <span class="icon-bar"></span> 
</a>

{{#data.title}}<a class="brand" href="#/">{{data.title}}</a>{{/data.title}}

<div class="nav-collapse">
    <ul class="nav">
        <li><a href="#/"><i class="icon-home icon-white"></i>&nbsp;&nbsp;Liste des albums</a></li>
        {{#data.username}}
        <li><a href="#/administration"><i class="icon-cogs icon-white"></i>&nbsp;&nbsp;Administration</a></li>
        {{/data.username}}
    </ul>
<!--    <p class="navbar-text pull-right">
        {{#data.username}}
            <i class="icon-user icon-white"></i>&nbsp;&nbsp;<strong>{{data.username}}</strong> (<a href="#/logout">Déconnexion</a>)
        {{/data.username}}
        {{^data.username}}
        <a data-toggle="modal" href="#login">Connexion</a>
        {{/data.username}}
    </p>-->

    {{#data.username}}
    <p class="navbar-text pull-right">
        <i class="icon-user icon-white"></i>&nbsp;&nbsp;<strong>{{data.username}}</strong> (<a href="#/logout">Déconnexion</a>)
    </p>
    {{/data.username}}
    {{^data.username}}
    <ul class="nav pull-right">
        <li class="dropdown" id="login">
            <a class="dropdown-toggle" href="#login" data-toggle="dropdown">&nbsp;&nbsp;Connexion <b class="caret"></b></a>
            <ul class="dropdown-menu dropdown-form" id="login-dropdown">
                <li>
                    <form id="login" action="#/authenticate" method="post">
                        <p></p>
                        <div class="control-group">
                            <label class="control-label" for="username">Nom d'utilisateur</label>
                            <div class="controls">
                                <input type="text" required id="username" name="username" />
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="password">Mot de passe</label>
                            <div class="controls">
                                <input type="password" required id="password" name="password" />
                            </div>
                        </div>
                        <div class="form-actions">
                            <input id="connect" type="submit" class="btn btn-primary" data-loading-text="Connexion en cours ..." value="Connexion" />
                        </div>
                    </form>
                </li>
            </ul>
        </li>
        {{/data.username}}
    </ul>
</div>