<h1 class="page-header">Accueil</h1>

<ul class="thumbnails">
{{#data}}
    <li class="span3">
        <a class="thumbnail cover" href="#/album/{{name}}" style="background-image:url('{{data.baseUrl}}{{coverUrl}}')">
            <span class="container">
                <span class="title"><span>{{name}}</span></span>
                <span class="count">{{photosCount}}</span>
            </span>
        </a>
    </li>
{{/data}}
</ul>

{{^data}}
    <p class="alert"><strong>Attention : </strong>Aucun album n'a été créé pour le moment !</p>
{{/data}}
