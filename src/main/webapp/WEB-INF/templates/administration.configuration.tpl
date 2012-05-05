<form  class="form-vertical" action="#/administration/configuration" method="post">
    <h2 class="page-header">{{i18n.administration.config.title}}</h2>
    <p class="alert hide"></p>
    <div class="control-group">
        <label for="title">{{i18n.administration.config.galery_title}}</label>
        <div class="controls">
            <input class="span5" type="text" required id="title" name="title" placeholder="{{i18n.administration.config.galery_title_placeholder}}" value="{{title}}" />
        </div>
    </div>
    <div class="control-group">
        <label for="sourceDirectory">{{i18n.administration.config.source_directory}}:</label>
        <div class="controls">
            <input class="span5" type="text" required id="sourceDirectory" name="sourceDirectory" placeholder="{{i18n.administration.config.source_directory_placeholder}}" value="{{source_path}}" />
        </div>
    </div>
    <div class="control-group">
        <label for="targetDirectory">{{i18n.administration.config.target_directory}}:</label>
        <div class="controls">
            <input class="span5" type="text" required id="targetDirectory" name="targetDirectory" placeholder="{{i18n.administration.config.target_directory_placeholder}}" value="{{target_path}}" />
        </div>
    </div>
    <div class="form-actions">
        <input type="hidden" name="force" />
        <button type="button" class="btn btn-danger" data-loading-text="{{i18n.administration.processing}}">{{i18n.administration.config.save_and_sync}}</button>
        <input type="submit" class="btn btn-primary" data-loading-text="{{i18n.administration.processing}}" value="{{i18n.administration.config.save}}" />
    </div>
</form>