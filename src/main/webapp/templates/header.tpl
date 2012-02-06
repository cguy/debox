<a class="brand" href="#/">Galerie photos</a>
<ul class="nav">
    <li><a href="#/">Liste des albums</a></li>
{{#data.username}}
    <li><a href="#/administration">Administration</a></li>
{{/data.username}}
</ul>
<p class="navbar-text pull-right">
{{#data.username}}
    <strong>{{data.username}}</strong> (<a href="#/logout">Déconnexion</a>)
{{/data.username}}
{{^data.username}}
    <a data-toggle="modal" href="#login">Connexion</a>
{{/data.username}}
</p>