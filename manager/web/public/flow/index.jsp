<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<base href="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>Bootstrap表单构造器</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="基于Bootstrap的web表单构造器，通过拖拽组件的方式生成完整可用的表单">
<meta name="keywords" content="Bootstrap,Form,表单,拖拽">
<meta name="author" content="Bootstrap中文网">

<link href="public/flow/files/bootstrap.min.css" rel="stylesheet">
<style>
body {
	padding-top: 60px;
	/* 60px to make the container go all the way to the bottom of the topbar */
	padding-bottom: 10px;
}

#components {
	min-height: 600px;
}

#target {
	min-height: 200px;
	border: 1px solid #ccc;
	padding: 5px;
}

#target .component {
	border: 1px solid #fff;
}

#temp {
	width: 500px;
	background: white;
	border: 1px dotted #ccc;
	border-radius: 10px;
}

.popover-content form {
	margin: 0 auto;
	width: 213px;
}

.popover-content form .btn {
	margin-right: 10px
}

#source {
	min-height: 500px;
}
</style>
<link href="public/flow/files/bootstrap-responsive.min.css" rel="stylesheet">

<!--[if lt IE 9]>
    <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

<link rel="shortcut icon" href="http://www.bootcss.com/p/bootstrap-form-builder/images/favicon.ico">
<link rel="apple-touch-icon" href="http://www.bootcss.com/p/bootstrap-form-builder/images/apple-touch-icon.png">
<link rel="apple-touch-icon" sizes="72x72" href="http://www.bootcss.com/p/bootstrap-form-builder/images/apple-touch-icon-72x72.png">
<link rel="apple-touch-icon" sizes="114x114" href="http://www.bootcss.com/p/bootstrap-form-builder/images/apple-touch-icon-114x114.png">
</head>

