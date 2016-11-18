monitors = "${host.monitors?keys?join(" ")}"
<#list host.monitors?values as m>

[monitor://${m.path}]
    <#if m.blacklist??>
blacklist = ${m.blacklist}
    </#if>
host = ${m.host}
</#list>