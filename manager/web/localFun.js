


function focusTakePhoto2(uid){

var path = require("path");  
//根目录，如app://movie_desktop  
//var rootArray = window.document.location.href.split("/");  
//var rootPath = rootArray[0] + "//" + rootArray[2];  
//数据存储地址  
//var dataPath = require('nw.gui').App.dataPath;  
//nw.exe运行地址  
var execPath = path.dirname(process.execPath);  
//应用地址  
var cwdPath = process.cwd();  
//console.log("rootArray:"+rootArray +",rootPath:"+rootPath +",dataPath:"+dataPath +",execPath:"+execPath+",cwdPath:"+cwdPath);
cwdPath = cwdPath.replace("/","\\");
const exec = require('child_process').exec;//产生exec，同时传入.bat文件
exec(execPath + '\\take_photo_model\\OperateCamera.exe', (err, stdout, stderr) => {
  if (stderr) {

    

  console.log(err.message);

  }
	
	if(stdout){
		var obj = JSON.parse(stdout);
		if(obj.result=='Y'){
				alert("拍照成功");
				var url=null;
				if(obj['url']!=null&&[obj['url'].length>0]){
					url=obj['url'];
				}else{
					url = "http://yeapao.com/" + obj.key;
				}
				uploadcallBack2(url,uid);
		}
	}	
  console.log(stdout);
});


}
	
	


  
