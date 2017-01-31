
$(function() {
	var rows = $("#right_evidence tbody").children().length;
	var logIdx = $("#right_evidence thead > tr").children().length - 1;


	for ( var i = 0; i < rows; i++) {

		var leftLog = $("#left_evidence tbody > tr").eq(i).children().eq(logIdx).text();
		var rightLog = $("#right_evidence tbody > tr").eq(i).children().eq(logIdx).text();

		var dateFormat = "[yyyy/MM/dd HH:mm:ss] ";
		leftLog = leftLog.substr(dateFormat.length - 1, leftLog.length);
		rightLog = rightLog.substr(dateFormat.length - 1, rightLog.length);

		if (leftLog != rightLog) {
			$("#right_evidence tbody > tr").eq(i).css("background-color", "pink");
			$("#left_evidence tbody > tr").eq(i).css("background-color", "pink");
		}
	}
});

function buildMenu(){

	var overlayHelp = "左右のスクリーンショットを重ねます。&#10;（キーボードショートカット：A）";
	var joinHelp = "スクリーンショットを左右隙間なく並べます。&#10;（キーボードショートカット：S）";
	var swapHelp = "手前に表示するスクリーンショットを切り替えます。&#10;（キーボードショートカット：D）";
	var returnHelp = "スクリーンショットの位置を元に戻します。&#10;（キーボードショートカット：F）";

	$("body").prepend("<div id='menu'></div>");
	$("#menu").after("<div id='toolBox'></div>");
	$("#toolBox").append("スクリーンショット操作<br/>");
	$("#toolBox").append("<span class='help' title='" + overlayHelp + "'><img class='icon' id='overlay' src='img/icon/ic_photo_library_black_36dp_1x.png'/><br/></span>");
	$("#toolBox").append("<span class='help' title='" + joinHelp + "'><img class='icon' id='join' src='img/icon/ic_compare_arrows_black_36dp_1x.png'/><br/></span>");
	$("#toolBox").append("<span class='help' title='" + swapHelp + "'><img class='icon' id='swap' src='img/icon/ic_cached_black_36dp_1x.png'/><br/></span>");
	$("#toolBox").append("<span class='help' title='" + returnHelp + "'><img class='icon' id='revert' src='img/icon/ic_keyboard_return_black_36dp_1x.png'/><br/></span>");

	$(".help").tooltip({
		position: { my: "left+5 center", at: "right bottom+5" }
	});
	
	$(".icon").css("cursor", "pointer").hover(
			function() {
				$(this).animate({ backgroundColor : "#D3D3D3" }, 200);
			},
			function() {
				$(this).animate({ backgroundColor : "transparent" }, 200);
			});

}

$(window).load(function() {

	buildMenu();

	init();

	$("#overlay").click(function() {
		overlay();
	});

	$("#join").click(function() {
		join();
	});

	$("#swap").click(function() {
		swap();
	});

	$("#revert").click(function() {
		revert();
	});
	
});

function init(){
	revert();
}

function shiftBackground(img) {
	img.css("opacity", "1");
	img.css("z-index", "0");
}

function shiftForeground(img) {
	img.css("opacity", "0.8");
	img.css("z-index", "1");
}

function overlay(){

	var leftImgArray = [];

	$("#left_evidence td.screenshot").each(function() {
		var img = $(this).find("img");
		shiftBackground(img);
		leftImgArray.push(img);
	});

	$("#right_evidence td.screenshot").each(function(i) {
		var img = $(this).find("img");
		shiftForeground(img);
		img.offset(leftImgArray[i].offset());
	});
}

function join(){

	var leftImgArray = [];

	$("#left_evidence td.screenshot").each(function() {
		var img = $(this).find("img");
		img.css("opacity", "1");
		leftImgArray.push(img);
	});

	$("#right_evidence td.screenshot").each(function(i) {
		var img = $(this).find("img");
		img.offset(leftImgArray[i].offset());
		img.offset({
			left : img.offset().left + leftImgArray[i].width()
		});
		img.css("opacity", "1");
	});	
}

function swap(){

	$("#left_evidence td.screenshot").each(function() {
		var img = $(this).find("img");
		if (img.css("z-index") == 1) {
			shiftBackground(img);
		} else {
			shiftForeground(img);
		}
	});

	$("#right_evidence td.screenshot").each(function() {
		var img = $(this).find("img");
		if (img.css("z-index") == 1) {
			shiftBackground(img);
		} else {
			shiftForeground(img);
		}
	});
}

function revert() {

	var leftImgArray = [];

	/**
	 * 右側のスクリーンショットの初期位置を取得する処理
	 */
	var getOriginPos = function(idx) {

		var originPos;

		$("#right_evidence td.screenshot img").each(function(i) {
			if (i == idx) {
				var img = $(this);
				var pos = img.css("position");
				img.css("position", "static");
				originPos = img.offset();
				img.css("position", pos);
				return false;
			}
		});

		return originPos;
	};

	$("#left_evidence td.screenshot").each(function() {
		var img = $(this).find("img");
		img.css("position", "relative");
		img.css("opacity", "1");
		leftImgArray.push(img);
	});

	$("#right_evidence td.screenshot").each(function(i) {
		var img = $(this).find("img");
		img.css("position", "absolute");
		img.css("z-index", "1");
		img.css("opacity", "1");
		img.css("cursor", "move");
		img.css("width", leftImgArray[i].width());
		img.draggable();
		img.offset(getOriginPos(i));

		var marginTop = 5; // style.cssと同じ定義
		var paddingTop = $(this).css("padding-top");
		$(this).css("padding-bottom", marginTop + parseInt(paddingTop) + img.height());
	});
}

$(function() {
	$(window).keydown(function(e){
		switch(e.which){
			case 65: // Key[A]
				overlay();
				break;
			case 83: // Key[S]
				join();
				break;
			case 68: // Key[D]
				swap();
				break;
			case 70: // Key[F]
				revert();
				break;
			case 37: // Key[←]
			case 38: // Key[↑]
			case 39: // Key[→]
			case 40: // Key[↓]
				e.preventDefault();
				$("#right_evidence td.screenshot img").each(function(i) {
					moveScreenshot(e, $(this));
				});
				break;
		}
	});
});

function moveScreenshot(e, img) {
	switch(e.which){
		case 37: // Key[←]
			img.css("left", "-=1px");
			break;
		case 38: // Key[↑]
			img.css("top", "-=1px");
			break;
		case 39: // Key[→]
			img.css("left", "+=1px");
			break;
		case 40: // Key[↓]
			img.css("top", "+=1px");
			break;
	}
}
