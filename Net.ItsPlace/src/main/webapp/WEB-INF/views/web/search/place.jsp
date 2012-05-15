<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form"  %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"  %>
<%@ taglib prefix="sec"    uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript" src="http://apis.daum.net/maps/maps3.js?apikey=3cc715fbd2c405578092bdae6c2a3a6867790d9f" charset="utf-8"></script>
<script type="text/javascript">
menuSelected("가맹점검색", "주변검색");
var mapImage = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"];
var PLACE = ${list};
var map;
var lat = "37.54428363237073";
var lng = "127.0205732625485";

function zoomIn() {
	map.setLevel(map.getLevel() - 3);
}

function zoomOut() {
	map.setLevel(map.getLevel() + 1);
}

function jsCallAndroid() {
	alert(msg);
}

function successCallback(position) {
	var lat = position.coords.latitude;
	var lng = position.coords.longitude;
	map = new daum.maps.Map(document.getElementById('map'), {
		center : new daum.maps.LatLng(lat, lng)
	});

	var marker = new daum.maps.Marker({
		position : new daum.maps.LatLng(lat, lng)
	});

	marker.setMap(map);
}

function errorCallback(error) {
	alert("map failed: " + error.message);
}

/*지도 중심점 바로 이동하기*/
function daummap_setCenter(lat, lng) {
	map.setCenter(new daum.maps.LatLng(lat, lng));

	var marker = new daum.maps.Marker({
		position : new daum.maps.LatLng(lat.lng)
	});

	marker.setMap(map);
}

var marker = new Array();
var infowindow = new Array();
var l, l2;
function init() {
	var div = $("#place_list");
	// 지도 생성
	map = new daum.maps.Map(document.getElementById('map'), {
		center : new daum.maps.LatLng(lat, lng)
	});
	var html = "";
 	for(var i=0; i<PLACE.length; i++)
	{
		var lat = PLACE[i].latitude;
		var lng = PLACE[i].longitude;
		var place_name   = PLACE[i].fname;
		var place_phone  = PLACE[i].phone1;
		var place_remark = PLACE[i].remark;
		var img = "http://localhost:8080/resources/images/marker/" + mapImage[i] + ".png";
		console.log(img);
		var icon = new daum.maps.MarkerImage(
			img,
			new daum.maps.Size(32, 32),
			new daum.maps.Point(16,34),
			"poly"
		);
		//마크먼저 배열에 담는다.
		marker[i] = new daum.maps.Marker({
			position : new daum.maps.LatLng(lat, lng),
			image : icon
		});

		// 마커 표시
		marker[i].setMap(map);
		
		// 툴팁
		//marker[i].setTitle(place_name);
		// 좌표로 이동
		map.setCenter(new daum.maps.LatLng(lat, lng));
		
		var script = '<img src="/resources/images/marker/' + mapImage[i] + '.png"/><a href=\'javascript:placeDisplay("' + i + '");void(0);\'>' + place_name + '</a>';
		html += 	"<div class='box'>" +
					"	<span onmouseover='placeTooltip(\"" + i + "\")'>" + script + "</span>" +
					"</div>" +
					"<div class='box'>" +
					"	<ul>" +
					"		<li>" + place_phone + "</li>" +
					"		<li>주소가 들어와야함</li>" +
					"		<li>" + place_remark + "</li>" +
					"	</ul>" +
					"</div>";
	}
	
 	for(var i=0; i<PLACE.length; i++)
	{
 		var fname = PLACE[i].fname;
 		marker[i].index = i;
 		// 메시지 박스 
 		//인포윈도우 배열에 담는다.
		infowindow[i] = new daum.maps.InfoWindow({
			content: '<p style="margin:7px 22px 7px 12px;font:12px/1.5 sans-serif"><strong>안녕하세요~</strong><br/>' + fname + '.</p>',
			removable : true
		});
	
		daum.maps.event.addListener(marker[i], "click", function() {
			for(var j=0; j<marker.length; j++)
			{
				infowindow[j].close();
			}
			infowindow[this.index].open(map, marker[this.index]);
		});
		
		daum.maps.event.addListener(marker[i], "mouseover", function() {  
		});
	}
 	
 	div.append(html);	
}

