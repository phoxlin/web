//进化工具包
window.jh = {
	ajax : function(url,succes,error,opt) {
		var type="GET";
		var dataType="json";
		var async=true;
		var authorization="";
		var data={};
		if (typeof (opt) != "undefined"){
			if(opt.type!=null){
				type=opt.type;
			}
			if(opt.dataType!=null){
				dataType=opt.dataType;
			}
			if(opt.async!=null){
				async=opt.async;
			}
			if(opt.data!=null){
				data=opt.data;
			}
		}
		//config.appid;
		//config.appsecret;
		
		//设置oauth2.0信息
		
		$.ajax({
			  type: type,
			  url: config.baseUrl+url,
			  dataType: dataType,
			  async: async,
			  headers: {
			    "Authorization": authorization
			  },
			  data: data,
			  success: function (data2){
				  if (sucess && typeof sucess === "function") {
					  sucess(data2);
				  }
			  },
			  error:function(){
				  if (error && typeof error === "function") {
					  error();
				  }
			  }
			});
	},
	// 跨域ajax get方法
	getUrl:function(url, sucess, error, data) {
		var t_url = config.baseUrl + url;
		var hasData=false;
		if (typeof (data) != "undefined"){
			hasData=true;
		}
		if(hasData){
			jQuery.getJSON(t_url,data, function(data2, status) {
				if ('success' == status) {
					if (sucess && typeof sucess === "function") {
						sucess(data2);
					}
				} else {
					if (error && typeof error === "function") {
						error();
					}
				}
			});
		}else{
			jQuery.getJSON(t_url, function(data2, status) {
				if ('success' == status) {
					if (sucess && typeof sucess === "function") {
						sucess(data2);
					}
				} else {
					if (error && typeof error === "function") {
						error();
					}
				}
			});
		}
	},
	//获取oath2.0的身份令牌号码
	getAccessToken:function(){
		//https://open.weixin.qq.com/connect/oauth2/authorize
		//?appid=wxfdd0fa18221c5872
		//&redirect_uri=http://www.ruifeng028.com/weixin.jsp
		//&response_type=code
		//&scope=snsapi_userinfo
		//&state=STATE#wechat_redirect
	},
	refreshAccessToken:function(){
		
	}
};