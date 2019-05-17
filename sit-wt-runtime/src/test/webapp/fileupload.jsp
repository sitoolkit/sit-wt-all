<!--
Copyright 2013 Monocrea Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
<%@page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
	<head>
		<title>ファイルアップロード</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<link rel="stylesheet" href="style.css"/>
	</head>
	<body>
		<div id="container">
			<div id="content">
				<h1>ファイルアップロード</h1>
				<p>
				</p>
				<form action="/fileupload" method="post" enctype="multipart/form-data">
					<table class="input-form" style="width:100%;">
						<col style="width:30%;"/>
						<col style="width:70%;"/>
						<tbody>
							<tr>
								<th>ファイル</th>
								<td><input id="file" name="file" type="file"/></td>
							</tr>
							<tr>
								<th>ファイルの内容</th>
								<td id="fileContent">${requestScope.content}</td>
							</tr>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="2">
									<input id="upload" type="submit" value="アップロード"/>
								</td>
							</tr>
						</tfoot>
					</table>
				</form>
			</div>
			<div id="footer">
				Copyright 2013 Monocrea Inc.
			</div>
		</div>
	</body>
</html>
