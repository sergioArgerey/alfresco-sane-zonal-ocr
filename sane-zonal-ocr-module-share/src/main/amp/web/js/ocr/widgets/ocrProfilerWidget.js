define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "alfresco/core/Core",
        "alfresco/core/CoreXhr",
        "dojo/dom-construct",
        "dojo/_base/array",
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/ocrProfilerWidget.html",
        "jquery",
        "jquery.imgareaselect",
        "ocr.profiler"
    ],
    function(declare, _Widget, Core, AlfCoreXhr, domConstruct, array, _Templated, template, $) {
        return declare([_Widget, Core, AlfCoreXhr, _Templated], {
            templateString: template,
            cssRequirements: [{cssFile:"./css/ocr-profiler.css"}],
            i18nRequirements: [ {i18nFile: "./i18n/ocr-profiler.properties"} ],

            buildRendering: function ocrProfilerWidget__buildRendering() {
                console.debug(this.message('widgetTitle'));
                console.debug(this.message('columnName'));
                console.debug(this.message('columnDescription'));
                console.debug(arguments);

                this.widgetTitle       = this.message('widgetTitle');
                this.columnName        = this.message('columnName');
                this.columnDescription = this.message('columnDescription');
                this.inherited(arguments);
            },

            postCreate: function ocrProfilerWidget__postCreate() {

                function preview(img, selection) {
                    if (!selection || !selection.width || !selection.height) {
                        return;
                    }

                    //console.info(JSON.stringify(selection));

                    $("#realX").val(pic_real_width);
                    $("#realY").val(pic_real_height);

                    var actual_with = document.getElementById("photo").width;
                    var actual_height = document.getElementById("photo").height;

                    console.info("pic_real_width preview: " + pic_real_width);
                    console.info("pic_real_height preview: " + pic_real_height);
                    console.info("actual_with: " + actual_with);
                    console.info("actual_height: " + actual_height);

                    $('#x1').val( Math.round((selection.x1 / actual_with) * pic_real_width) );
                    $('#y1').val( Math.round((selection.y1 / actual_height) * pic_real_height) );
                    $('#x2').val( Math.round((selection.x2 / actual_with) * pic_real_width) );
                    $('#y2').val( Math.round((selection.y2 / actual_height) * pic_real_height) );
                    $('#width').val( Math.round((selection.width / actual_with) * pic_real_width) );
                    $('#height').val( Math.round((selection.height / actual_height) * pic_real_height));
                }

                function listar(lista) {
                    var divLista = document.getElementById("lista");
                    divLista.innerHTML = "";
                    var listaDiv = document.createElement("DIV");
                    listaDiv.className = "datagrid";
                    var langArg = "language";
                    var metaArg = "metadata";
                    var typoArg = "typography";
                    var psmArg = "psm";
                    var removeArg = "btn-remove";

                    var tbody = document.createElement("div");
                    tbody.className = "col-xs-12 nopadding";

                    for (var i = 0; i < lista.length; i++) {
                        var rectTR = document.createElement("div");
                        rectTR.setAttribute("class", "col-xs-12 list-row-margin")
                        var xTD = document.createElement("div");
                        var yTD = document.createElement("div");
                        var widthTD = document.createElement("div");
                        var heightTD = document.createElement("div");
                        var metadataTD = document.createElement("div");
                        var languageTD = document.createElement("div");
                        var typographyTD = document.createElement("div");
                        var psmTD = document.createElement("div");
                        var removeTD = document.createElement("div");

                        xTD.setAttribute("class", "list-coordinate-item col-xs-half");
                        yTD.setAttribute("class", "list-coordinate-item col-xs-half");
                        widthTD.setAttribute("class", "list-coordinate-item col-xs-half");
                        heightTD.setAttribute("class", "list-coordinate-item col-xs-half");
                        metadataTD.setAttribute("class", "col-xs-3 fewer-padding");
                        languageTD.setAttribute("class", "col-xs-2 fewer-padding");
                        typographyTD.setAttribute("class", "col-xs-3 fewer-padding");
                        psmTD.setAttribute("class", "col-xs-4-and-half fewer-padding");
                        removeTD.setAttribute("class", "col-xs-1 remove-padding");

                        var languageSelect = document.createElement("SELECT");
                        languageSelect.setAttribute("id", "language-" + i);
                        languageSelect.setAttribute("class", "form-control");
                        var index = i;
                        $(languageSelect).change(function() {
                            var id = this.getAttribute("id");
                            var langArg = id.substring(0, id.indexOf('-'));
                            var index = id.substring(id.indexOf('-') + 1);
                            var valor = this.options[this.selectedIndex].value;
                            console.debug(lista[index][langArg]);
                            lista[index][langArg] = valor;
                        });

                        var metadataInput = document.createElement("INPUT");
                        metadataInput.setAttribute("id", "metadata-" + i);
                        metadataInput.setAttribute("size", "15");
                        metadataInput.setAttribute("class", "form-control")
                        $(metadataInput).change(function() {
                            var id = this.getAttribute("id");
                            var metaArg = id.substring(0, id.indexOf('-'));
                            var index = id.substring(id.indexOf('-') + 1);
                            var valor = this.value;
                            console.debug(lista[index][metaArg]);
                            lista[index][metaArg] = valor;
                        });

                        var typographyInput = document.createElement("INPUT");
                        typographyInput.setAttribute("id", "typography-" + i);
                        typographyInput.setAttribute("size", "6");
                        //var typoArg = "typography";
                        //var index = i;
                        $(typographyInput).change(function() {
                            var id = this.getAttribute("id");
                            var typoArg = id.substring(0, id.indexOf('-'));
                            var index = id.substring(id.indexOf('-') + 1);
                            var valor = this.value;
                            console.debug(lista[index][typoArg]);
                            lista[index][typoArg] = valor;
                        });

                        var psmSelect = document.createElement("SELECT");
                        psmSelect.setAttribute("id", "psm-" + i);
                        psmSelect.setAttribute("class", "form-control");
                        //var psmArg = "psm";
                        //var index = i;
                        $(psmSelect).change(function() {
                            var id = this.getAttribute("id");
                            var psmArg = id.substring(0, id.indexOf('-'));
                            var index = id.substring(id.indexOf('-') + 1);
                            var valor = this.options[this.selectedIndex].value;
                            console.debug(lista[index][psmArg]);
                            lista[index][psmArg] = valor;

                        });

                        var removeButton = document.createElement("BUTTON");
                        removeButton.setAttribute("id", "btn-remove-" + i);
                        removeButton.setAttribute("class", "btn btn-warning");
                        removeButton.innerHTML = "X";
                        //var removeArg = "btn-remove";
                        //var index = i;
                        $(removeButton).click(function() {
                            var id = this.getAttribute("id");
                            var index = id.substring(id.lastIndexOf('-') + 1);
                            lista.splice(index, 1);
                            //lista[index][psmArg] = valor;
                            listar(lista);
                        });

                        fillLanguage(languageSelect);
                        fillPsm(psmSelect);
                        xTD.innerHTML = lista[i].x;
                        yTD.innerHTML = lista[i].y;
                        widthTD.innerHTML = lista[i].width;
                        heightTD.innerHTML = lista[i].height;
                        metadataInput.value = lista[i].metadata;
                        $(languageSelect).val(lista[i].language);
                        //typographyInput.setAttribute("value", lista[i].typography);
                        $(psmSelect).val(lista[i].psm);

                        metadataTD.appendChild(metadataInput)
                        //languageTD.innerHTML += "<!--[if !IE]> --> <div class='notIE'> <!-- <![endif]--><label />";
                        languageTD.appendChild(languageSelect);
                        //languageTD.innerHTML += "<!--[if !IE]> --></div> <!-- <![endif]-->";
                        //typographyTD.appendChild(typographyInput);
                        //psmTD.innerHTML += "<!--[if !IE]> --> <div class='notIE'> <!-- <![endif]--><label />";
                        psmTD.appendChild(psmSelect);
                        //psmTD.innerHTML += "<!--[if !IE]> --></div> <!-- <![endif]-->";
                        removeTD.appendChild(removeButton);

                        rectTR.appendChild(xTD);
                        rectTR.appendChild(yTD);
                        rectTR.appendChild(widthTD);
                        rectTR.appendChild(heightTD);
                        rectTR.appendChild(metadataTD);
                        rectTR.appendChild(languageTD);
                        //rectTR.appendChild(typographyTD);
                        rectTR.appendChild(psmTD);
                        rectTR.appendChild(removeTD);

                        tbody.appendChild(rectTR);
                    }
                    listaDiv.appendChild(tbody);
                    divLista.appendChild(listaDiv);
                }

                function fillPsm(psmSelect) {
                    /*
                     * Set Tesseract to only run a subset of layout
                     * layout analysis and assume a certain form of image
                     * ----------------------------------------------------------
                     * 0 = Orientation and script detection (OSD) only.
                     * 1 = Automatic page segmentation with OSD.
                     * 2 = Automatic page segmentation, but no OSD, or OCR.
                     * 3 = Fully automatic page segmentation, but no OSD. (Default)
                     * 4 = Assume a single column of text of variable sizes.
                     * 5 = Assume a single uniform block of vertically aligned text.
                     * 6 = Assume a single uniform block of text.
                     * 7 = Treat the image as a single text line.
                     * 8 = Treat the image as a single word.
                     * 9 = Treat the image as a single word in a circle.
                     * 10 = Treat the image as a single character.
                     */

                    var psm = [ {
                        code : 0,
                        name : "Orientation and script detection (OSD) only"
                    }, {
                        code : 1,
                        name : "Automatic page segmentation with OSD"
                    }, {
                        code : 2,
                        name : "Automatic page segmentation, but no OSD, or OCR"
                    }, {
                        code : 3,
                        name : "Fully automatic page segmentation, but no OSD. (Default)"
                    }, {
                        code : 4,
                        name : "Assume a single column of text of variable sizes"
                    }, {
                        code : 5,
                        name : "Assume a single uniform block of vertically aligned text"
                    }, {
                        code : 6,
                        name : "Assume a single uniform block of text"
                    }, {
                        code : 7,
                        name : "Treat the image as a single text line"
                    }, {
                        code : 8,
                        name : "Treat the image as a single word"
                    }, {
                        code : 9,
                        name : "Treat the image as a single word in a circle"
                    }, {
                        code : 10,
                        name : "Treat the image as a single character"
                    }, ];

                    for (var i = 0; i < psm.length; i++) {
                        var option = document.createElement("OPTION");
                        option.setAttribute("value", psm[i].code);
                        if(psm[i].code == 3){
                            option.setAttribute("selected", "selected");
                        }
                        option.innerHTML = psm[i].name;
                        psmSelect.appendChild(option);
                    }
                }

                function fillLanguage(languageSelect) {
                    /*
                    Available languages for tesseract, you should install in tesseract all
                    you are planning to use.
                     * --------------------------------------------------------------------
                    ara (Arabic), aze (Azerbauijani), bul (Bulgarian), cat (Catalan), ces
                    (Czech), chi_sim (Simplified Chinese), chi_tra (Traditional Chinese),
                    chr (Cherokee), dan (Danish), dan-frak (Danish (Fraktur)), deu
                    (German), ell (Greek), eng (English), enm (Old English), epo
                    (Esperanto), est (Estonian), fin (Finnish), fra (French), frm (Old
                    French), glg (Galician), heb (Hebrew), hin (Hindi), hrv (Croation), hun
                    (Hungarian), ind (Indonesian), ita (Italian), jpn (Japanese), kor
                    (Korean), lav (Latvian), lit (Lithuanian), nld (Dutch), nor
                    (Norwegian), pol (Polish), por (Portuguese), ron (Romanian), rus
                    (Russian), slk (Slovakian), slv (Slovenian), sqi (Albanian), spa
                    (Spanish), srp (Serbian), swe (Swedish), tam (Tamil), tel (Telugu), tgl
                    (Tagalog), tha (Thai), tur (Turkish), ukr (Ukrainian), vie (Vietnamese)
                     */
                    var languages = [ {
                        code : "ara",
                        name : "Arabic"
                    }, {
                        code : "aze",
                        name : "Azerbauijani"
                    }, {
                        code : "bul",
                        name : "Bulgarian"
                    }, {
                        code : "cat",
                        name : "Catalan"
                    }, {
                        code : "ces",
                        name : "Czech"
                    },{
                        code : "chi_sim",
                        name : "Chinese"
                    },
                    /*
                    {
                        code : "chi_sim",
                        name : "Simplified Chinese"
                    },*/
                    /*{
                        code : "chi_tra",
                        name : "Traditional Chinese"
                    },*/
                    {
                        code : "chr",
                        name : "Cherokee"
                    }, {
                        code : "dan",
                        name : "Danish"
                    }, {
                        code : "dan-frak",
                        name : "Danish Fraktur"
                    }, {
                        code : "deu",
                        name : "German"
                    }, {
                        code : "ell",
                        name : "Greek"
                    }, {
                        code : "eng",
                        name : "English"
                    }, {
                        code : "enm",
                        name : "Old English"
                    }, {
                        code : "epo",
                        name : "Esperanto"
                    }, {
                        code : "est",
                        name : "Estonian"
                    }, {
                        code : "fin",
                        name : "Finnish"
                    }, {
                        code : "fra",
                        name : "French"
                    }, {
                        code : "frm",
                        name : "Old French"
                    }, {
                        code : "glg",
                        name : "Galician"
                    }, {
                        code : "heb",
                        name : "Hebrew"
                    }, {
                        code : "hin",
                        name : "Hindi"
                    }, {
                        code : "hrv",
                        name : "Croation"
                    }, {
                        code : "hun",
                        name : "Hungarian"
                    }, {
                        code : "ind",
                        name : "Indonesian"
                    }, {
                        code : "ita",
                        name : "Italian"
                    }, {
                        code : "jpn",
                        name : "Japanese"
                    }, {
                        code : "kor",
                        name : "Korean"
                    }, {
                        code : "lav",
                        name : "Latvian"
                    }, {
                        code : "lit",
                        name : "Lithuanian"
                    }, {
                        code : "nld",
                        name : "Dutch"
                    }, {
                        code : "nor",
                        name : "Norwegian"
                    }, {
                        code : "pol",
                        name : "Polish"
                    }, {
                        code : "por",
                        name : "Portuguese"
                    }, {
                        code : "ron",
                        name : "Romanian"
                    }, {
                        code : "rus",
                        name : "Russian"
                    }, {
                        code : "slk",
                        name : "Slovakian"
                    }, {
                        code : "slv",
                        name : "Slovenian"
                    }, {
                        code : "sqi",
                        name : "Albanian"
                    }, {
                        code : "spa",
                        name : "Spanish"
                    }, {
                        code : "srp",
                        name : "Serbian"
                    }, {
                        code : "swe",
                        name : "Swedish"
                    }, {
                        code : "tam",
                        name : "Tamil"
                    }, {
                        code : "tel",
                        name : "Telugu"
                    }, {
                        code : "tgl",
                        name : "Tagalog"
                    }, {
                        code : "tha",
                        name : "Thai"
                    }, {
                        code : "tur",
                        name : "Turkish"
                    }, {
                        code : "ukr",
                        name : "Ukrainian"
                    }, {
                        code : "vie",
                        name : "Vietnamese"
                    } ];

                    for (var i = 0; i < languages.length; i++) {
                        var option = document.createElement("OPTION");
                        option.setAttribute("value", languages[i].code);
                        if(languages[i].code == "spa"){
                            option.setAttribute("selected", "selected");
                        }
                        option.innerHTML = languages[i].name;
                        languageSelect.appendChild(option);
                    }

                }

                var lista = [];
                var json = {};
                var pic_real_width, pic_real_height;
                $(document).ready(function() {

                    $("#loadImageButton").click(function(event) {
                        var url = $("#img-url").val();
                        $('#photo').attr("src", url);

                        // Make in memory copy of image to avoid css issues
                        $("<img/>").attr("id", "preview").attr("src", url).load(function() {
                            pic_real_width = this.width; // Note: $(this).width() will not
                            pic_real_height = this.height; // work for in memory images.
                            console.info("pic_real_width: " + pic_real_width);
                            console.info("pic_real_height: " + pic_real_height);
                        });

                        $('#photo').imgAreaSelect({
                            x1 : 0,
                            y1 : 0,
                            x2 : 300,
                            y2 : 200,
                            handles : true,
                            fadeSpeed : 200,
                            onSelectChange : preview,
                            onSelectEnd : preview
                        });
                    });

                    $('#btnAgregar').click(function(event) {
                        var rect = {
                            x : x1.value,
                            y : y1.value,
                            width : width.value,
                            height : height.value,
                            metadata : "",
                            language : "spa",
                            typography : "",
                            psm : 3,
                        };
                        lista.push(rect);
                        listar(lista);
                        event.preventDefault();
                    });

                    $('#btnGenerar').click(function(event) {
                        var ocrZones = "";

                        ocrZones = JSON.stringify(lista);
                        var type = document.getElementById("type").value;
                        var result = "{\"type\":\""+ type + "\",\n\"ocr-zones\":" + ocrZones + "}";
                        document.getElementById("copyCode").value = result;
                        document.getElementById("profile").value = result;
                        document.getElementById("copyCode").className = "form-control";
                        event.preventDefault();
                        document.getElementById("btnDescargar").removeAttribute("disabled");
                    });
                });



                //implement JSON.stringify serialization
                JSON.stringify = JSON.stringify || function(obj) {
                    var t = typeof (obj);
                    if (t != "object" || obj === null) {
                        // simple data type
                        if (t == "string")
                            obj = '"' + obj + '"';
                        return String(obj);
                    } else {
                        // recurse array or object
                        var n, v, json = [], arr = (obj && obj.constructor == Array);
                        for (n in obj) {
                            v = obj[n];
                            t = typeof (v);
                            if (t == "string")
                                v = '"' + v + '"';
                            else if (t == "object" && v !== null)
                                v = JSON.stringify(v);
                            json.push((arr ? "" : '"' + n + '":') + String(v));
                        }
                        return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
                    }
                };


            },

            _onSuccessCallback: function ocrProfilerWidget__onSuccessCallback(response, config) {
                /*if (response.totalResults != undefined && response.totalResults > 0) {
                    var parentNode = this.containerNode;
                    array.forEach( response.items, function(item) {
                        var row = domConstruct.create( "tr", {}, parentNode );
                        domConstruct.create( "td", { innerHTML: item.name }, row);
                        domConstruct.create( "td", { innerHTML: item.description }, row);
                    });
                }*/
            }
        });
});