{
"scanners":[
<#list nodes as child>
  	  { "name" : "${child.name}" , "nodeRef" : "${child.nodeRef}"}<#if child_has_next>,</#if>
</#list>
]
}