<div class="page-header">
    <h1>
        {{#isSignIn}}
            {{i18n.signin.title}}
        {{/isSignIn}}
        {{^isSignIn}}
            {{i18n.registration.title}}
        {{/isSignIn}}
    </h1>
</div>

<h2 class="subtitle">
    {{#isSignIn}}
        {{i18n.signin.subtitle}}
    {{/isSignIn}}
    {{^isSignIn}}
        {{i18n.registration.subtitle}}
    {{/isSignIn}}
</h2>
{{#config.providers.length}}
    <p class="center">
        {{#config.providers}}
            {{#enabled}}
                <a href="{{url}}" class="btn social {{id}}">
                    <span class="logo"></span>
                    {{#isSignIn}}
                        {{i18n.signin.providerPrefix}}
                    {{/isSignIn}}
                    {{^isSignIn}}
                        {{i18n.registration.providerPrefix}}
                    {{/isSignIn}}
                    {{name}}
                </a>
            {{/enabled}}
        {{/config.providers}}
    </p>
    <p class="mail">
        {{#isSignIn}}
            {{i18n.signin.altChoice}}
        {{/isSignIn}}
        {{^isSignIn}}
            {{i18n.registration.altChoice}}
        {{/isSignIn}}
    </p>
{{/config.providers.length}}
<form id="register" class="form-horizontal" action="{{#isSignIn}}authenticate{{/isSignIn}}{{^isSignIn}}register{{/isSignIn}}" method="post">
    {{#alreadyRegistered}}
    <p class="alert alert-danger">{{i18n.registration.errors.alreadyRegistered}}</p>
    {{/alreadyRegistered}}
    {{#error}}
    <p class="alert alert-danger">{{i18n.registration.errors.internal}}</p>
    {{/error}}
    {{#success}}
    <p class="alert alert-success">{{{i18n.registration.success}}}</p>
    {{/success}}
    {{^success}}
    <p>
        <input type="email" name="username" required placeholder="{{i18n.registration.placeholder.mail}}">
    </p>
    <p>
        <input type="password" name="password" required placeholder="{{i18n.registration.placeholder.password}}">
    </p>
    {{^isSignIn}}
    <p>
        <input type="text" name="firstname" class="firstname" required placeholder="{{i18n.registration.placeholder.firstname}}">
        <input type="text" name="lastname" class="lastname" required placeholder="{{i18n.registration.placeholder.lastname}}">
    </p>
    <p class="small">{{i18n.registration.note}}</p>
    {{/isSignIn}}
    <div class="form-actions">
        <input type="submit" class="btn btn-primary" value="{{#isSignIn}}{{i18n.common.connection}}{{/isSignIn}}{{^isSignIn}}{{i18n.registration.finish}}{{/isSignIn}}" />
    </div>
    {{/success}}
</form>
