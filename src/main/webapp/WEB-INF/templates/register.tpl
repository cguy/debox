<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
    <head></head>
    <body th:fragment="body">
        <div class="page-header">
            <h1 th:if="${isSignIn}" th:text="#{signin.title}"></h1>
            <h1 th:if="${not isSignIn}" th:text="#{registration.title}"></h1>
        </div>

        <h2 class="subtitle" th:if="${isSignIn}" th:text="#{signin.subtitle}"></h2>
        <h2 class="subtitle" th:if="${not isSignIn}" th:text="#{registration.subtitle}"></h2>
        <p class="center" th:if="${not #lists.isEmpty(config.providers)}">
            <a th:href="${provider.url}" th:class="'btn social ' + ${provider.id}" th:each="provider : ${config.providers}" th:if="${provider.enabled}" th:inline="text">
                <span class="logo"></span>
                [[${isSignIn} ? #{signin.providerPrefix} : #{registration.providerPrefix}]]
                [[#{name}]]
            </a>
        </p>
        <p class="mail" th:if="${not #lists.isEmpty(config.providers)}" th:text="${isSignIn} ? #{signin.altChoice} : #{registration.altChoice}"></p>
        <form id="register" class="form-horizontal" th:action="${isSignIn} ? 'login' : 'register'" method="post">
            <p class="alert alert-danger" th:text="#{registration.errors.alreadyRegistered}" th:if="${alreadyRegistered}"></p>
            <p class="alert alert-danger" th:text="#{registration.errors.internal}" th:if="${error}"></p>
            <p class="alert alert-danger" th:text="#{signin.errors.500}" th:if="${signInError}"></p>
            <p class="alert alert-danger" th:text="#{registration.errors.mandatoryFields}" th:if="${mandatoryFields}"></p>
            <p class="alert alert-danger" th:text="#{registration.errors.passwordMatch}" th:if="${passwordMatch}"></p>
            <p>
                <input type="email" name="username" required="required" th:placeholder="#{registration.placeholder.mail}" />
            </p>
            <p>
                <input type="password" name="password" required="required" th:placeholder="#{registration.placeholder.password}" />
            </p>
            <p th:if="${not isSignIn}">
                <input type="password" name="confirm" required="required" th:placeholder="#{registration.placeholder.confirm}" />
            </p>
            <p th:if="${not isSignIn}">
                <input type="text" name="firstname" class="firstname" required="required" th:placeholder="#{registration.placeholder.firstname}" />
                <input type="text" name="lastname" class="lastname" required="required" th:placeholder="#{registration.placeholder.lastname}" />
            </p>
            <p th:if="${not isSignIn}" class="small" th:text="#{registration.note}"></p>
            <div class="form-actions">
                <input type="submit" class="btn btn-primary" th:value="${isSignIn} ? #{common.connection} : #{registration.finish}" />
            </div>
        </form>
    </body>
</html>
