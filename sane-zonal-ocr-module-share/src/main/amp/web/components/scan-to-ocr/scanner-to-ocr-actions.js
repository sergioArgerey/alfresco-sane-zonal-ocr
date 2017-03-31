var doSetupFormsValidation = function dlA_oACT_doSetupFormsValidation(form) {
	form.addValidation(this.id + "-scannerDialog", function a(h, d, g, f, c, e) {
		return h.options[h.selectedIndex].value !== "-"
	}, null, "change", null, {validationType : "mandatory"});

	//Dom.get(this.id+"-scannerDialog-loading").style.display="block";
	form.doBeforeAjaxRequest = {
	    fn : function() {
            var elem = Dom.get(this.id+"-scannerDialog-loading");
            elem.style.display="block";
            return true;
        },
        scope : this,
        obj: null
    };
};

(function() {
	YAHOO.Bubbling.fire(
	    "registerAction",
        {
            actionName : "onActionScanOcr",
            fn : function onActionScanOcr(file) {
                var actionUrl = Alfresco.constants.PROXY_URI + "/scanner/send-doc-to-scan?noderef=" + file.nodeRef;
                var configDialog = new Alfresco.module.SimpleDialog(this.id + "-scannerDialog");
                configDialog.setOptions({
                            templateUrl : Alfresco.constants.URL_SERVICECONTEXT + "modules/scan-to-ocr/select-scanner-ocr",
                            actionUrl : actionUrl,
                            doSetupFormsValidation : {
                                fn : doSetupFormsValidation,
                                scope : this
                            },
                            firstFocus : configDialog.id + "-select-scanner",
                            width : "40em",
                            onSuccess : { message : this.msg("message.scan.success")},
                            onFailure : { message : this.msg("message.scan.failure")}
                }).show()
            }
        })
})();