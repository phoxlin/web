<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<script type="text/html" id="jhTaskTpl">
<form onkeydown="if(event.keyCode==13)return false;" onsubmit="return false;" id="<#=task.code#>_Form" name="<#=task.code#>_Form" method="post" class="form-inline">
	
	<input id="instance_id" name="instance_id" type="hidden" value="<#=task.instance_id#>">
	<input id="instance_no" name="instance_no" type="hidden" value="<#=task.instance_no#>">
	<input id="task_step_id" name="task_step_id" type="hidden" value="<#=task.step_id#>">
	<input id="task_state" name="task_state" type="hidden" value="<#=task.state#>">
	<input id="taskcode" name="taskcode" type="hidden" value="<#=task.code#>">
	<input id="taskuserid" name="taskuserid" type="hidden" value="<#=task.userid#>">
<!-- ----------工作流步骤展示---------- -->
<#
	if(isTask=='Y'){
#>
	<ul class="processor_bar grid_line">
<#
	if(task.steps!=null&&task.steps.length>0){
		var currentNum = "";
		for(var i=0;i<task.steps.length;i++){
			var taskstep=task.steps[i];
			var sid=taskstep.id;
			var isCurrent=(sid==task.step_id);
			if(isCurrent){
				currentNum = i;
			}
		}
		for(var i=0;i<task.steps.length;i++){
			var taskstep=task.steps[i];
			var sid=taskstep.id;
			var taskname=taskstep.taskname;
			var isCurrent=(sid==task.step_id);
			var className = "next";
			if(currentNum > i){
				if(currentNum - i == 1){
					className = "prev";
				} else {
					className = "pprev";
				}
			} else if(currentNum < i){
				if(i - currentNum == 1){
					className = "next";
				} else {
					className = "nnext";
				}
			}
			if(currentNum.length <= 0){
				className = "";
			}
			if(isCurrent){
			#>
				<!--<li class="step grid_item size1of5 prev" style="width: 15px;"><h4>&nbsp;</h4></li>-->
				<li class="step grid_item size1of5 current">
					<a  href="pages/<#=taskstep.taskcode#>/index.jsp?sid=<#=sid#>"><h4><#=(i+1)#> <#=taskname#></h4></a>
				</li>	
				<!--<li class="step grid_item size1of5 next" style="width: 15px;"><h4>&nbsp;</h4></li>-->		
			<#
			}else{
			#>
				<li class="step grid_item size1of5 <#=className#> whitecurrent"> 
					<a  href="pages/<#=taskstep.taskcode#>/index.jsp?sid=<#=sid#>"><h4><#=(i+1)#> <#=taskname#></h4></a>
				</li>	    
				<!--<li class="step grid_item size1of5 nnext" style="width: 15px;"><h4>&nbsp;</h4></li>-->
			<#
			}
		}
	}
#>
	</ul>
<#
	}
#>
<!-- ----------工作流步骤展示结束---------- -->

<!-- ----------内容框---------- -->
			<#
		     	for(var i=0;i<task.legends.length;i++){
		     		var legend=task.legends[i];
		     		var showlegendId=false;
		     		if(legend.name.length>0){
		     			showlegendId=true;
		     		}
					var autoLoadingData=legend.autoLoadingData;

		     		#>
		     		
		     			<#if(showlegendId){#>
						<!--<legend  name="<#=legend.code#>" id="<#=legend.code#>">-->
							<!--<#=legend.name#>-->
							<#
								if(autoLoadingData){
									#>
									<img onclick="autoFetch('<#=legend.code#>','<#=task.step_id#>',this);" src="public/process/tpl/default/img/autoLoad.png" style="width: 30px;height: 30px;">
									<#
								}
							#>
						</legend><#}#>
		     			<#
						if("legend"==legend.type){

							for(var k=0;k<legend.rows.length;k++){
								var row=legend.rows[k];
								#>
							<div class="row " style="margin-right: 0;margin-left: 0;padding:2px;">
								<#
								for(var m=0;m<row.cols.length;m++){
									var col=row.cols[m];
									var labelColumn=col.labelCols;
									var fieldColumn=col.fieldCols;
									var cols=col.cols;

									var labelClass=col.labelClass;
									var labelStyle=col.labelStyle;
									if(labelClass==null||labelClass.length<=0){labelClass=""}
									if(labelStyle==null||labelStyle.length<=0){labelStyle=""}

									var column_clz=col.col_class;
									var column_style=col.col_style;
									if(column_clz==null||column_clz.length<=0){column_clz=""}
									if(column_style==null||column_style.length<=0){column_style=""}

									var val="";
									try{val=task.data[col.input_tablename+"__"+col.name];}catch(e){}
									if(val==undefined||val.length<=0){
										val=col.defaultValue;										
									}

									var showfieldId=false;
						     		if(col.name.length>0){
						     			showfieldId=true;
						     		}

						     		var hidden=col.hidden;

						     		if(!hidden){
									#>
									<div class="col-xs-<#=cols#>">
										<div class="form-group" style="display: block;">
											<label class="col-xs-<#=labelColumn#> input-sm <#=labelClass#>" style="<#=labelStyle#>">
												<#=col.comment#><#=col.nullable?'':'(*)'#>
											</label>
											<#
												var inputStr=createProcessComponent(col.input_tablename+"__"+col.name,col,fieldColumn,column_clz,column_style,val);
											#>
											<#:=inputStr#>
										</div>
									</div>
									<#
									}else{
										
										#>
										<input <#if(showfieldId){#> id="<#=col.input_tablename#>__<#=col.name#>" name="<#=col.input_tablename#>__<#=col.name#>" <#}#>
												type="hidden" value="<#=val#>">
										<#
									}
								}
								#>
							</div>
							<#
							}
						}else if('data-grid'==legend.type){
							var cq_name=task.code+'___'+legend.code;				
							#>
								<div id="<#=cq_name#>"></div>
		     				<#
								cq(cq_name,'task');
						}else{
							console.warn('其他类型的legend【"+legend.type+"】');	
						}
		     			#>
		     		<#
		     	}
		     
		     #>	

</form>
<#
	if(isTask=='Y'){
		if(task.state=='waitview'||task.state=='viewed'||task.state=='drafted'||task.state=='waitview'){
#>
<!-- -------------确认提交开始---------- -->
<div class="row" style="margin-top:20px;">
		<div class="col-md-5 col-md-offset-4">
			<div class="row">
			<div class="col-md-3 col-md-offset-1">
				<button type="button" class="btn btn-default" onclick="saveDraft('<#=task.code#>_Form')">保存草稿</button>
			</div>
			<#
				var next_step_count=task.next_step_count;
				if(next_step_count>0){
					#>
			<div class="col-md-3">
				<button type="button" class="btn btn-info" onclick="saveAndForward('<#=task.code#>_Form')">保存&下一步</button>
			</div>			
					<#
				}else{
					#>
			<div class="col-md-3">
				<button type="button" class="btn btn-info" onclick="saveAndClose('<#=task.code#>_Form')">保存&完成任务</button>
			</div>	
					<#
				}
			#>
			
	 		</div>
		</div>
  </div>
  <!-- -------------确认提交结束---------- -->
	
<#
		}else{
#>
<div class="row" style="margin-top:20px;">
		<div class="col-md-5 col-md-offset-4">
			<div class="row">
			<div class="col-md-6 col-md-offset-1">
				<button type="button" class="btn btn-default">知道了</button>
			</div>
		</div>
  </div>
<#
		}
	}
#>

</script>