var sort1 = {
        column: "@{http://www.alfresco.org/model/content/1.0}name",
        ascending: true
    };


var def = {
        query: "PATH:\"/app:company_home/app:dictionary/cm:Scanners/*\"",
        language: "lucene",
        sort: [sort1]
    };

var nodes = search.query(def);
model.nodes = nodes;