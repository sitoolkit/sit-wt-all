/**
 *
 */

$(function() {

	var additionalLinks = function(input, td) {
		if (input.data("mask").length > 0) {
			td.append(" <a href='" + input.data("mask") + "'>[ マスク版 ]</a>");
		}
		if (input.data("comp").length > 0) {
			td.append(" <a href='" + input.data("comp") + "'>[ 比較 ]</a>"); // TODO メインブラウザとの比較、サブブラウザ同士の比較、区別
		}
		if (input.data("compmask").length > 0) {
			td.append(" <a href='" + input.data("compmask") + "'>[ 比較（マスク版） ]</a>");
		}
		if (input.data("compng").length > 0) {
			td.append(" <a href='" + input.data("compng") + "'>[ スクリーンショット比較NG ]</a>");
		}
		if (input.data("compngmask").length > 0) {
			td.append(" <a href='" + input.data("compngmask") + "'>[ スクリーンショット比較NG（マスク版） ]</a>");
		}
	};

	$("input:hidden").each(function() {
		var input = $(this);
		$("h3:contains('" + input.data("class") + "')").each(function() {
			$(this).siblings("table").find("td:contains('" + input.data("method") + "')").each(function() {
				var td = $(this);
				if (td.text() == input.data("method") || td.find("a:first").text() == input.data("method")) {
					td.append(" ( エビデンス： <a href='" + input.data("evidence") + "'>[ 通常 ]</a>");
					additionalLinks(input, td);
					td.append(" )");
				}
			});
		});
	});
});

