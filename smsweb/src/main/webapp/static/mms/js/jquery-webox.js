/**
 *
 *	Plugin: Jquery.webox
 *	Developer: Blank
 *	Version: 1.0 Beta
 *	Update: 2012.07.08
 *
**/
$.extend({
	box:function(option){
		var _x,_y,m,allscreen=false;
		if(!option){
			alert('options can\'t be empty');
			return;
		};
		if(!option['html']&&!option['iframe']){
			alert('html attribute and iframe attribute can\'t be both empty');
			return;
		};
		option['parent']='box';
		option['locked']='locked';
		$(document).ready(function(e){
			$('.webox').remove();
			$('.background').remove();
			var width=option['width']?option['width']:450;
			var height=option['height']?option['height']:150;
			$('body').append('<div class="background" style="display:none;"></div><div class="webox" style="width:'+width+'px;height:'+height+'px;display:none;"><div class="box" style="width:'+(width)+'px;height:'+(height-2)+'px;"><div id="inside" style="height:'+(height-2)+'px;"><h1 id="locked" onselectstart="return false;">'+(option['title']?option['title']:'webox')+'<a class="span" href="javascript:void(0);">× 关闭</a></h1>'+(option['iframe']?'<iframe class="w_iframe" src="'+option['iframe']+'" frameborder="0" height="93%" width="100%" scrolling="auto" style="border:none;overflow-x:hidden; background-color:none;"></iframe>':option['html']?option['html']:'')+'</div></div></div>');
			if(navigator.userAgent.indexOf('MSIE 7')>0||navigator.userAgent.indexOf('MSIE 8')>0){
				$('.webox').css({'filter':'progid:DXImageTransform.Microsoft.gradient(startColorstr=#ffffff,endColorstr=#ffffff)'});
			}if(option['bgvisibel']){
				$('.background').fadeTo('slow',0);
			};
			$('.webox').css({display:'block'});
			$('#'+option['locked']+' .span').click(function(){
				$('.webox').css({display:'none'});
				$('.box').css({display:'none'});
				$('.background').css({display:'none'});
			});
			var marginLeft=parseInt(width/2);
			var marginTop=parseInt(height/2);
			var winWidth=parseInt($(window).width()/2);
			var winHeight=parseInt($(window).height()/2.2);
			var left=winWidth-marginLeft;
			var top=winHeight-marginTop+25;
			$('.webox').css({left:left,top:top});
			$('#'+option['locked']).mousedown(function(e){
				if(e.which){
					m=true;
					_x=e.pageX-parseInt($('.webox').css('left'));
					_y=e.pageY-parseInt($('.webox').css('top'));
				}
				}).dblclick(function(){
					if(allscreen){
						$('.webox').css({height:height,width:width});
						$('.box').css({height:height-8,width:width-8});
						$('#inside').height(height-8);
						$('.w_iframe').height(height-30);
						$('.webox').css({left:left,top:top});
						allscreen = false;
					}else{
						allscreen=true;
						var screenHeight = $(window).height();
						var screenWidth = $(window).width();
						$('.webox').css({'width':screenWidth-18,'height':screenHeight-18,'top':'0px','left':'0px'});
						$('.box').css({'width':screenWidth-18,'height':screenHeight-18,'top':'0px','left':'0px'});
						$('#inside').height(screenHeight-28);
						$('.w_iframe').height(screenHeight-50);
					}
				});
			}).mousemove(function(e){
				if(m && !allscreen){
					var x=e.pageX-_x;
					var y=e.pageY-_y;
					$('.webox').css({left:x});
					$('.webox').css({top:y});
				}
			}).mouseup(function(){
				m=false;
			});
			$(window).resize(function(){
				if(allscreen){
					var screenHeight = $(window).height();
					var screenWidth = $(window).width();
					$('.webox').css({'width':screenWidth-18,'height':screenHeight-18,'top':'0px','left':'0px'});
					$('#inside').height(screenHeight-20);
					$('.w_iframe').height(screenHeight-50);
				}
			});

			
	}
});

 // 关闭方法
function closeDialog(){ 
		$('.webox').css({display:'none'});
		$('.box').css({display:'none'});
		$('.background').css({display:'none'});
}