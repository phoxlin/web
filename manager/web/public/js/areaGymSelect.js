//通用查询使用
function  areaGymSelect(areaSelect,gymselect,gymLevel,dept,salesSelect,coachSelect){
$.ajax({
			url : "search-info",
			dataType : "json",
			success : function(data) {
				if (data.rs == "Y") {
					var area_code = data.area_code;
					var usergym = data.usergym;
					var area = data.area;
					var gym = data.gym;
					initSelect2(area, gym,area_code,usergym,areaSelect,gymselect,gymLevel,dept,salesSelect,coachSelect);
				}
			},
			error : function() {
				error("网络错误,请刷新重试!");
			}
		});

}

function initSelect2(area, gym,area_code,usergym,areaSelect,gymselect,gymLevel,deptSelect,salesSelect,coachSelect){
	var area = JSON.parse(area);
	$("#"+areaSelect).html();
	var aHtml = '<option value="">地区</option>';
	for(var i = area.length; i--;) {
		var a = area[i];
		aHtml += '<option value="'+a.id+'">' + a.text + '</option>';
	}
	
	$("#"+areaSelect).html(aHtml);
	$(document).on("change", "#"+areaSelect, function() {
		changeLevelSelect(gym,gymLevel);
		changeGymSelect2(gym,areaSelect,gymselect);
	});
	$(document).on("change", "#"+gymselect, function() {
		getdept(gymselect,deptSelect);
		getcoach(gymselect,"",coachSelect);
		getsales(gymselect,"",salesSelect);
		typeCodeSelect();
	});
	$(document).on("change", "#"+deptSelect, function() {
		getcoach(gymselect,deptSelect,coachSelect);
		getsales(gymselect,deptSelect,salesSelect);
	});
}
function getsales(gymselect,deptSelect,salesSelect){
	var dept = $("#"+deptSelect).val();
	var gym = $("#"+gymselect).val();
	var lHtml = "<option value=''>会籍顾问</option>";
	$.ajax({
		url : "search-info-emp-sales",
		dataType : "json",
		data:{
			gym:gym,
			dept:dept
		},
		success : function(data) {
			if (data.rs == "Y") {
				var emp = data.emp
				for(var i =0; i < emp.length; i++) {
					var l = emp[i];
					lHtml += "<option value='"+l.id+"'>" + l.emp_name + "</option>";
				}
				
				$("#"+salesSelect).html(lHtml);
			}
		},
		error : function() {
			error("网络错误,请刷新重试!");
		}
	});
}
function getcoach(gymselect,deptSelect,coachSelect){
	var dept = $("#"+deptSelect).val();
	var gym = $("#"+gymselect).val();
	var lHtml = "<option value=''>专属教练</option>";
	$.ajax({
		url : "search-info-emp-coach",
		dataType : "json",
		data:{
			gym:gym,
			dept:dept
		},
		success : function(data) {
			if (data.rs == "Y") {
				var emp = data.emp
				for(var i =0; i < emp.length; i++) {
					var l = emp[i];
					lHtml += "<option value='"+l.id+"'>" + l.emp_name + "</option>";
				}
				
				$("#"+coachSelect).html(lHtml);
			}
		},
		error : function() {
			error("网络错误,请刷新重试!");
		}
	});
}
function getdept(gymselect,deptselect){
	var gym = $("#"+gymselect).val();
	var lHtml = "<option value=''>员工部门</option>";
	$.ajax({
		url : "search-info-dept",
		dataType : "json",
		data:{
			gym:gym
		},
		success : function(data) {
			if (data.rs == "Y") {
				var dept = data.dept;
				for(var i =0; i < dept.length; i++) {
					var l = dept[i];
					var de = l.dept;
					if(de){
						lHtml += "<option value='"+de+"'>" + de + "</option>";
					}
				}
				
				$("#"+deptselect).html(lHtml);
			}
		},
		error : function() {
			error("网络错误,请刷新重试!");
		}
	});
}
function changeLevelSelect(gym,gymLevel) {
	var lHtml = "<option value=''>通店</option>";
	var selectArea = $("#area-select").val();
	
	var a = 0;
	var levels = [];
	for(var i in gym) {
		var g = gym[i];
		if(selectArea && g.area_code == selectArea) {
			levels.push(g.gym_level);
		} else if(!selectArea) {
			levels.push(g.gym_level);
		}
	}
	//levels = levels.sort();
	levels.sort(function(a,b){return a>b?1:-1});//从小到大排序
	var res =  [levels[0]];
		 for(var i = 1; i < levels.length; i++){
			  if(levels[i] !== res[res.length - 1]){
			   res.push(levels[i]);
			  }
			 }
	for(var i =0; i < res.length; i++) {
		var l = res[i];
		if(l != undefined ){
			lHtml += "<option value='"+l+"'>" + l + "</option>";
		}
	}
	$("#"+gymLevel).html(lHtml);
}

function changeGymSelect2(gym,areaSelect,gymselect) {
	$("#"+gymselect).html();
	var lHtml = "<option value=''>门店</option>";
	var selectArea = $("#"+areaSelect).val();
	var selectLevel = $("#level-select").val();
	var gyms = [];
	for(var i in gym) {
		var g = gym[i];
		if( g.area_code == selectArea) {
			gyms.push(g);
		}
	}
	
	gyms = unique2(gyms,"gym_name");
	for(var i =0; i < gyms.length; i++) {
		var l = gyms[i];
		lHtml += "<option value='"+l.gym+"'>" + l.gym_name + "</option>";
	}
	$("#"+gymselect).html(lHtml);
}
//通用查询使用

function unique(arr){
	var tmp = [];
	for(var m in arr){
		tmp[arr[m]]=1;
	}
	var tmparr = [];
	for(var n in tmp){
		tmparr.push(n);
	}
	return tmparr;
}

function unique2(arr, unique_name){
	var tmparr = [];
	if(arr != null && arr.length > 0){
		for(var n in arr){
			if(tmparr == null || tmparr.length <= 0){
				tmparr.push(arr[n]);
				continue;
			}
			var count = 0;
			for(var m in tmparr){
				if(arr[n][unique_name] == tmparr[m][unique_name]){
					count ++;
					break;
				}
			}
			if(count === 0){
				tmparr.push(arr[n]);
			}
		}
	}
	return tmparr;
}

function typeCodeSelect(){
	var gym = $("#gym_code").val();
	var lHtml = "<option value=''>卡种</option>";
	$.ajax({
		url : "search-info-type_code",
		dataType : "json",
		data:{
			gym:gym,
		},
		success : function(data) {
			if (data.rs == "Y") {
				var typeCode = data.typeCode
				for(var i =0; i < typeCode.length; i++) {
					var l = typeCode[i];
					lHtml += "<option value='"+l.type_code+"'>" + l.type_name + "</option>";
				}
				
				$("#type_code").html(lHtml);
			}
		},
		error : function() {
			error("网络错误,请刷新重试!");
		}
	});
}