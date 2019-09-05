<!DOCTYPE html>
<html lang="en">
<#include "head.ftl">
<body>
<#include "navbar.ftl">
<div class="container">
    <#list vos as vo>
        <hr/>
        <div class="row">
            <div class="col-sm-2">
                <a href="/msg/detail?conversationId=${vo.message.conversationId}">
                    <img src="${vo.user.headUrl}" width="40" height="40" alt="">
                </a>
            </div>
            <div class="col-sm-10">
                <div class="row">
                    <div class="col-sm-2">
                        ${vo.user.name}
                    </div>
                    <div class="col-sm-7"></div>
                    <div class="col-sm-3">
                        ${vo.message.createdDate?datetime}
                    </div>
                </div>
                <div class="row">
                    <div class="col-sm-9">
                        ${vo.message.content}
                    </div>
                    <div class="col-sm-3">
                        共${vo.message.id}条消息
                    </div>
                </div>
            </div>
        </div>
    </#list>
</div>
</body>
</html>