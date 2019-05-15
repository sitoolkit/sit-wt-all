$(function () {

  var typeMap = new Map();
  typeMap.set("evidence", "通常");
  typeMap.set("mask", "マスク版");
  typeMap.set("comp", "比較");
  typeMap.set("compmask", "比較（マスク版）")
  typeMap.set("compng", "スクリーンショット比較NG");

  var section = $("h2:contains(Test Cases)").closest(".section");

  var buildEvidenceLinks = function (data) {
    var links = "";
    typeMap.forEach(function (label, type) {
      let path = data[type];
      if (path.length > 0) {
        links += buildLinkTag(path, label);
      }
    })
    return links;
  };

  let buildLinkTag = function (href, label) {
    return " " + $("<a />").attr({
      href: href,
      target: "_blank",
    }).text("[ " + label + " ]")[0].outerHTML;
  };

  $("input.evidence").each(function () {
    let input = $(this);
    section.find("td > a[name='TC_" + input.data("name") + "']").each(function () {
      let td = $(this).parent();
      td.append(" ( エビデンス： " + buildEvidenceLinks(input.data()) + " )");
    });
  });

});