<html>
<head>
    <meta charset="utf-8">
    <title>Freemarker入门</title>
</head>
<body>

<#include "head.ftl"/>

<#--我只是一个注释，我不会有任何输出  -->
${name}你好，${message}

<#--定义简单类型-->
<#assign linkman="周先生">
联系人：${linkman}

<#--定义对象类型-->
<#assign info={"mobile":"13812345678",'address':'北京市昌平区'} >
电话：${info.mobile} 地址：${info.address}

<#--if指令-->
<#if success=true>
你已通过实名认证
<#else>
你未通过实名认证
</#if>

<#--list指令-->
<#list goodsList as goods>
商品名称： ${goods.name} 价格：${goods.price}<br>
</#list>
</body>
</html>