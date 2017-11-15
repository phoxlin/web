<%@page import="java.util.List"%>
<%@page import="com.gd.m.GdUser"%>
<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	GdUser user=(GdUser)SystemUtils.getSessionUser(request, response);
	List<String>cds=null;
	if(user!=null){
		cds=user.getCD();
	}
%>

<script type="text/html" id="common_query_Index_dataOnlyTpl">
	<#
		var cd={};
		for(var i=0;i<cds.length;i++){
			cd[''+cds[i]]=true;
		}

		var head = list.head;
		
		var headers=[];
		
		var orderby='';
		var desc='desc';
		if(list.orderby!=undefined){orderby=list.orderby; };
		if(list.desc!=undefined){desc=list.desc; };
											
		if(head != null && head.length > 0){
			for(var i=0; i<head.length; i++){
				var item = head[i];
				if(item.show){
					headers.push(item);
				}
			}
		}
		var colspan=headers.length+1;

	#>
	<div style="position: relative;padding: 0;" id="<#=name#>_responsive" name="<#=name#>_responsive" class="table-responsive data-container col-lg-12">
		<div style="position: absolute;min-width: 100%;top: 0;border-bottom: 1px solid #d8d8d8;">
			<table>
				<thead>
					<tr>
						<th style="width: 30px;">
							<input id="<#=name#>_all" type="checkbox" onclick="checkedAll('<#=name#>');">
						</th>
						<#
							for(var i=0;i<headers.length;i++){
								var h=headers[i];
								var sortclass="";
								var descParam='desc'; 
								if(h.code==list.orderby){
									if(list.desc=='desc'){
										sortclass="descClass";
										descParam='desc';
									}else{
										sortclass="ascClass";
										descParam='asc';
									}
								}
								#>
									<th class="<#=sortclass#>" onclick="cq('<#=name#>','<#=type#>',<#=params#>,'<#=list.curpage#>','<#=list.pagesize#>','<#=h.code#>','<#=descParam#>');">
										<div style="width: <#=h.width#>px;overflow: hidden;"><#=h.display#></div>
									</th>
								<#
							}
						#>
						<th><div style="width: 100px;">操作</div></th>
					</tr>
				</thead>
			</table>
		</div>
		
		<#
			var w = list.tableWidth;
			w = w + headers.length*10 + 23;
		#>
		<div class="data" style="min-width: 100%;margin-top: 43px;width:<#=w#>px;overflow-y:auto;overflow-x:hidden;">
			<table id="<#=name#>_table" name="<#=name#>_table" class="table table-hover table-condensed">
				<tbody>
					<#
					if(list && list.rows.length > 0){
						for(var i=0;i<list.rows.length;i++){
							var obj = list.rows[i];
							#>
							<tr ondblclick="onDblClick('<#=name#>');">
								<td style="width: 30px;">
									<input name="<#=name#>_row" id="<#=name#>_row_<#=i#>" type="checkbox"  onclick="changed_click('<#=name#>','<#=i#>')">
									<#
										 
										if(head != null && head.length > 0){
											for(var k=0; k<head.length; k++){
												var item = head[k];
												if(item.send){
													var val=obj[item.code];if(val == undefined){val='';}
													var input_name=name+"__"+item.code+"_"+i;
													#>
													<input name="<#=input_name#>" id="<#=input_name#>" type="hidden" value="<#=val#>">
													<#
													
													var val2=obj[item.code+"__qm_code"];if(val2 == undefined){val2='';}
													var input_name2=name+"__"+item.code+"__qm_code"+"_"+i;
													if(val2.length>0){
													#>
													<input name="<#=input_name2#>" id="<#=input_name2#>" type="hidden" value="<#=val2#>">
													<#
													}
												}
											}
										}
									#>
								</td>
									<#
									
									for(var k=0;k<headers.length;k++){
										var h=headers[k];
										var input_html_name=name+"__"+h.code+"_html_"+i;
										var val=obj[h.code]; if(val == undefined){val='';}
			
									#>
										<td style="overflow: hidden;" id="<#=input_html_name#>" name="<#=input_html_name#>">
		 									<div style="width: <#=h.width#>px;">
												<#=val#>
											</div>
										</td>
									<#
								}
									#>
								<td>
									<div style="width: 100px;">
										<#
										if(list.toolbars!=null&&list.toolbars.length>0){
											for(var xx=0;xx<list.toolbars.length;xx++){
												var toolbar=list.toolbars[xx];
												var js=toolbar.js;
												var text=toolbar.text;
												var toolbarname=toolbar.name;
												var class_name = toolbar._class;

												if(cd[""+toolbar.visibleCodes]||!toolbar.visibleCodes||toolbar.visibleCodes.length<=0){

												if("修改" == text){
												#>
													<span onclick="javascript:<#=js#>('<#=name#>')" id="<#=toolbarname#>_toolbar" class="icon-update"></span>		
												<#
												} else if("删除" == text){
												#>
													<span onclick="javascript:<#=js#>('<#=name#>')" id="<#=toolbarname#>_toolbar" class="icon-remove"></span>		
												<#
												} else if("详细信息" == text){
												#>
													<span onclick="javascript:<#=js#>('<#=name#>')" id="<#=toolbarname#>_toolbar" class="icon-detail"></span>		
												<#
												}
												}
											}
										}
										#>
									</div>
								</td>
							</tr>
						<#}
					} else{
					#>
						<tr style="display: block;">
							<td style="display: block;text-align: center;" colspan="<#=colspan#>" style="text-align: center;">没有查询到符合条件的数据</td>
						</tr>
					<#
					}
					#>
					</tbody>
			</table>
		</div>
		
	</div>
</script>
					

<script type="text/html" id="common_query_IndexTpl">
<div class="panel panel-info">
	  <div class="panel-body">
	<#
		var cd={};
		for(var i=0;i<cds.length;i++){
			cd[''+cds[i]]=true;
		}

		var head = list.head;
		
		var headers=[];
		
		var orderby='';
		var desc='desc';
		if(list.orderby!=undefined){orderby=list.orderby; };
		if(list.desc!=undefined){desc=list.desc; };
											
		if(head != null && head.length > 0){
			for(var i=0; i<head.length; i++){
				var item = head[i];
				if(item.show){
					headers.push(item);
				}
			}
		}
		
		var colspan=headers.length+1;
	#>
	<div class="filter-container">
	<#
	if(list.searchs!=null&&list.searchs.length>0){
		#>
		<#
			for(var i=0;i<list.searchs.length;i++){
				var row_search=list.searchs[i];
				for(var k=0;k<row_search.length;k++){
					var sfield=row_search[k];
					var label=sfield.label;
					var stype=sfield.type;
					var value=sfield.value;
					var labelname=sfield.name;

					#>
					<#
						if(stype=='text'){
						#>
						<input type="text" style="width:140px;height: 30px;" id="<#=labelname#>" placeholder="<#=label#>" value="<#=value#>">
						<#
						}else if(stype=='code'){
						#>
						<select id="<#=labelname#>" style="width:140px;height: 30px;">
							<option value =""><#=label#></option>
						<#
							if(sfield.bindData!=null&&sfield.bindData.length>0){
								for(var j=0;j<sfield.bindData.length;j++){
									var data=sfield.bindData[j];
									var code=data.code;
									var note=data.note;
									if(code==value){
										#>
											<option value ="<#=code#>" selected='selected'><#=note#></option>
										<#
									}else{
										#>
										<option value ="<#=code#>"><#=note#></option>
										<#
									}
								}
							}				
						#>
						</select>
						<#
						}else if(stype=='date'){
						#>
							<input id="<#=labelname#>" type="text" class="easyui-datebox" value="<#=value#>" data-attr="<#=label#>" options="editable:false">
						<#
						}
					}
			}
		#>
		<#
		}
	#>
		<input  id="<#=name#>_cq_type" value="<#=list.cq_type#>" type="hidden">
		<input id="<#=name#>_editpage" value="<#=list.editpage#>"  type="hidden">
		<a style="display: none;" id="<#=name#>_refresh_toolbar" class="btn btn-primary-plain icon-refresh" onclick="cq('<#=name#>','<#=type#>',<#=params#>,'<#=list.curpage#>','<#=list.pagesize#>','<#=orderby#>','<#=desc#>');">刷新</a>
		<#
			if(list.searchs!=null&&list.searchs.length>0){
		#>
			<button id="<#=name#>_Search_toolbar" class="btn btn-primary-plain" onclick="cq('<#=name#>','<#=type#>',<#=params#>,'<#=list.curpage#>','<#=list.pagesize#>','<#=orderby#>','<#=desc#>');">搜索</button>
		<#
		}
		if(list.toolbars!=null&&list.toolbars.length>0){
			for(var i=0;i<list.toolbars.length;i++){
				var toolbar=list.toolbars[i];
				var js=toolbar.js;
				var text=toolbar.text;
				var toolbarname=toolbar.name;
				var class_name = toolbar._class;
				
				if(toolbar.li&&toolbar.li.length>0){
					
				#>
				<div class="btn-group">
					<button type="button" class="btn btn-primary-plain dropdown-toggle" data-toggle="dropdown">
					  <#=text#> <span class="caret"></span>
					</button>
					<ul class="dropdown-menu" role="menu">
						<#
							for(var k=0;k<toolbar.li.length;k++){
								var st=toolbar.li[k];
								var js1=st.js;
								var text1=st.text;
								var toolbarname1=st.name;
								var class_name1 = st._class;
								console.log(toolbar.visibleCodes+"  "+text1);
								if(cd[""+toolbar.visibleCodes]||!toolbar.visibleCodes||toolbar.visibleCodes.length<=0){
								#>
									<li><a  id="<#=toolbarname1#>_toolbar" class="btn <#=class_name1#>"  onclick="javascript:<#=js1#>('<#=name#>')"><#=text1#></a>
								<#
								}
							}
						#>
					</ul>
				</div>
				<#
				}else{
					if("修改" == text || "删除" == text || "详细信息" == text){
						continue;
					}
if(cd[""+toolbar.visibleCodes]||!toolbar.visibleCodes||toolbar.visibleCodes.length<=0){				
				#>
					<button id="<#=toolbarname#>_toolbar" class="btn btn-primary-plain" onclick="javascript:<#=js#>('<#=name#>')"><#=text#></button>
				<#		
				}
}	
			}
		}
			if(!cashier){
		#>
		<div class="btn-group">
			<button type="button" class="btn btn-primary-plain dropdown-toggle" data-toggle="dropdown">
			  数据导出 <span class="caret"></span>
			</button>
			<ul class="dropdown-menu" role="menu">
				<li><a onclick="cExpert('<#=name#>','<#=type#>',<#=params#>,'<#=list.curpage#>','<#=list.pagesize#>','<#=orderby#>','<#=desc#>');">导出当前页</a></li>
				<li><a onclick="cExpert('<#=name#>','<#=type#>',<#=params#>,'-1','<#=list.pagesize#>','<#=orderby#>','<#=desc#>');">导出所有页</a></li>
			</ul>
		</div>
		<#
			}
		#>
	</div>
<div id="<#=name#>_responsive_tableAndpaging">
	<div style="position: relative;padding: 0;" id="<#=name#>_responsive" name="<#=name#>_responsive" class="table-responsive data-container col-lg-12">
		<div style="position: absolute;min-width: 100%;top: 0;border-bottom: 1px solid #d8d8d8;">
			<table>
				<thead>
					<tr>
						<th style="width: 30px;"><input id="<#=name#>_all" type="checkbox" onclick="checkedAll('<#=name#>');"></th>
						<#
							for(var i=0;i<headers.length;i++){
								var h=headers[i];
								var sortclass="";
								var descParam='desc'; 
								if(h.code==list.orderby){
									if(list.desc=='desc'){
										sortclass="descClass";
										descParam='desc';
									}else{
										sortclass="ascClass";
										descParam='asc';
									}
								}
								#>
									<th class="<#=sortclass#>" onclick="cq('<#=name#>','<#=type#>',<#=params#>,'<#=list.curpage#>','<#=list.pagesize#>','<#=h.code#>','<#=descParam#>');">
										<div style="width: <#=h.width#>px;overflow: hidden;"><#=h.display#></div>
									</th>
								<#
							}
						#>
						<th>
							<div style="width: 100px;">操作</div>
						</th>
					</tr>
				</thead>
			</table>
		</div>
		<#
			var w = list.tableWidth;
			w = w + headers.length*10 + 23 + 100;
		#>
		<div class="data" style="min-width: 100%;margin-top: 43px;width:<#=w#>px;overflow-y:auto;overflow-x:hidden;">
			<table style="min-width: 100%;" id="<#=name#>_table" name="<#=name#>_table" class="table table-hover table-condensed">
				<tbody>
					<#
					if(list && list.rows.length > 0){
						for(var i=0;i<list.rows.length;i++){
							var obj = list.rows[i];
							#>
							<tr ondblclick="onDblClick('<#=name#>');">
								<td style="width: 30px;">
									<input name="<#=name#>_row" id="<#=name#>_row_<#=i#>" type="checkbox"  onclick="changed_click('<#=name#>','<#=i#>')">
									<#
										 
										if(head != null && head.length > 0){
											for(var k=0; k<head.length; k++){
												var item = head[k];
												if(item.send){
													var val=obj[item.code];if(val == undefined){val='';}
													var input_name=name+"__"+item.code+"_"+i;
													#>
													<input name="<#=input_name#>" id="<#=input_name#>" type="hidden" value="<#=val#>">
													<#
												}
											}
										}
									#>
								</td>
									<#
									
									for(var k=0;k<headers.length;k++){
										var h=headers[k];
										var input_html_name=name+"__"+h.code+"_html_"+i;
										var val=obj[h.code]; if(val == undefined){val='';}
			
									#>
										<td style="overflow: hidden;" id="<#=input_html_name#>" name="<#=input_html_name#>">
		 									<div style="width: <#=h.width#>px;">
												<#=val#>
											</div>
										</td>
									<#
								}
									#>
								<td>
									<div style="width: 100px;">
										<#
										if(list.toolbars!=null&&list.toolbars.length>0){
											for(var xx=0;xx<list.toolbars.length;xx++){
												var toolbar=list.toolbars[xx];
												var js=toolbar.js;
												var text=toolbar.text;
												var toolbarname=toolbar.name;
												var class_name = toolbar._class;
											if(cd[""+toolbar.visibleCodes]||!toolbar.visibleCodes||toolbar.visibleCodes.length<=0){
												if("修改" == text){
												#>
													<span onclick="javascript:<#=js#>('<#=name#>')" id="<#=toolbarname#>_toolbar" class="icon-update"></span>		
												<#
												} else if("删除" == text){
												#>
													<span onclick="javascript:<#=js#>('<#=name#>')" id="<#=toolbarname#>_toolbar" class="icon-remove"></span>		
												<#
												} else if("详细信息" == text){
												#>
													<span onclick="javascript:<#=js#>('<#=name#>')" id="<#=toolbarname#>_toolbar" class="icon-detail"></span>		
												<#
												}
												}
											}
										}
										#>
									</div>
								</td>
							</tr>
						<#
							}
						}else{
						#>
							<tr style="display: block;">
								<td style="display: block;text-align: center;" colspan="<#=colspan#>" style="text-align: center;">没有查询到符合条件的数据</td>
							</tr>
						<#	
						}#>
					</tbody>
			</table>
		</div>
	</div>
</div>
</div>
</div>	

<#
if(list.curpage>0&&list.totalpage>0){
	var curpage = list.curpage;
	var pre = curpage - 1;
	if(pre < 0){
		pre = 0;
	}
#>
	<div id="<#=name#>_responsive_tablepager" class="row" style="margin-top: 1%;">
		<div class="col-xs-6">
			 总数<#=list.total#>&nbsp;当前页条数<#=list.rows.length#>
		</div>
		<div class="col-xs-6 pager" style="text-align: right;">
			<#
				if(pre <= 0){
			#>
				<a class="disabled">
					<i style="margin-right: -1px;">|</i>
					<span class="glyphicon glyphicon-menu-left"></span>
				</a>
				<a class="disabled">
					<span class="glyphicon glyphicon-menu-left"></span>
				</a>
			<#		
				} else {
			#>
				<a onclick="cq('<#=name#>','<#=type#>',<#=params#>,'1','<#=list.pagesize#>','<#=orderby#>','<#=desc#>');">
					<i style="margin-right: -1px;">|</i>
					<span class="glyphicon glyphicon-menu-left"></span>
				</a>
				<a onclick="cq('<#=name#>','<#=type#>',<#=params#>,'<#=pre#>','<#=list.pagesize#>','<#=orderby#>','<#=desc#>');" class="glyphicon glyphicon-menu-left"></a>
			<#		
				}
			#>
			<input data-name="<#=name#>" data-type="<#=type#>" data-params="<#=params#>" 
					data-totalpage="<#=list.totalpage#>" data-pagesize="<#=list.pagesize#>"
					data-orderby="<#=orderby#>" data-desc="<#=desc#>" onkeyup="pager(this,event)"
				value="<#=curpage#>" type="number" style="max-width: 55px;padding: 0 5px;height: 23px;margin: 0 5px 0 10px;">/<#=list.totalpage#>&nbsp;&nbsp;
			<#
				if(curpage == list.totalpage){
			#>
			<a class="disabled">
				<span class="glyphicon glyphicon-menu-right"></span>
			</a>
			<a class="disabled">
				<span class="glyphicon glyphicon-menu-right"></span>
				<i style="margin-left: -1px;">|</i>
			</a>
			<#		
				} else {
			#>
			<a onclick="cq('<#=name#>','<#=type#>',<#=params#>,'<#=curpage+1#>','<#=list.pagesize#>','<#=orderby#>','<#=desc#>');" class="glyphicon glyphicon-menu-right"></a>
			<a onclick="cq('<#=name#>','<#=type#>',<#=params#>,'<#=list.totalpage#>','<#=list.pagesize#>','<#=orderby#>','<#=desc#>');">
				<span class="glyphicon glyphicon-menu-right"></span>
				<i style="margin-left: -1px;">|</i>
			</a>
			<#
				}
			#>
		</div>
	</div>
	<#
}
#>
</script>
<script type="text/html" id="common_query_Index_pagerTpl">

<#
if(list.curpage>0&&list.totalpage>0){
	var orderby='';
	var desc='desc';
	if(list.orderby!=undefined){orderby=list.orderby; };
	if(list.desc!=undefined){desc=list.desc; };
	var curpage = list.curpage;
	var pre = curpage - 1;
	if(pre < 0){
		pre = 0;
	}
#>
		<div class="col-xs-6">
			 总数<#=list.total#>&nbsp;当前页条数<#=list.rows.length#>
		</div>
		<div class="col-xs-6 pager" style="text-align: right;">
			<#
				if(pre <= 0){
			#>
				<a class="disabled">
					<i style="margin-right: -1px;">|</i>
					<span class="glyphicon glyphicon-menu-left"></span>
				</a>
				<a class="disabled">
					<span class="glyphicon glyphicon-menu-left"></span>
				</a>
			<#		
				} else {
			#>
				<a onclick="cq('<#=name#>','<#=type#>',<#=params#>,'1','<#=list.pagesize#>','<#=orderby#>','<#=desc#>');">
					<i style="margin-right: -1px;">|</i>
					<span class="glyphicon glyphicon-menu-left"></span>
				</a>
				<a onclick="cq('<#=name#>','<#=type#>',<#=params#>,'<#=pre#>','<#=list.pagesize#>','<#=orderby#>','<#=desc#>');" class="glyphicon glyphicon-menu-left"></a>
			<#		
				}
			#>
			<input data-name="<#=name#>" data-type="<#=type#>" data-params="<#=params#>" 
					data-totalpage="<#=list.totalpage#>" data-pagesize="<#=list.pagesize#>"
					data-orderby="<#=orderby#>" data-desc="<#=desc#>" onkeyup="pager(this,event)"
				value="<#=curpage#>" type="number" style="max-width: 55px;padding: 0 5px;height: 23px;margin: 0 5px 0 10px;">/<#=list.totalpage#>&nbsp;&nbsp;
			<#
				if(curpage == list.totalpage){
			#>
			<a class="disabled">
				<span class="glyphicon glyphicon-menu-right"></span>
			</a>
			<a class="disabled">
				<span class="glyphicon glyphicon-menu-right"></span>
				<i style="margin-left: -1px;">|</i>
			</a>
			<#		
				} else {
			#>
			<a onclick="cq('<#=name#>','<#=type#>',<#=params#>,'<#=curpage+1#>','<#=list.pagesize#>','<#=orderby#>','<#=desc#>');" class="glyphicon glyphicon-menu-right"></a>
			<a onclick="cq('<#=name#>','<#=type#>',<#=params#>,'<#=list.totalpage#>','<#=list.pagesize#>','<#=orderby#>','<#=desc#>');">
				<span class="glyphicon glyphicon-menu-right"></span>
				<i style="margin-left: -1px;">|</i>
			</a>
			<#
				}
			#>
		</div>
	<#
}
#>
</script>				