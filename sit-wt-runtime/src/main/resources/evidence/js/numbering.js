/**
 * 枠の位置、大きさのスタイルを構築します。
 * @param {type} basePos 操作ログページでのスクリーンショットの位置
 * @param {type} pos 実行時ページでの項目の位置
 * @returns {String} 表示する枠のスタイル
 */
function buildPosStyle(basePos, pos, scale) {
	return "left:" + (basePos.left + pos.x * scale - 10) + "px;"
		+ "top:" + (basePos.top + pos.y * scale - 10) + "px;"
		+ "width:" + (pos.w + 20) + "px;"
		+ "height:" + (pos.h + 20) + "px;";
}

function buildBox() {
	var pos2Key = function (pos) {
		return pos.x + "_" + pos.y;
	};

	$("td.screenshot").each(function() {
		var $td = $(this);
		var $img = $td.find("img");
		var scale = $img.width() / $img.data("original-width");
		var basePos = $img.offset();

		$td.find("input:hidden").each(function() {
			var val = $(this).val();
			var pos = $.parseJSON(val);

			var posKey = pos2Key(pos);
			var posBox = $td.find("[data-pos-key='" + posKey + "']");

			if (posBox[0] !== undefined) {
				posBox.append("," + pos.no);
				return;
			}

			var style = buildPosStyle(basePos, pos, scale);
			$td.prepend("<div data-pos-key='" + posKey + "' class='box' style=" + style + ">" + pos.no + "</div>");
		});
	});

	$("div.box").click(function() {
		$(this).toggleClass("box-hidden");
	});
}

$(window).load(function() {
	buildBox();
});

$(window).resize(function() {
	$("div.box").remove();
	buildBox();
});
