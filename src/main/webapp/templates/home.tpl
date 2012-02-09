<h1 class="page-header">Accueil</h1>

<ul class="thumbnails">
{{#data}}
    <li class="span3">
        <i class="icon-list-alt"></i>&nbsp;<a href="#/album/{{name}}">{{name}}</a>
        <a class="thumbnail" href="#/album/{{name}}">
            <img class="album" src="{{data.baseUrl}}{{coverUrl}}" alt="{{name}}" style="background-color:#ddd;width:210px;"/>
        </a>
    </li>
{{/data}}
</ul>

{{^data}}
    <p class="alert"><strong>Attention : </strong>Aucun album n'a été créé pour le moment !</p>
{{/data}}