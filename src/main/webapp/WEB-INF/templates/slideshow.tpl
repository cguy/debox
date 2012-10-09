<div id="fullscreenContainer">
    <div id="fullscreenContainer_photos">
        {{#data}}
        <img id="{{id}}" src="{{url}}" class="undisplayed" />
        {{/data}}
    </div>
    <a id="slideshow-previous"><i class="icon-chevron-left"></i></a>
    <a id="slideshow-next"><i class="icon-chevron-right"></i></a>
    <div id="slideshow-label"></div>
    <div id="slideshow-options">
        <!--<a class="details" href=""><i class="icon-picture"></i></a>
        <a class="share" href=""><i class="icon-share"></i></a>-->
        {{#config.authenticated}}    
        <a class="comments" data-placement="left" rel="tooltip" href="">
            <span class="badge badge-info hide">0</span>
            <i class="icon-comment"></i>
        </a>
        {{/config.authenticated}}    
        <a class="exit" data-placement="left" rel="tooltip" title="{{i18n.slideshow.exit}}" href="#"><i class="icon-remove"></i></a>
    </div>

    <div id="slideshow-drawer" class="hide">
        <div id="slideshow-comments">
            <div class="alert alert-heading no-comments">{{i18n.comments.empty.photo}}</div>
            <form id="new-photo-comment" method="post" action="">
                <textarea name="content" required placeholder="{{i18n.comments.placeholder}}"></textarea>
                <div class="form-actions">
                    <input type="submit" class="btn btn-primary btn-small" value="{{i18n.common.validate}}" />
                </div>
            </form>
        </div>
        <div id="slideshow-details">
            <h3>Informations</h3>
        </div>
    </div>
</div>
