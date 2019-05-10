$(function() {
  let buildEvidenceLink = function (caseName) {
    let evidenceFile = "../" + caseName.replace(/^TC_/, "").replace(".", "_") + ".html";

    return $("<a />")
      .attr("href", evidenceFile)
      .attr("target", "_blank")
      .text("evidence");
  }

  $("h2:contains(Test Cases)").closest(".section").find("tr").each(function (i, trElm) {
    let tr = $(trElm);
    let td = $("<td />").appendTo(tr);

    let testCaseLink = tr.find("td:nth-child(2) > a");
    if (testCaseLink.length == 0) {
      return;
    }

    let caseName = testCaseLink.attr("name");
    buildEvidenceLink(caseName).appendTo(td);
  })
});
