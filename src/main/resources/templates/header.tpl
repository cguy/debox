{{#data.title}}<a class="brand" href="#/">{{data.title}}</a>{{/data.title}}
<ul class="nav">
    <li><a href="#/"><i class="icon-home icon-white"></i>&nbsp;&nbsp;Liste des albums</a></li>
{{#data.username}}
    <li><a href="#/administration"><i class="icon-cog icon-white"></i>&nbsp;&nbsp;Administration</a></li>
{{/data.username}}
</ul>
<p class="navbar-text pull-right">
{{#data.username}}
    <i class="icon-user icon-white"></i>&nbsp;&nbsp;<strong>{{data.username}}</strong> (<a href="#/logout">Déconnexion</a>)
{{/data.username}}
{{^data.username}}
    <a data-toggle="modal" href="#login">Connexion</a>
{{/data.username}}
</p>