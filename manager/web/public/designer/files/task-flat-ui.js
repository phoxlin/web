
function createProcessComponent(name,json,fieldColumn,column_clz,column_style,val){
	//<input type="text" id="<%=hControlName%>" name="<%=hControlName%>" style="width:60%" class="form-control easyui-validatebox input-sm" data-options="required:true"  placeholder="<%=hControlTitle%>" value="<%=defaultVal%>">
	var content='';
	var tpl='';
	if(json.controlType=='easyui-combobox'){
		tpl=document.getElementById('processEasyui-comboboxTpl').innerHTML;
		if(val.length!=0){//查看代码 发现 每一次取的都是默认值,但是有时候存在值, 这里传入的值是val  替换掉就行 
			tpl=tpl.replace("var defaultVal=data.defaultValue;","var defaultVal="+val+";")
		}
	}else if(json.controlType=='niu-upload'){
		tpl=document.getElementById('msNiuUploadTpl').innerHTML;
	}else if(json.controlType=='old-upload'){
		tpl=document.getElementById('msOldUploadTpl').innerHTML;
	}else if(json.controlType=='password'){
		tpl=document.getElementById('msPasswordTpl').innerHTML;
	}else if(json.controlType=='easyui-numberspinner'){
		tpl=document.getElementById('msNumberSpinnerTpl').innerHTML;
	}else if(json.controlType=='editor'){
		tpl=document.getElementById('msEditorTpl').innerHTML;
		tpl = tpl.replace("&lt;","<");
		tpl = tpl.replace("&gt;",">");
	}else if(json.controlType=='textarea'){
		tpl=document.getElementById('msTextareaTpl').innerHTML;
	}else if(json.controlType=='easyui-timespinner'){
		tpl=document.getElementById('msEasyui-timespinnerTpl').innerHTML;
	}else if(json.controlType=='easyui-datebox'){
		tpl=document.getElementById('msEasyui-dateboxTpl').innerHTML;
	}else if(json.controlType=='easyui-datetimebox'){
		tpl=document.getElementById('msEasyui-datetimeboxTpl').innerHTML;
	}else if(json.controlType=='easyui-numberbox'){
		tpl=document.getElementById('msEasyui-numberboxTpl').innerHTML;
	}else{
		tpl=document.getElementById('processEasyui-validateboxTpl').innerHTML;
	}
	console.log(json)
	content=template(tpl, {data:json,name:name,fieldColumn:fieldColumn,column_clz:column_clz,column_style:column_style,val:val});
	return content;
}
