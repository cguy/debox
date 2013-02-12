<h2 class="subtitle">{{i18n.account.comments.title}}</h2>

<div class="block">
    <h3>{{i18n.common.summary}}</h3>
    <ul class="unstyled">
        <li>{{i18n.account.comments.albums}} <span class="bold">{{albums}}</span></li>
        <li>{{i18n.account.comments.photos}} <span class="bold">{{photos}}</span></li>
        <li>{{i18n.account.comments.videos}} <span class="bold">{{videos}}</span></li>
        <li>{{i18n.account.comments.total}} <span class="bold">{{total}}</span></li>
    </ul>
    
    <h3>{{i18n.account.comments.list}}</h3>
    {{#comments.length}}
    <table id="admin-comments" class="table table-striped table-bordered table-condensed">
        <colgroup>
            <col>
            <col>
            <col>
            <col>
        </colgroup>
        <thead>
            <tr>
                <th>{{i18n.account.comments.date}}</th>
                <th>{{i18n.account.comments.author}}</th>
                <th>{{i18n.account.comments.content}}</th>
                <th>{{i18n.account.comments.actions}}</th>
            </tr>
        </thead>
        <tbody>
            {{#comments}}
            <tr id="{{id}}">
                <td>{{date}}</td>
                <td><div>{{user.firstName}} {{user.lastName}}</div></td>
                <td>
                    {{#media.albumId}}
                    <a href="#/album/{{media.albumId}}/{{media.id}}/comments">{{content}}</a>
                    {{/media.albumId}}
                    {{^media.albumId}}
                    <a href="#/album/{{media.id}}/comments">{{content}}</a>
                    {{/media.albumId}}
                </td>
                <td>
                    <div class="btn-group">
                        <!--<button class="btn btn-small" data-modal="modal-comment-delete">{{i18n.common.modify}}</button>-->
                        <a href="#modal-comment-delete" role="button" class="btn btn-small btn-danger" data-toggle="modal">{{i18n.common.delete}}</a>
                    </div>
                </td>
            </tr>
            {{/comments}}
        </tbody>
    </table>
    {{/comments.length}}
    {{^comments.length}}
    <p class="alert">{{i18n.account.comments.empty}}</p>
    {{/comments.length}}
</div>

{{! ========================================================== }}
{{! POPUP MODAL - DELETE A TOKEN (VISITORS ACCESS)             }}
{{! ========================================================== }}
<form id="modal-comment-delete" class="modal hide fade form-horizontal" action="" data-action="#/comments/" method="delete">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3>{{i18n.account.comments.delete.title}}</h3>
    </div>
    <div class="modal-body">
        <p></p>
        <p>{{i18n.account.comments.delete.message}}</p>
    </div>
    <div class="modal-footer">
        <input type="submit" class="btn btn-danger" data-loading-text="{{i18n.common.deletion_in_progress}}" value="{{i18n.common.delete}}" />
        <button type="reset" class="btn">{{i18n.common.cancel}}</button>
    </div>
</form>
