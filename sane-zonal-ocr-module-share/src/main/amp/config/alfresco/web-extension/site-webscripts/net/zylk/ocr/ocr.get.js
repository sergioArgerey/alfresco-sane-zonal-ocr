model.jsonModel = {
    widgets: [
    /*
    * JSON Model ID: HEADER_LOGO
    * Widget Type:	alfresco/logo/Logo
    * Find Widget Code Snippet:	widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_LOGO");
    */

    /*
    * JSON Model ID:	HEADER_TITLE
    * Widget Type:	alfresco/header/Title
    * Find Widget Code Snippet:	widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_TITLE");
    */
    {
        id: "SET_PAGE_TITLE",
        name: "alfresco/header/SetTitle",
        config: {
            title: "Zonal OCR Profiler",
            browserTitlePrefix: "Zonal OCR Profiler"
        }
    },
    {
        id: "MY_HORIZONTAL_WIDGET_LAYOUT",
        name: "alfresco/layout/HorizontalWidgets",
        config: {
            widgetWidth: 100,
            widgets: [
                {
                  name: "js/ocr/widgets/ocrProfilerWidget",
                  config: {

                  }
                }
            ]
        }
    }]
};