<div id="{{id}}" class="comment">
    <div class="authorPicture" style="background-image: url('{{#user.avatar}}{{user.avatar}}{{/user.avatar}}{{^user.avatar}}img/avatar_male_light_on_gray_32x32.png{{/user.avatar}}')"></div>
    <div class="date">{{date}}</div>
    {{#deletable}}<a href="#" class="remove" title="{{i18n.comments.remove}}" rel="tooltip" data-placement="left"><i class="icon-remove"></i></a>{{/deletable}}
    <div class="author">{{user.firstName}} {{user.lastName}}</div>
    <div class="content">{{content}}</div>
</div>
