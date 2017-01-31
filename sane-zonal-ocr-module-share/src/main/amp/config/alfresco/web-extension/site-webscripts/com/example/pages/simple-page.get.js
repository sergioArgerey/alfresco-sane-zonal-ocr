model.jsonModel = {
    widgets: [{
        id: "SET_PAGE_TITLE",
        name: "alfresco/header/SetTitle",
        config: {
            title: "This is an advanced page with Ajax and DOM operations"
        }
    },
    {
        name: "example/widgets/AjaxWidget"
        //name: "example/widgets/ocrProfilerWidget"
    }]
};

//
//model.jsonModel = {
//    widgets: [{
//        id: "SET_PAGE_TITLE",
//        name: "alfresco/header/SetTitle",
//        config: {
//            title: "This is a simple page"
//        }
//    },
//    {
//        id: "MY_HORIZONTAL_WIDGET_LAYOUT",
//        name: "alfresco/layout/HorizontalWidgets",
//        config: {
//            widgetWidth: 50,
//            widgets: [
//                {
//                    name: "alfresco/logo/Logo",
//                    config: {
//                        logoClasses: "alfresco-logo-only"
//                    }
//                },
//                {
//                  name: "example/widgets/TemplateWidget"
//                }
//            ]
//        }
//    }]
//};

//model.jsonModel = {
//widgets: [
//         {
//            id: "HEADER_BAR",
//            name: "alfresco/header/Header",
//            config: {
//               widgets: [
//                  {
//                     id: "APP_MENU_BAR",
//                     name: "alfresco/header/AlfMenuBar",
//                     align: "left",
//                     config: {
//                        widgets: [
//                           {
//                              id: "HOME",
//                              name: "alfresco/menus/AlfMenuBarItem",
//                              config: {
//                                 label: "Home",
//                                 targetUrl: "ap/ws/home"
//                              }
//                           }
//                        ]
//                     }
//                  },
//                  {
//                     id: "USER_MENU_BAR",
//                     name: "alfresco/header/AlfMenuBar",
//                     align: "right",
//                     config: {
//                        widgets: [
//                           {
//                              id: "USER_MENU",
//                              name: "alfresco/header/AlfMenuBarPopup",
//                              config: {
//                                 label: "User Menu",
//                                 widgets: [
//                                    {
//                                       id: "HEADER_USER_MENU",
//                                       name: "alfresco/menus/AlfMenuGroup",
//                                       config: {
//                                          widgets: [
//                                             {
//                                                id: "LOGOUT",
//                                                name: "alfresco/header/AlfMenuItem",
//                                                config:
//                                                {
//                                                   label: "Logout",
//                                                   iconClass: "alf-user-logout-icon",
//                                                   publishTopic: "ALF_DOLOGOUT"
//                                                }
//                                             }
//                                          ]
//                                       }
//                                    }
//                                 ]
//                              }
//                           }
//                        ]
//                     }
//                  }
//               ]
//            }
//         }
//      ]
//};




//var pageTitle = {
//    id: "SET_PAGE_TITLE",
//    name: "alfresco/header/SetTitle",
//    config: {
//        title: "myModule Title"
//    }
//};
//
//var columnAlfrescoLogo = {
//   name : "alfresco/logo/Logo",
//   widthPx : "80",
//   config : {
//       logoClasses: "alfresco-logo-only"
//   }
//};
//
//var columnSearch = {
//    id: "CON_SEARCH_COLUMN",
//    name: "myModule/MyModuleWidget",
//    widthPc : "50",
//};
//
//var columnResult = {
//    id: "CON_SEARCH_RESULT",
//    name: "alfresco/documentlibrary/views/AlfTableView",
//    widthPc : "50",
//};
//
//model.jsonModel = {
//    widgets : [ {
//        name : "alfresco/layout/HorizontalWidgets",
//        config : {
//            widgetMarginLeft : "5",
//            widgetMarginRight : "5",
//            widgets : [ pageTitle, columnAlfrescoLogo, columnSearch, columnResult ]
//        }
//    } ]
//};