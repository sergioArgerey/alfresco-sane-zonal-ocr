<#assign el = args.htmlid?html />
<script type="text/javascript">//<![CDATA[

function selectMultiPDF(){

	
	if(document.getElementById("check-pdf-multipage").checked){
		document.getElementById("check-pdf-transform").checked = true;
		document.getElementById("check-pdf-transform").disabled = true;
	}else {
		document.getElementById("check-pdf-transform").checked = false;
		document.getElementById("check-pdf-transform").disabled = false;
		
	}
}


//]]></script>
<div id="${el}-scannerDialog">
   <div class="hd">${msg("title.scanner.ocr.dialog")}</div>
   <div class="bd">
      <form id="${el}-form" action="" method="post">
         <div class="yui-gd">
         	<table a>
         		<tr>
         			<td style="padding:20px 10px 10px 20px;">${msg("title.scanner.dialog")}</td>
         			<td>	               
         				<select id="${el}-select-scanner" type="text" name="select-scanner" tabindex="0">
		               		<#list scanners as scanner>
		                  		<option value="${scanner.nodeRef}">${scanner.name}</option>
	               			</#list>
	               		</select>&nbsp;*
	               </td>
         		</tr>
         		<!--
         		<tr>
         			<td style="padding:20px 10px 10px 20px;">${msg("title.ocr.dialog")}</td>
         			<td>
         				<select id="${el}-select-ocrTypeDocument" type="text" name="select-ocrTypeDocument" tabindex="0">
			               <#list ocrProfiles as ocrProfile>
			                  <option value="${ocrProfile.nodeRef}">${ocrProfile.name}</option>
			               </#list>
	               		</select>&nbsp;*
         			</td>
         		</tr>
         		-->
         		<tr> <td style="padding:10px 10px 10px 20px;">${msg("title.pdf.transform")}</td>
         		    <td style="padding:10px 10px 10px 20px;"><input type="checkbox" id="check-pdf-transform" name="check-pdf-transform"></td>
         		    
         		</tr>
         		<tr> <td style="padding:10px 10px 10px 20px;">${msg("title.pdf.multipage")}</td>
         		    <td style="padding:10px 10px 10px 20px;"><input type="checkbox" id="check-pdf-multipage" onclick="selectMultiPDF();" name="check-pdf-multipage"></td>
         		    
         		</tr>
         		
            </table>
         </div>
                           
         <#-- Imagen a mostrar mientras se escanean los documentos -->
         <div style="height:50px;">
         	<div style="text-align:center; display:none;" id="${el}-loading">         		
         		<img style="margin:20px;" src="/share/components/documentlibrary/images/ajax-loader.gif" alt="Cargando" />
         	</div>
         </div>
         <div class="bdft">
            <input type="button" id="${el}-ok" value="OK" tabindex="0" />
            <input type="button" id="${el}-cancel" value="CANCEL" tabindex="0" />
         </div>
      </form>
   </div>
</div>