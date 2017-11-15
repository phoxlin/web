function sys_task_todo___sys_task_todo_legendHook(){
	var rows = document.getElementsByName("sys_task_todo___sys_task_todo_legend_row");
	for (var i = 0; i < rows.length; i++) {
		var taskcode=$('#sys_task_todo___sys_task_todo_legend__taskcode_'+i).val();
		var id=$('#sys_task_todo___sys_task_todo_legend__id_'+i).val();
		var html="<a onclick='processTask(\""+id+"\",\""+taskcode+"\");'>处理</a>";
		$('#sys_task_todo___sys_task_todo_legend__op_html_'+i).find('div').html(html);
		
	}
}

function addNewTask(){
	art.dialog.open("public/pub/sys_task_todo/addNewTask.jsp",{
		title : '新建任务',
		width : 400,
		height : 200,
		cancelVal : "关闭",
		cancel : function() {
			return true;
		},
		okVal : "启动",
		ok : function() {
			var iframe = this.iframe.contentWindow;
			iframe.lanuchTask(this, document,'sys_task_todo___sys_task_todo_legend');
			return false;
		},
	});
}

function lanuchTask(win,doc,name){
	$.messager.progress();
	$('#sys_task_todo_legendFormObj').form('submit',	{
		url : "task-start-up?lockId="+lockId,
		onSubmit : function(data) {
			var isValid = $(this).form('validate');
			if (!isValid) {
				$.messager.progress('close');
			}
			return isValid;
		},
		success : function(data) {
			$.messager.progress('close');
			var result="当前系统繁忙";try{data = eval('(' + data + ')');	result=data.rs;}catch(e){try{data = eval(data);result=data.rs;}catch(e1){}}
			if ("Y" == result) {
				callback_info( "成功", function (){
					win.close();
					doc.getElementById(name+'_refresh_toolbar').click();
				});
			} else {
				error(result);
			}
		}
	});
}