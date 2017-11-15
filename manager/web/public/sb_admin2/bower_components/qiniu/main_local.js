/*global Qiniu */
/*global plupload */
/*global FileProgress */
/*global hljs */

//"jpg,jpeg,gif,png"

function GetRequest() {   
	   var url = location.search; //获取url中"?"符后的字串   
	   var theRequest = new Object();   
	   if (url.indexOf("?") != -1) {   
	      var str = url.substr(1);   
	      strs = str.split("&");   
	      for(var i = 0; i < strs.length; i ++) {   
	         theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);   
	      }   
	   }   
	   return theRequest;   
	}   
	
var getHost = function() {
	var host =  location.protocol+"//"+location.hostname+contextPath+"/";
	if(location.port!=80){
		host =  location.protocol+"//"+location.hostname+":"+location.port+contextPath+"/";
	}
    return host;
}
var ext="*";
var upload_key="";

var params=GetRequest();
	if(params.ext!=null&&params.ext.length>0){
		ext=params.ext;
	}
$(function() {
	var uploader = Qiniu
			.uploader({
				runtimes : 'html5',
				browse_button : 'pickfiles',
				container : 'container',
				drop_element : 'container',
				max_file_size : '1000mb',
				dragdrop : true,
				unique_names:false,
				chunk_size : '4mb',
				filters: {
				      mime_types : [
				        {title : "Files types", extensions:ext }
				      ]
				    },
				multi_selection : !(mOxie.Env.OS.toLowerCase() === "ios"),
				// uptoken_url : 'upload-qiniu-uptoken',
				uptoken_func : function() {
					var ajax = new XMLHttpRequest();
					ajax.open('GET', 'upload-qiniu-uptoken', false);
					ajax.setRequestHeader("If-Modified-Since", "0");
					ajax.send();
					if (ajax.status === 200) {
						var res = JSON.parse(ajax.responseText);
						console.log('custom uptoken_func:' + res.uptoken);
						upload_key=res.key;
						return res.uptoken;
					} else {
						console.log('custom uptoken_func err');
						return '';
					}
				},
				domain : ''+getHost(),
				get_new_uptoken : false,
				auto_start : true,
				log_level : 2,
				init : {
					'FilesAdded' : function(up, files) {
						var limitedSize=$('#uploaded_file_count_limited').val();
						for(var i=0;i<files.length;i++){
							var curSize=$('.progressContainer').size()+files.length;
							if(curSize>limitedSize){
								$('.progressContainer').first().remove();
							}
						}
						$('table').show();
						$('#success').hide();
						plupload.each(files, function(file) {
							var progress = new FileProgress(file,
									'fsUploadProgress');
							progress.setStatus("等待...");
							progress.bindUploadCancel(up);
						});
					},
					'BeforeUpload' : function(up, file) {
						var progress = new FileProgress(file,
								'fsUploadProgress');
						var chunk_size = plupload.parseSize(this
								.getOption('chunk_size'));
						if (up.runtime === 'html5' && chunk_size) {
							progress.setChunkProgess(chunk_size);
						}
					},
					'UploadProgress' : function(up, file) {
						var progress = new FileProgress(file,
								'fsUploadProgress');
						var chunk_size = plupload.parseSize(this
								.getOption('chunk_size'));
						progress.setProgress(file.percent + "%", file.speed,
								chunk_size);
					},
					'UploadComplete' : function() {
						$('#success').show();
					},
					'FileUploaded' : function(up, file, info) {
						var progress = new FileProgress(file,
								'fsUploadProgress');
						progress.setComplete(up, info);
					},
					'Error' : function(up, err, errTip) {
						$('table').show();
						var progress = new FileProgress(err.file,
								'fsUploadProgress');
						progress.setError();
						progress.setStatus(errTip);
					},
					'Key' : function(up, file) {
						 //当save_key和unique_names设为false时，该方法将被调用
						/*
			            $.ajax({
			                url: '/qiniu-token/get-key/',
			                type: 'GET',
			                async: false,//这里应设置为同步的方式
			                success: function(data) {
			                    var ext = Qiniu.getFileExtension(file.name);
			                    key = data + '.' + ext;
			                },
			                cache: false
			            });
			            */
			            return file.id+"_._"+file.name;
					}
				}
			});

	uploader.bind('FileUploaded', function() {
		console.log('hello man,a file is uploaded');
	});
	$('#container').on('dragenter', function(e) {
		e.preventDefault();
		$('#container').addClass('draging');
		e.stopPropagation();
	}).on('drop', function(e) {
		e.preventDefault();
		$('#container').removeClass('draging');
		e.stopPropagation();
	}).on('dragleave', function(e) {
		e.preventDefault();
		$('#container').removeClass('draging');
		e.stopPropagation();
	}).on('dragover', function(e) {
		e.preventDefault();
		$('#container').addClass('draging');
		e.stopPropagation();
	});

	$('#show_code').on('click', function() {
		$('#myModal-code').modal();
		$('pre code').each(function(i, e) {
			hljs.highlightBlock(e);
		});
	});

	$('body').on('click', 'table button.btn', function() {
		$(this).parents('tr').next().toggle();
	});

	var getRotate = function(url) {
		if (!url) {
			return 0;
		}
		var arr = url.split('/');
		for (var i = 0, len = arr.length; i < len; i++) {
			if (arr[i] === 'rotate') {
				return parseInt(arr[i + 1], 10);
			}
		}
		return 0;
	};

	$('#myModal-img .modal-body-footer')
			.find('a')
			.on(
					'click',
					function() {
						var img = $('#myModal-img').find('.modal-body img');
						var key = img.data('key');
						var oldUrl = img.attr('src');
						var originHeight = parseInt(img.data('h'), 10);
						var fopArr = [];
						var rotate = getRotate(oldUrl);
						if (!$(this).hasClass('no-disable-click')) {
							$(this).addClass('disabled').siblings()
									.removeClass('disabled');
							if ($(this).data('imagemogr') !== 'no-rotate') {
								fopArr.push({
									'fop' : 'imageMogr2',
									'auto-orient' : true,
									'strip' : true,
									'rotate' : rotate,
									'format' : 'png'
								});
							}
						} else {
							$(this).siblings().removeClass('disabled');
							var imageMogr = $(this).data('imagemogr');
							if (imageMogr === 'left') {
								rotate = rotate - 90 < 0 ? rotate + 270
										: rotate - 90;
							} else if (imageMogr === 'right') {
								rotate = rotate + 90 > 360 ? rotate - 270
										: rotate + 90;
							}
							fopArr.push({
								'fop' : 'imageMogr2',
								'auto-orient' : true,
								'strip' : true,
								'rotate' : rotate,
								'format' : 'png'
							});
						}

						$('#myModal-img .modal-body-footer')
								.find('a.disabled')
								.each(
										function() {

											var watermark = $(this).data(
													'watermark');
											var imageView = $(this).data(
													'imageview');
											var imageMogr = $(this).data(
													'imagemogr');

											if (watermark) {
												fopArr
														.push({
															fop : 'watermark',
															mode : 1,
															image : 'http://www.b1.qiniudn.com/images/logo-2.png',
															dissolve : 100,
															gravity : watermark,
															dx : 100,
															dy : 100
														});
											}

											if (imageView) {
												var height;
												switch (imageView) {
												case 'large':
													height = originHeight;
													break;
												case 'middle':
													height = originHeight * 0.5;
													break;
												case 'small':
													height = originHeight * 0.1;
													break;
												default:
													height = originHeight;
													break;
												}
												fopArr.push({
													fop : 'imageView2',
													mode : 3,
													h : parseInt(height, 10),
													q : 100,
													format : 'png'
												});
											}

											if (imageMogr === 'no-rotate') {
												fopArr.push({
													'fop' : 'imageMogr2',
													'auto-orient' : true,
													'strip' : true,
													'rotate' : 0,
													'format' : 'png'
												});
											}
										});

						var newUrl = Qiniu.pipeline(fopArr, key);

						var newImg = new Image();
						img
								.attr(
										'src',
										'public/sb_admin2/bower_components/qiniu/images/loading.gif');
						newImg.onload = function() {
							img.attr('src', newUrl);
							img.parent('a').attr('href', newUrl);
						};
						newImg.src = newUrl;
						return false;
					});

});
