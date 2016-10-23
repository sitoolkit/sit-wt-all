
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


$(function() {
    $(window).keydown(function(e){
    	$('#left_evidence td.screenshot').each(function() {

    		var img = $(this).find("img");

    		if (e.keyCode == 72) { // Key[H]

    			img.css('position', 'relative');
        		img.css('opacity', '1');
        		img.css('z-index', '0');

    		} else if (e.keyCode == 74) { // Key[J]

    			img.css('position', 'relative');
        		img.css('opacity', '1');
        		img.css('z-index', '0');

        	} else if (e.keyCode == 75) { // Key[K]

        		img.css('position', 'relative');
        		img.css('opacity', '0.8');
        		img.css('z-index', '1');

    		} else if (e.keyCode == 76) { // Key[L]

    			img.css('position', 'static');
    			img.css('opacity', '1');

    		} else if (e.keyCode == 78) { // Key[N]

    			img.css('position', 'static');
    			img.css('opacity', '1');

    		}
    	});

    	$('#right_evidence td.screenshot').each(function(i) {

    		var td = $(this);
    		var img = td.find("img");

    		moveScreenshot(e, img);

    		var height;
    		var leftPos;
    		var imgWidth;
    		$('#left_evidence td.screenshot').each(function(j) {
    			if (i == j) {
    				td.css('height', $(this).css('height'));
    				leftPos = $(this).find("img").offset();
    				imgWidth = $(this).find("img").width();
    			}
    		});

    		// style.cssと同じ定義
    		var margin_top = 5;

    		if (e.keyCode == 72) { // Key[H]

    			img.css('position', 'absolute');
    			img.css('left', leftPos.left);
    			img.css('top', leftPos.top - margin_top);
        		img.css('width', imgWidth);
        		img.css('opacity', '0.8');
        		img.css('z-index', '1');


    		} else if (e.keyCode == 74) { // Key[J]

        		img.css('opacity', '0.8');
        		img.css('z-index', '1');

    		} else if (e.keyCode == 75) { // Key[K]

        		img.css('opacity', '1');
        		img.css('z-index', '0');

    		} else if (e.keyCode == 76) { // Key[L]

    			img.css('position', 'static');
    			img.css('opacity', '1');

    		} else if (e.keyCode == 78) { // Key[N]

    			img.css('position', 'absolute');
    			img.css('left', leftPos.left + imgWidth);
    			img.css('top', leftPos.top - margin_top);
        		img.css('width', imgWidth);
    			img.css('opacity', '1');

    		}
    	});

    });

});


function moveScreenshot(e, img) {
    switch(e.which){
		case 39: // Key[→]
		img.css('left', '+=1px');
		break;

		case 37: // Key[←]
		img.css('left', '-=1px');
		break;

		case 38: // Key[↑]
		img.css('top', '-=1px');
		break;

		case 40: // Key[↓]
		img.css('top', '+=1px');
		break;
    }
}
