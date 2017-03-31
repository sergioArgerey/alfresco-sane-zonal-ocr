var connector = remote.connect("alfresco");
var result = connector.get("/scanner/get/perfiles");

if(result.status == 200){
	
	var jsonresult = eval('(' + result + ')');	
	model.scanners = jsonresult["scanners"];
	
}

var resultProfiles = connector.get("/scanner/get/ocrProfiles");

if(result.status == 200){
	
	var jsonresult = eval('(' + resultProfiles + ')');	
	model.ocrProfiles = jsonresult["ocrProfiles"];
	
}

