var logined=false;

function test(file_name){
	var result =/\.[^\.]+/.exec(file_name);
	return result;
	var fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
}


String.prototype.fileExt=function(){     
	return this.substring(this.lastIndexOf('.') + 1);    
}


String.prototype.startWith=function(str){     
  var reg=new RegExp("^"+str);     
  return reg.test(this);        
}  

String.prototype.endWith=function(str){     
  var reg=new RegExp(str+"$");     
  return reg.test(this);        
}

function deleteFile(baseId,field_name){
	var a1=$('#_1'+baseId);
	var ahref=a1.attr('href');
	a1.remove();
	$('#_2'+baseId).remove();
	var val=$('#'+field_name).val();
	var vals=val.split(',');
	var newVals=[];
	for(var i=0;i<vals.length;i++){
		var v=vals[i];
		if(v!=ahref){
			newVals.push(v);
		}
	}
	$('#'+field_name).val(newVals.join(','));
}

function uploadFile(field_name,ext,count){
	if (typeof (ext) == "undefined") {
		ext = "*";
	}
	if (typeof (count) == "undefined") {
		count = 1;
	}
	var url="public/sb_admin2/bower_components/qiniu/index.jsp?count="+count+"&ext="+ext;
	art.dialog.open(url, {
		title : '上传文件',
		width : 800,
		height : 400,
		okVal : "确定",
		ok : function() {
			var iframe = this.iframe.contentWindow;
			var names=iframe.document.getElementsByName('uploaded_file');
			var info=[];
			var urls=[];
			for(var i=0;i<names.length;i++){
				var o={};
				var name=names[i];
				o.url=name.attributes["href"].value;
				o.filename=name.attributes["data-name"].value; 
				info.push(o);
				urls.push(o.url);
			}
			
			var upload_result_tpl=document.getElementById('upload_result_tpl').innerHTML;
        	var content=template(upload_result_tpl, {info:info,field_name:field_name});

			
			$('#_'+field_name).html(content);
			$('#'+field_name).val(urls.join(','));
			qiniuUploadSuccess();
			return true;
		}
	});
}

function qiniuUploadSuccess() {
	
}

window.isJson = function(obj){
    var isjson = typeof(obj) == "object" && Object.prototype.toString.call(obj).toLowerCase() == "[object object]" && !obj.length
    return isjson;
}