<body>

	<div class="navbar navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
				</a> <a class="brand" href="http://www.bootcss.com/p/bootstrap-form-builder/">Bootstrap 表单构造器</a>
			</div>
		</div>
	</div>
	<div class="container">
		<div class="row clearfix">
			<div class="span6">
				<div class="clearfix">
					<h2>Your Form</h2>
					<hr>
					<div id="build">
						<form id="target" class="form-horizontal" style="background-color: rgb(255, 255, 255);">
							<fieldset>
								<div id="legend" class="component" rel="popover" trigger="manual" data-content="
                    &lt;form class=&#39;form&#39;&gt;
                      &lt;div class=&#39;controls&#39;&gt;
                        &lt;label class=&#39;control-label&#39;&gt;Title&lt;/label&gt; &lt;input class=&#39;input-large&#39; type=&#39;text&#39; name=&#39;title&#39; id=&#39;text&#39;&gt;
                        &lt;hr/&gt;
                        &lt;button class=&#39;btn btn-info&#39;&gt;Save&lt;/button&gt;&lt;button class=&#39;btn btn-danger&#39;&gt;Cancel&lt;/button&gt;
                      &lt;/div&gt;
                    &lt;/form&gt;" data-original-title="Form Title"
									style="border-top: 1px solid white; border-bottom: none;">
									<legend class="valtype" data-valtype="text">表单名</legend>
								</div>




							</fieldset>
						</form>
					</div>
				</div>
			</div>

			<div class="span6">
				<h2>拖拽下面的组件到左侧</h2>
				<hr>
				<div class="tabbable">
					<ul class="nav nav-tabs" id="navtab">
						<li class="active"><a href="#1" data-toggle="tab">控件1</a></li>
						<li class=""><a href="#2" data-toggle="tab">控件2</a></li>
						<li class=""><a id="sourcetab" href="#5" data-toggle="tab">生成代码</a></li>
					</ul>
					<form class="form-horizontal" id="components">
						<fieldset>
							<div class="tab-content">

								<div class="tab-pane active" id="1">

									<div class="control-group component" data-type="text" rel="popover" title="单行输入框" trigger="manual"
										data-content="
                      &lt;form class=&#39;form&#39;&gt;
                        &lt;div class=&#39;controls&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Label Text&lt;/label&gt; &lt;input class=&#39;input-large&#39; type=&#39;text&#39; name=&#39;label&#39; id=&#39;label&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Placeholder&lt;/label&gt; &lt;input type=&#39;text&#39; name=&#39;placeholder&#39; id=&#39;placeholder&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Help Text&lt;/label&gt; &lt;input type=&#39;text&#39; name=&#39;help&#39; id=&#39;help&#39;&gt;
                          &lt;hr/&gt;
                          &lt;button class=&#39;btn btn-info&#39;&gt;Save&lt;/button&gt;&lt;button class=&#39;btn btn-danger&#39;&gt;Cancel&lt;/button&gt;
                        &lt;/div&gt;
                      &lt;/form&gt;">

										<!-- Text input-->
										<label class="control-label valtype" for="input01" data-valtype="label">单行输入框</label>
										<div class="controls">
											<input type="text" placeholder="提示文字" class="input-xlarge valtype" data-valtype="placeholder">
											<p class="help-block valtype" data-valtype="help">帮助说明文字</p>
										</div>
									</div>
									<div class="control-group component" rel="popover" title="多行输入框" trigger="manual" data-content="
					                      &lt;form class=&#39;form&#39;&gt;
					                        &lt;div class=&#39;controls&#39;&gt;
					                          &lt;label class=&#39;control-label&#39;&gt;Label Text&lt;/label&gt; &lt;input class=&#39;input-large&#39; type=&#39;text&#39; name=&#39;label&#39; id=&#39;label&#39;&gt;
					                          &lt;hr/&gt;
					                          &lt;button class=&#39;btn btn-info&#39;&gt;Save&lt;/button&gt;&lt;button class=&#39;btn btn-danger&#39;&gt;Cancel&lt;/button&gt;
					                        &lt;/div&gt;
					                      &lt;/form&gt;">
										<!-- Textarea -->
										<label class="control-label valtype" data-valtype="label">多行输入框</label>
										<div class="controls">
											<div class="textarea">
												<textarea type="" class="valtype" data-valtype="checkbox"> </textarea>
											</div>
										</div>
									</div>
									<div class="control-group component" data-type="text" rel="popover" title="数字输入框" trigger="manual"
										data-content="
                      &lt;form class=&#39;form&#39;&gt;
                        &lt;div class=&#39;controls&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Label Text&lt;/label&gt; &lt;input class=&#39;input-large&#39; type=&#39;text&#39; name=&#39;label&#39; id=&#39;label&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Placeholder&lt;/label&gt; &lt;input type=&#39;text&#39; name=&#39;placeholder&#39; id=&#39;placeholder&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Help Text&lt;/label&gt; &lt;input type=&#39;text&#39; name=&#39;help&#39; id=&#39;help&#39;&gt;
                          &lt;hr/&gt;
                          &lt;button class=&#39;btn btn-info&#39;&gt;Save&lt;/button&gt;&lt;button class=&#39;btn btn-danger&#39;&gt;Cancel&lt;/button&gt;
                        &lt;/div&gt;
                      &lt;/form&gt;">

										<!-- Text input-->
										<label class="control-label valtype" for="input01" data-valtype="label">数字输入框</label>
										<div class="controls">
											<input type="text" placeholder="提示文字" class="input-xlarge valtype" data-valtype="placeholder">
											<p class="help-block valtype" data-valtype="help">帮助说明文字</p>
										</div>
									</div>
									<div class="control-group component" data-type="text" rel="popover" title="金额" trigger="manual"
										data-content="
                      &lt;form class=&#39;form&#39;&gt;
                        &lt;div class=&#39;controls&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Label Text&lt;/label&gt; &lt;input class=&#39;input-large&#39; type=&#39;text&#39; name=&#39;label&#39; id=&#39;label&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Placeholder&lt;/label&gt; &lt;input type=&#39;text&#39; name=&#39;placeholder&#39; id=&#39;placeholder&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Help Text&lt;/label&gt; &lt;input type=&#39;text&#39; name=&#39;help&#39; id=&#39;help&#39;&gt;
                          &lt;hr/&gt;
                          &lt;button class=&#39;btn btn-info&#39;&gt;Save&lt;/button&gt;&lt;button class=&#39;btn btn-danger&#39;&gt;Cancel&lt;/button&gt;
                        &lt;/div&gt;
                      &lt;/form&gt;">

										<!-- Text input-->
										<label class="control-label valtype" for="input01" data-valtype="label">金额</label>
										<div class="controls">
											<input type="number" placeholder="提示文字" class="input-xlarge valtype" data-valtype="placeholder">
											<p class="help-block valtype" data-valtype="help">帮助说明文字</p>
										</div>
									</div>
									<div class="control-group component" rel="popover" title="单选框" trigger="manual"
										data-content="
					                      &lt;form class=&#39;form&#39;&gt;
					                        &lt;div class=&#39;controls&#39;&gt;
					                          &lt;label class=&#39;control-label&#39;&gt;Label Text&lt;/label&gt; &lt;input class=&#39;input-large&#39; type=&#39;text&#39; name=&#39;label&#39; id=&#39;label&#39;&gt;
					                          &lt;label class=&#39;control-label&#39;&gt;Options: &lt;/label&gt;
					                          &lt;textarea style=&#39;min-height: 200px&#39; id=&#39;option&#39;&gt; &lt;/textarea&gt;
					                          &lt;hr/&gt;
					                          &lt;button class=&#39;btn btn-info&#39;&gt;Save&lt;/button&gt;&lt;button class=&#39;btn btn-danger&#39;&gt;Cancel&lt;/button&gt;
					                        &lt;/div&gt;
					                      &lt;/form&gt;">

										<!-- Select Basic -->
										<label class="control-label valtype" data-valtype="label">单选框</label>
										<div class="controls">
											<select class="input-xlarge valtype" data-valtype="option">
												<option>Enter</option>
												<option>Your</option>
												<option>Options</option>
												<option>Here!</option>
											</select>
										</div>

									</div>
									<div class="control-group component" rel="popover" title="多选框" trigger="manual"
										data-content="
                      &lt;form class=&#39;form&#39;&gt;
                        &lt;div class=&#39;controls&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Label Text&lt;/label&gt; &lt;input class=&#39;input-large&#39; type=&#39;text&#39; name=&#39;label&#39; id=&#39;label&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Options: &lt;/label&gt;
                          &lt;textarea style=&#39;min-height: 200px&#39; id=&#39;option&#39;&gt;&lt;/textarea&gt;
                          &lt;hr/&gt;
                          &lt;button class=&#39;btn btn-info&#39;&gt;Save&lt;/button&gt;&lt;button class=&#39;btn btn-danger&#39;&gt;Cancel&lt;/button&gt;
                        &lt;/div&gt;
                      &lt;/form&gt;">

										<!-- Select Multiple -->
										<label class="control-label valtype" data-valtype="label">多选框</label>
										<div class="controls">
											<select class="input-xlarge valtype" multiple="multiple" data-valtype="option">
												<option>Enter</option>
												<option>Your</option>
												<option>Options</option>
												<option>Here!</option>
											</select>
										</div>
									</div>

								</div>

									
								<div class="tab-pane" id="2">
									<div class="control-group component" data-type="text" rel="popover" title="日期输入框" trigger="manual"
										data-content="
                      &lt;form class=&#39;form&#39;&gt;
                        &lt;div class=&#39;controls&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Label Text&lt;/label&gt; &lt;input class=&#39;input-large&#39; type=&#39;text&#39; name=&#39;label&#39; id=&#39;label&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Placeholder&lt;/label&gt; &lt;input type=&#39;text&#39; name=&#39;placeholder&#39; id=&#39;placeholder&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Help Text&lt;/label&gt; &lt;input type=&#39;text&#39; name=&#39;help&#39; id=&#39;help&#39;&gt;
                          &lt;hr/&gt;
                          &lt;button class=&#39;btn btn-info&#39;&gt;Save&lt;/button&gt;&lt;button class=&#39;btn btn-danger&#39;&gt;Cancel&lt;/button&gt;
                        &lt;/div&gt;
                      &lt;/form&gt;">

										<!-- Text input-->
										<label class="control-label valtype" for="input01" data-valtype="label">日期输入框</label>
										<div class="controls">
											<input type="date" placeholder="提示文字" class="input-xlarge valtype" data-valtype="placeholder">
											<p class="help-block valtype" data-valtype="help">帮助说明文字</p>
										</div>
									</div>
									<div class="control-group component" data-type="text" rel="popover" title="日期区间" trigger="manual"
										data-content="
                      &lt;form class=&#39;form&#39;&gt;
                        &lt;div class=&#39;controls&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Label Text&lt;/label&gt; &lt;input class=&#39;input-large&#39; type=&#39;text&#39; name=&#39;label&#39; id=&#39;label&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Placeholder&lt;/label&gt; &lt;input type=&#39;text&#39; name=&#39;placeholder&#39; id=&#39;placeholder&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Help Text&lt;/label&gt; &lt;input type=&#39;text&#39; name=&#39;help&#39; id=&#39;help&#39;&gt;
                          &lt;hr/&gt;
                          &lt;button class=&#39;btn btn-info&#39;&gt;Save&lt;/button&gt;&lt;button class=&#39;btn btn-danger&#39;&gt;Cancel&lt;/button&gt;
                        &lt;/div&gt;
                      &lt;/form&gt;">

										<!-- Text input-->
										<label class="control-label valtype" for="input01" data-valtype="label">日期区间</label>
										<div class="controls">
											开始日期:<input type="date" placeholder="提示文字" class="input-xlarge valtype" data-valtype="placeholder">
											<p class="help-block valtype" data-valtype="help"></p>
											结束日期:<input type="date" placeholder="提示文字" class="input-xlarge valtype" data-valtype="placeholder">
											<p class="help-block valtype" data-valtype="help"></p>
										</div>
									</div>
									<div class="control-group component" data-type="text" rel="popover" title="图片" trigger="manual"
										data-content="
                      &lt;form class=&#39;form&#39;&gt;
                        &lt;div class=&#39;controls&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Label Text&lt;/label&gt; &lt;input class=&#39;input-large&#39; type=&#39;text&#39; name=&#39;label&#39; id=&#39;label&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Placeholder&lt;/label&gt; &lt;input type=&#39;text&#39; name=&#39;placeholder&#39; id=&#39;placeholder&#39;&gt;
                          &lt;label class=&#39;control-label&#39;&gt;Help Text&lt;/label&gt; &lt;input type=&#39;text&#39; name=&#39;help&#39; id=&#39;help&#39;&gt;
                          &lt;hr/&gt;
                          &lt;button class=&#39;btn btn-info&#39;&gt;Save&lt;/button&gt;&lt;button class=&#39;btn btn-danger&#39;&gt;Cancel&lt;/button&gt;
                        &lt;/div&gt;
                      &lt;/form&gt;">

										<!-- Text input-->
										<label class="control-label valtype" for="input01" data-valtype="label">图片</label>
										<div class="controls">
											<img src="public/flow/files/see.png">
											<p class="help-block valtype" data-valtype="help">帮助说明文字</p>
										</div>
									</div>
								<div class="control-group component" rel="popover" title="File Upload" trigger="manual" data-content="
								                      &lt;form class=&#39;form&#39;&gt;
								                        &lt;div class=&#39;controls&#39;&gt;
								                          &lt;label class=&#39;control-label&#39;&gt;Label Text&lt;/label&gt; &lt;input class=&#39;input-large&#39; type=&#39;text&#39; name=&#39;label&#39; id=&#39;label&#39;&gt;
								                          &lt;hr/&gt;
								                          &lt;button class=&#39;btn btn-info&#39;&gt;Save&lt;/button&gt;&lt;button class=&#39;btn btn-danger&#39;&gt;Cancel&lt;/button&gt;
								                        &lt;/div&gt;
								                      &lt;/form&gt;">
																		<label class="control-label valtype" data-valtype="label">File Button</label>

										<!-- File Upload -->
										<div class="controls">
											<input class="input-file" id="fileInput" type="file">
										</div>
									</div>
								</div>
									


								<div class="tab-pane" id="5">
									<textarea id="source" class="span6"></textarea>
								</div>
							</div>
						</fieldset>
					</form>
				</div>
			</div>
			<!-- row -->
			<div class="row clearfix">
				<div class="span12">
					<hr>
					By Adam Moore (<a href="http://twitter.com/minikomi">@minikomi</a>).<br> Source on (<a href="http://github.com/minikomi">Github</a>).
					<p>
						Bootstrap 表单构造器由<a href="http://www.bootcss.com/">Bootstrap中文网</a>翻译整理
					</p>
				</div>
			</div>
		</div>
		<!-- /container -->
		<script src="public/flow/files/jquery.min.js"></script>
		<script src="public/flow/files/bootstrap.min.js"></script>

		<script src="public/flow/files/fb.js"></script>

		<script src="public/flow/files/projects.js"></script>
		<script src="public/flow/files/h.js" type="text/javascript"></script>


	</div>
	<div id="site-navbar" style="position: absolute; top: -4px; left: -3px; border: 0; z-index: 2000; padding: 0; margin: 0;">
		<a href="http://www.bootcss.com/" title="返回首页" style="background: none;"><img src="public/flow/files/return-back.png" style="padding: 0; margin: 0; border: 0; -webkit-box-shadow: none; -moz-box-shadow: none; box-shadow: none;"></a>
	</div>
</body>
</html>