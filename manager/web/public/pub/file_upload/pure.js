//common variables                                                                                                           

var iBytesUploaded = 0;

var iBytesTotal = 0;

var iPreviousBytesLoaded = 0;

var iMaxFilesize = 1048576; // 1MB

var oTimer = 0;

var sResultFileSize = '';

function secondsToTime(secs) { // we will use this 000 function to convert
	// seconds in normal time format

	var hr = Math.floor(secs / 3600);

	var min = Math.floor((secs - (hr * 3600)) / 60);

	var sec = Math.floor(secs - (hr * 3600) - (min * 60));

	if (hr < 10) {
		hr = "0" + hr;
	}

	if (min < 10) {
		min = "0" + min;
	}

	if (sec < 10) {
		sec = "0" + sec;
	}

	if (hr) {
		hr = "00";
	}

	return hr + ':' + min + ':' + sec;

};

function bytesToSize(bytes) {

	var sizes = [ 'Bytes', 'KB', 'MB' ];

	if (bytes == 0)
		return 'n/a';

	var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));

	return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i];

};

function fileSelected() {

	// hide different warnings


	document.getElementById('error').style.display = 'none';

	document.getElementById('error2').style.display = 'none';

	document.getElementById('abort').style.display = 'none';

	document.getElementById('warnsize').style.display = 'none';

	// get selected file element

	var oFile = document.getElementById('image_file').files[0];

	// filter for image files

	var rFilter = /^(image\/bmp|image\/gif|image\/jpeg|image\/png|image\/tiff)$/i;

	if (!rFilter.test(oFile.type)) {

		document.getElementById('error').style.display = 'block';

		return;

	}

	// little test for filesize

	if (oFile.size > iMaxFilesize) {

		document.getElementById('warnsize').style.display = 'block';

		return;

	}

	// get preview element


	// prepare HTML5 FileReader

	var oReader = new FileReader();

	oReader.readAsDataURL(oFile);

}

function startUploading(sid,number) {
	iPreviousBytesLoaded = 0;
	document.getElementById('error').style.display = 'none';
	document.getElementById('error2').style.display = 'none';
	document.getElementById('abort').style.display = 'none';
	document.getElementById('warnsize').style.display = 'none';
	document.getElementById('progress_percent').innerHTML = '';
	var vFD = new FormData(document.getElementById('upload_form'));
	var oXHR = new XMLHttpRequest();
	oXHR.upload.addEventListener('progress', uploadProgress, false);
	oXHR.addEventListener('load', uploadFinish, false);
	oXHR.addEventListener('error', uploadError, false);
	oXHR.addEventListener('abort', uploadAbort, false);
	oXHR.open('POST', 'public/pub/file_upload/upload.jsp?sId='+sid+'&number='+number);
	oXHR.send(vFD);
	oTimer = setInterval(doInnerUpdates, 300);
}

function doInnerUpdates() { // we will use this 000 function to display upload
	var iCB = iBytesUploaded;
	var iDiff = iCB - iPreviousBytesLoaded;
	if (iDiff == 0)
		return;
	iPreviousBytesLoaded = iCB;
	iDiff = iDiff * 2;
	var iBytesRem = iBytesTotal - iPreviousBytesLoaded;
	var secondsRemaining = iBytesRem / iDiff;
	var iSpeed = iDiff.toString() + 'B/s';
	if (iDiff > 1024 * 1024) {
		iSpeed = (Math.round(iDiff * 100 / (1024 * 1024)) / 100).toString()
				+ 'MB/s';

	} else if (iDiff > 1024) {

		iSpeed = (Math.round(iDiff * 100 / 1024) / 100).toString() + 'KB/s';

	}

	document.getElementById('speed').innerHTML = iSpeed;

	document.getElementById('remaining').innerHTML = '| '
			+ secondsToTime(secondsRemaining);

}

function uploadProgress(e) { // upload process in progress

	if (e.lengthComputable) {

		iBytesUploaded = e.loaded;

		iBytesTotal = e.total;

		var iPercentComplete = Math.round(e.loaded * 100 / e.total);

		var iBytesTransfered = bytesToSize(iBytesUploaded);

		document.getElementById('progress_percent').innerHTML = iPercentComplete
				.toString()
				+ '%';

		document.getElementById('progress').style.width = (iPercentComplete * 4)
				.toString()
				+ 'px';

		document.getElementById('b_transfered').innerHTML = iBytesTransfered;

	} else {

		document.getElementById('progress').innerHTML = 'unable to compute';

	}

}

function uploadFinish(e) { // upload successfully finished

	document.getElementById('progress_percent').innerHTML = '100%';

	document.getElementById('progress').style.width = '400px';

	document.getElementById('filesize').innerHTML = sResultFileSize;

	document.getElementById('remaining').innerHTML = '| 00:00:00';

	clearInterval(oTimer);
	document.getElementById("sys_file_upload___sys_file_refresh_toolbar").click();
}

function uploadError(e) { // upload error

	document.getElementById('error2').style.display = 'block';

	clearInterval(oTimer);

}

function uploadAbort(e) { // upload abort

	document.getElementById('abort').style.display = 'block';

	clearInterval(oTimer);

}