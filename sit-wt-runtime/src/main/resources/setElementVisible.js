var setElementVisible = function(element) {
	
	if (element.tagName == 'BODY') {
		return element;
	}
	
	if (element.style.display == 'none' || window.getComputedStyle(element).display == 'none') {
		
		element.style.display = 'inline';
		
		document.sitFuncRestoreElementVisibility = function() {

			element.style.display = 'none';
			if (typeof console.debug == 'function') {
				console.debug(element);
			}
			return element;

		};
		
		return element;
		
	} 

	return setElementVisible(element.parentNode);
		
};
return setElementVisible(arguments[0]);
