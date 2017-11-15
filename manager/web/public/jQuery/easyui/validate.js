$.extend($.fn.validatebox.defaults.rules, {
	long : {
		validator : function(value, param) {
			var reg = /[+\-]?\d+[.\d+]?$/ ;
			return reg.test(value);
		},
		message : '请输入整数'
	}
});

$.extend($.fn.validatebox.defaults.rules, {   
    myRequired: {   
        validator: function(value, param){
        	if(param[0]==1){
        		if(value=='请选择'||value.length<=0){
        			return false;
            	}else{
            		return true;
            	}
        	}else{
        		return true;
        	}
        },   
        message: '该输入项为必选项'  
    }   
});

$.extend($.fn.validatebox.defaults.rules, {
	float : {
		validator : function(value, param) {
			var reg = /^(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))$/;
			return reg.test(value);
		},
		message : '请输入数字,并保留{0}位小数'
	}
});
$.extend($.fn.validatebox.defaults.rules, {
	variable : {
		validator : function(value, param) {
			var reg = /^[a-zA-Z]{1}([a-zA-Z0-9]|[_])+$/;
			return reg.test(value);
		},
		message : '请输入字母_和数字,并以字母开头'
	}
});