model.jsonModel = {
    widgets: [
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