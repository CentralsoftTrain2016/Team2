<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<jsp:useBean id="message" class="java.lang.String" scope="request" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>機雷掃除人 キャラクターセレクト</title>
<script type="text/JavaScript">
	var set = 0;

	//サブミットボタンを1度押したら2度目以降は押せないようにする
	function double() {
		if (set == 0) {
			set = 1;
		} else {
			return false;
		}
	}
</script>
</head>
<body>
<form  method="POST" action="CharacterChoiceServlet">
<table border=0 style="width:720px;text-align:center">
 <tr ><td colspan=3 style="font-size:300%; color: #000000; font-family: 'ＭＳ 明朝';"> －CHARACTER SELECT－</td>  </tr>
 <tr ><td ><input type="radio" name="character" value="1"></td>
 <td ><input type="radio" name="character" value="2"></td>
 <td ><input type="radio" name="character" value="3"></td></tr>
 <tr>
 <td style="mergin-left:150px"><img src="charImg/samon_select.png" alt="仮" width="150px" height="150px"></td>
 <td style="mergin-left:150px"><img src="charImg/sasaki_select.png" alt="仮" width="150px" height="150px"></td>
 <td style="mergin-left:150px"><img src="charImg/kasano_select.png" alt="仮" width="150px" height="150px"></td>
 </tr>
 <tr >
 <td style=" width:150px;height : 100px; padding: 10px;  border: 5px double #333333;">
 	サモン </td>
 <td style=" width:150px;height : 100px; padding: 10px;  border: 5px double #333333;">
 	佐々木 </td>
 <td style=" width:150px;height : 100px; padding: 10px;  border: 5px double #333333;">
 	笠野 </td></tr>
<tr ></tr>
<tr > <td colspan=3>

<input type="submit" value="決定" onClick="return double()" style="width:240px; height:120px; font-size:80px;">
<%=message %>

 </td>
</tr>
</table>
</form>
</body>
</html>