window.jinhuayun = {
	access_token:'',
	config : function(obj) {
		obj.state="code";
		$.ajax({
			type : 'POST',
			url : 'oauth-access_token',
			data : obj,
			async : false,
			dataType : 'json',
			success : function(data) {
				var result = "当前系统繁忙";
				result = data.rs;
				var exdate=new Date();
				if (result == 'Y') {
					var at = data.access_token;
					store.set('jh_access_token', at);
					jinhuayun.jh_access_token=at;
					exdate.setDate(exdate.getDate() + 7);
					document.cookie='jh_access_token=' + escape(at) + ';expires='+exdate.toGMTString()+';path=/';
// $.cookie('jh_access_token', at, { expires: 7, path: '/' });
				} else {
					jinhuayun.jh_access_token='';
					document.cookie='jh_access_token=;expires='+exdate.toGMTString()+';path=/';
					store.set('jh_access_token', '');
// $.cookie('jh_access_token', 'n/a', { expires: 7, path: '/' });
					alert(result);
				}
			},
			error : function(xhr, type) {
				jinhuayun.jh_access_token='';
				alert("请联系管理员处理");
			}
		});
	},

	login : function(name, pwd,usrClz,success,failed) {
		var at = jinhuayun.jh_access_token;
		$.ajax({
			type : 'POST',
			url : 'oauth-by-name-pwd',
			async : true,// 异步
			data:{
				name:name,
				pwd:pwd,
				usrClz:usrClz,
				jh_access_token:at
			},
			dataType : 'json',
			success : function(data) {
				var result = "当前系统繁忙";
				result = data.rs;
				if (result == 'Y') {
					logined=true;
					if (success && typeof success === "function") {
						success(data);
					}
				} else {
					logined=false;
					if (failed && typeof failed === "function") {
						failed(data);
					}
				}

			},
			error : function(xhr, type) {
				logined=false;
				if (failed && typeof failed === "function") {
					failed({rs:'系统请求出错'});
				}
			}
		});
	},
	loginWithOpenId : function(openId,success,failed) {
		var at = store.get('jh_access_token');
		$.ajax({
			type : 'POST',
			url : 'oauth-by-openid',
			data:{
				at:at,
				openId:openId
			},
			async : true,// 异步
			dataType : 'json',
			success : function(data) {
				var result = "当前系统繁忙";
				result = data.rs;
				if (result == 'Y') {
					logined=true;
					if (success && typeof success === "function") {
						success(data);
					}
				} else {
					logined=false;
					if (failed && typeof failed === "function") {
						failed(data);
					}
				}

			},
			error : function(xhr, type) {
				logined=false;
				if (failed && typeof failed === "function") {
					failed({rs:'系统请求出错'});
				}
			}
		});
	},
	sendMq:function(tag,key,body,timeStr){
		if(timeStr==null||timeStr.length!=10){
			timeStr="";
		}
		if(logined){
			$.ajax({
				type : 'POST',
				url : 'mq-send',
				async : true,
				data:{
					tag:tag,
					key:key,
					body:body,
					time:timeStr,
					jh_access_token:jinhuayun.jh_access_token
				},
				dataType : 'json',
				success : function(data) {
					var result = data.rs;
					if (result == 'Y') {
					}else{
						alert(result);
					}
				},
				error : function(xhr, type) {
					alert('发送消息失败');
				}
			});	
		}else{
			alert("当前用户没有登录，不能发送消息");
		}
	},
	processStart:function(name){
		if (typeof (name) == "undefined"){
			alert("工作流的name不能为空")
		}
		
		if(typeof(jh_process_realod)=='undefined'){
			jh_process_realod=false;
		}
		
		$.ajax({
			type : 'GET',
			url : 'process-start-json',
			async : true,
			data:{
				reload:jh_process_realod,
				process_name:name,
				jh_access_token:jinhuayun.jh_access_token
			},
			dataType : 'json',
			success : function(data) {
				var result = data.rs;
				if (result == 'Y') {
			//		console.log(JSON.stringify(data));
					var pageTpl=document.getElementById('jhProcessTpl').innerHTML;
					var html=template(pageTpl, {task:data.task});
					$('#'+name+'_jh_process_page').html(html);
					$.parser.parse('#'+name+'_jh_process_page');
				}else{
					alert(result);
				}
			},
			error : function(xhr, type) {
				alert('启动系统工作流【'+name+'】失败');
			}
		});	
	}
	
	
	
};


/**
 * 本地存储工具类，提供本地数据的存储、获取、删除和清理本地存储等功能
 */
window.store = {
	get : function(key) {// 根据指定的key获取本地存储中的字符串内容
		return window.localStorage.getItem(key);
	},
	getJson : function(key) {// 根据指定的key从本地获取对应的JS对象
		var content = this.get(key);
		if (content) {// 将字符串内容转换为JS对象
			if (content == 'no') {
				return content;
			} else {
				return JSON.parse(content);
			}
		} else {
			return null;
		}
	},
	set : function(key, value) {// 将指定的内容保存到本地存储中
		if (typeof value === "object") {// 将JS对象转换为JSON字符串
			window.localStorage.setItem(key, JSON.stringify(value));
		} else {
			window.localStorage.setItem(key, value);
		}
		return this;
	},
	del : function(key) {// 删除本地存储中的内容
		window.localStorage.removeItem(key);
		return this;
	},
	clear : function() {// 清理本地存储中的所有内容
		return window.localStorage.clear();
	}
};