function init3() {
	var p = new daum.maps.LatLng(lat, lng);
	var rc = new daum.maps.RoadviewClient();
	var rv = new daum.maps.Roadview(document.getElementById("roadview"));

	rc.getNearestPanoId(p, 100, function(panoid) {
		rv.setPanoId(panoid, p);
	});
}
function placeDisplay(idx)
{
	for(var j=0; j<marker.length; j++)
	{
		infowindow[j].close();
	}
	infowindow[idx].open(map, marker[idx]);
}

function placeTooltip(idx)
{

}

$(document).ready(function(){
	init();	
});	
</script>
<style type="text/css">
input[type="text"], input[type="password"], textarea{
	border:1px solid #c8c8c8;
	background-color:#e4e4e4;
	margin-bottom:5px;
	height:20px;
	padding:5px 4px;
	margin:0 0 3px;
	border-radius:4px;
	-moz-border-radius:4px;
	-webkit-border-radius:4px;
}
.box span.s{
	display: block;
	float: left;
}
.box span.s.select{
	padding-top:5px;
}
.box span.s.pl5{
	padding-left:5px;
}
.box span.s p{
	overflow: hidden;	
}
label.left{display: inline;}
span.label{font-weight: bold;}

html,body,#map {
	margin: 0;
	padding: 0;
	width: 100%;
	height: 100%
}

#btnZoomIn,#btnZoomOut {
	position: absolute;
	right: 1em;
	z-index: 1;
	padding: 10px;
	-webkit-border-radius: 0.5em;
	border: 1px solid #aaa;
	background: -webkit-gradient(linear, left top, left bottom, from(#EFEFF0),to(#BCBEC1) )
}

#roadview {
	position: absolute;
	top: 1em;
	right: 10em;
	z-index: 1;
}

#btnZoomIn {top: 1em;}
#btnZoomOut {top: 4em;}
</style>
<div class="container">
	<section id="middle">
		<div class="middle_inner">
			<div class="headline">
				<h3>가맹점검색</h3>
			</div>
			<div class="cont_nav">
				<a href="index.html">Home</a>&nbsp; /&nbsp;<a href="shortcodes.html">가맹점검색</a>&nbsp; /&nbsp;주변검색
			</div>
			<section id="middle_content">
				<div class="entry">
					<div class="form_info cmsms_input">
						<input type="text" title="example@gmail.com" name="email" id="email" style="width:90%;" class="validate[required,custom[email]]" />
						<a href="#" class="button" style="background-color:#696969;"><span>검색</span></a>
					</div>
				</div>
				<div>
					<div style="width:25%;float: left;">
						<div class="box">
							<span class="s select">
								<select name="" id="">
									<option value="">선택</option>
									<option value="">선택</option>
									<option value="">선택</option>
								</select>
							</span>
							<span class="s">
								<input type="text" title="example@gmail.com" name="email" id="email" style="width:140px;" class="validate[required,custom[email]]" />
								<a href="#" class="button" style="background-color:#696969;"><span>검색</span></a>
							</span>
						</div>
						<div class="box">
							<span class="s pl5">
								<a href="#" class="button_medium" style="width:65px;background-color:#3c7bc9;"><span>커피숍</span></a>
							</span>
							<span class="s pl5">
								<a href="#" class="button_medium" style="width:55px;background-color:#3c7bc9;"><span>한식</span></a>
							</span>
							<span class="s pl5">
								<a href="#" class="button_medium" style="width:55px;background-color:#3c7bc9;"><span>중식</span></a>
							</span>
						</div>
						<div class="box">
							<span class="s pl5">
								<a href="#" class="button_medium" style="width:65px;background-color:#3c7bc9;"><span>커피숍</span></a>
							</span>
							<span class="s pl5">
								<a href="#" class="button_medium" style="width:55px;background-color:#3c7bc9;"><span>한식</span></a>
							</span>
							<span class="s pl5">
								<a href="#" class="button_medium" style="width:55px;background-color:#3c7bc9;"><span>중식</span></a>
							</span>
						</div>
						<div class="box">
							<span class="s" style="border-bottom:2px solid #bbb;width:100%;"></span>
						</div>
						<div id="place_list" style="padding-top:10px;clear:both;"></div>
					</div>
					<div style="width:75%;height:700px;float: left;" id="map"></div>
				</div>
				<div id="roadview" style="width: 600px; height: 400px;"></div>
				<!-- <span id="btnZoomIn" onclick="zoomIn()">확대</span>
				<span id="btnZoomOut" onclick="zoomOut()">축소</span>
				<img id="roadview" src="../resources/images/roadview.png" onclick="init3()" /> -->
			</section>
		</div>
	</section>
</div>
			