<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<script type="text/html" id="jhTask_forwardTpl">
	<#
		var trans=li.trans;
	#>
		<form id="<#=name#>" class="form-inline" style="width:<#=width#>;height:<#=height#>" method="post">
			<input type="hidden" value="<#=li.instance_id#>" id="instance_id" name="instance_id" >
			<input type="hidden" value="<#=li.instance_no#>" id="instance_no" name="instance_no" >
			<input type="hidden" value="<#=li.state#>" id="state" name="state" >
			<input type="hidden" value="<#=li.step_id#>" id="step_id" name="step_id" >
			<input type="hidden" value="<#=li.taskcode#>" id="taskcode" name="taskcode" >
			<div class="row">
				<div class="col-md-6">
					<ul class="nav nav-tabs" id="<#=name#>_tabs"  role="tablist">
					<#
						for(var i=0;i<trans.length;i++){
							var tranClass=""; if(i==0){ tranClass="active";}
							var tranHref=name+"_"+i;
							var tran=trans[i];
							var display=tran.name;
							if(i==0){
							#>
							<input id="traned2Name" name="traned2Name" type="hidden" value="<#=tran.to#>" >
							<#							
							}
							#>
							<li role="presentation" class="<#=tranClass#>"><a data-href='<#=tranHref#>' data-tran2name='<#=tran.to#>'><#=display#></a></li>
							<#
						}
					#>
					</ul>
				</div>
			</div>
			<div class="tab-content" style="margin-bottom: 5px;">
				<#
					for(var i=0;i<trans.length;i++){
						var tranClass=""; if(i==0){ tranClass="active";}
						var tranHref=name+"_"+i;
						var tran=trans[i];
						var display=tran.name;
						var hasRemark=false;
						var vals=tran.assigneeVals;
						#>
						<div role="tabpanel" class="tab-pane <#=tranClass#>" id="<#=tranHref#>">
								<div class="tab-text">
									<select class="form-control " data-userinfo='<#=tran.to#>'  style="width:180px;margin-top:10px;">
											<option value="">请选择下一步操作员</option>
									<#
										for(var ii=0;ii<tran.assigneeVals.length;ii++){
											var op=tran.assigneeVals[ii];
											var code=op.code;
											var note=op.note;
											#>
		  									<option value="<#=code#>"><#=note#></option>
											<#
										}
									#>
									</select>
								</div>
						</div>
						<#
					}
				#>
			</div>
			<!------------ -tabs结束------------ -->
			<!-- ------------备注开始---------- -->

			<div class="end-container">
				<div class="input-group" style="width:100%;">
					<textarea  class="form-control" id="forward_remark" rows="2" name="forward_remark" placeholder="请填写备注..."></textarea>
				</div>

			</div>
			<!-- -------------确认提交开始---------- -->
			<div class="row">
				<div class="col-md-6 col-md-offset-4">
						<div class="checkbox col-md-4" style="padding: 0px;line-height: 40px;">
							<label> <input type="checkbox" id="forward_confirm">我已确认
							</label>
						</div>
						<div class="col-md-4" style="line-height: 40px;">
							<button type="button" class="btn btn-info" onclick="forwardConfirm('<#=name#>');">提交</button>
						</div>
				</div>
			</div>
		<!-- -------------确认提交结束---------- -->
	</form>
</script>