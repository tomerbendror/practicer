var base_url='http://www.almagu5.com/webreader.audio/';
var ttsUrl='http://www.almagu5.com/webreader?callback=?';
var introText='click to play sound';
var popup_imgs = [
        base_url+'/images/button1.png',
        base_url+'/images/button2.png',
        base_url+'/images/button3.png'
]                               
var icons= {
        'loading' :  'images/loader.gif' ,
        'ready' : 'images/play.png' ,        
        'play' : 'images/play.png' ,        
        'pause' :  'images/pause.png',
        'play_ico' : 'images/ico.png',
        'settings' : 'images/settings.png',
        'arrow' : 'images/arrow.png',
        'volume' : 'images/volume.png'        
}                                                     
var cid = 'CID';
var hovering=false;
var readAll_mode=false;
var soundFinished=false;      
var widgetScript;
document.createElement("almawebreader");  
var orders=new Array();
(function() {   
//Load jQuery 
var jQuery;
var js=['jquery.min.js','soundmanager2.js','inlineplayer.js'];
var jsLoaded=0;
var widgetScript;
var controls;
var inlinePlayer;
var playerLink;
var soundManagerReady=false;
var playing = false;
var isSoundWaiting=false;
var audio_activated;
var span_class='reader_span';
var content_class='#ctlContent *,.ListArticle1of2 *';
var exclude_tag='.readmore a,.ArticlesListDate,.ArticleMainDate,.navigationTab,.navigationTab *,.hidden';

/******** Load  js files *********/
for(var i =0 ; i< js.length;i++){
    addScriptTag(js[i]);
}
/******** Called  when a js file has loaded ******/
function scriptLoadHandler() {
    jsLoaded++;      
    if(jsLoaded==js.length){
        jQuery = window.jQuery.noConflict(true);
        // Call our main function
        main();     
    }        
}

/******** Our main function ********/
function main() { 
    jQuery(document).ready(function($) {             
        if (false  && parseInt($.browser.version, 10) <= 7) return false;
        widgetScript=$('#widgetScript'); 
        audio_activated = (getCookie('AUDIO_ACTIVATED')=='true') ? true : false;
        var ldr =$('<img>').attr('src',base_url+icons['loading']).addClass('ldr').css('width','60px');
        widgetScript.after(ldr);
        /******* Load HTML *******/                                                          
        $.getJSON('http://www.almagu5.com/test/request.aspx?callback=?','task=getHtmlContent&url='+base_url,function(res){               
                ldr.remove();
                $('.header_panel tbody tr:first').prepend('<td>'+res.htmlContent+'</td>');               
                checkCompatMode();
                init($);
        });                                  
        
    });
}

var hoverTimeout;
function init($) {
    var tagsToRead='a,.img_reader,.'+span_class;                                                                        
    setControls($);
    render_Popup();        
    var container=$('<div>').addClass('reader_widget_container');                
    $('.player_container').wrap(container);               
    $('.reader_widget_container').prepend(controls['playIco']);                     
     $('.player_container').css('display','none');
    var order=0;                      
     $(content_class).not(exclude_tag).contents().filter(function() { 
	     var isText = (this.nodeType == 3) && this.nodeValue.match(/\S/) && !( $(this).is('style') || $(this).parent().is('style') || $(this).parent().is('option') ) && !( $(this).is('script') || $(this).parent().is('script') ) ;         
        return isText; 
     }).wrap('<span class="'+span_class+'"  />');
     $(content_class).not(exclude_tag).contents().filter(function() {       
        var isImg = $(this).is('img');
        return  isImg; 
     }).addClass('img_reader');
     
     $('.'+span_class).each(function(){         
         if($(this).attr('data-order')==null){
             order++;             
             var siblings=$(this).siblings('.'+span_class)
              if(siblings.size()>0){
                  siblings.attr('data-order',order);
              }
              siblings=$(this).siblings().find('.'+span_class);
              if(siblings.size()>0){
                  siblings.attr('data-order',order);
              }
              $(this).attr('data-order',order);             
         } 

     });     
     $('.'+span_class).each(function(){
         var order=parseInt($(this).attr('data-order'));
         if($.inArray(order,orders) ==-1){             
            orders.push(order);               
         }        
     });
     
    $('.play_ico').click(function(){                        
        $('.player_container').toggle('fast');
    });    
    
    $('.settings_ico').click(function(){
        $('.settings').slideToggle('fast');
    });
    
    $('.settings_item').click(function(){
        $(this).parent('div').find('.settings_on').removeClass('settings_on');
        $(this).addClass('settings_on');
    })
     $('#slider').slider({            
            animate : true,
            range: "min",
            max: 100,
            value: 70,
            slide: function(e, ui) {                             
                if(typeof inlinePlayer !== 'undefined'){
                    var s = inlinePlayer.lastSound;                    
                    if(typeof s !== 'undefined' && s!=null)
                        soundManager.setVolume(s.id,ui.value);                    
                }
            }
    });    
    $('.voice_option,.rate_option').click(function(){                      
          var params= $('#player').attr('href').split('_');
          var href = params[0]+'_'+params[1]+getVoiceRateParam()+'.mp3';
          $('#player').attr('href',href);               
          inlinePlayer.init();                
          if(readAll_mode){
            var s =inlinePlayer.lastSound;                                            
            s.play();    
          }
          
      });                                               
    $(document).bind('mousemove', function(e){
        $('#fly_speaker').css({
           left:  e.pageX + 20,
           top:   e.pageY
        });
    });              
     $(tagsToRead).hover(function(){
         tagsToReadMouseover(this,false);
     },
     function(){         
         tagsToReadMouseout(this,false);
     });                    
     
    initPlayer($);                          
    
    var firstVisit = getCookie('FIRST_VISIT');
     if(firstVisit==null){
            setCookie('FIRST_VISIT', 'false');
            if( ! (/Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent))){
                getHash(introText,false,renderPlayer,{'autoPlay' : true});
            }
     }                                                          
     
     if(audio_activated){
        //$('.slider-frame').trigger('click');        
    }
}

function setControls($){
    controls = {
        'playIco' : $('<img>').addClass('play_ico').attr({'src' : base_url+icons['play_ico'] ,  width : '32px' , height : '32px'}),
        'play' : $('<img>').attr('src',base_url+icons['play']),
        'volume' : $('<img>').attr('src',base_url+icons['volume']),
        'fly_speaker' : $('<img>').attr({'src' : base_url+icons['volume'] , 'id' :'fly_speaker' }),
        'settings' : $('<img>').attr('src',base_url+icons['settings']),
        'arrow' : $('<img>').attr('src',base_url+icons['arrow'])        
    };
    playerLink = $('<a disabled></a>').attr({'id':'player','href':'#'});                                    
    playerLink.append(controls['play']);  
    $('body').append(controls['fly_speaker']);
    $('.play.btn').append(playerLink);
    $('.volume.btn').append(controls['volume']);
    $('.settings_ico.btn').append(controls['settings']);
    $('.settings_item.btn').prepend(controls['arrow']);
    $('.settings_label').append(controls['arrow']);
    
}

function initPlayer($){      
    window.SM2_DEFER = true
    soundManager.setup({  
      debugMode: false,
      waitForWindowLoad : false,
      wmode : 'transparent',      
      useFlashBlock : true, 
      allowScriptAccess : 'always', 
      useHighPerformance : false,     
      flashLoadTimeout : 0,   
      preferFlash: true,
      useFlashBlock: true,  
      // path to directory containing SM2 SWF
      url: base_url+'swf/',
      // optional: enable MPEG-4/AAC support (requires flash 9)
      flashVersion: 9  
});    
    
    soundManager.onready(function($) {
        soundManagerReady=true; 
        inlinePlayer = new InlinePlayer();  
        inlinePlayer.config.autoPlay = true;
        inlinePlayer.init();          
        if(!isSoundWaiting){            
            soundManager.stopAll();                   
        }else{
            var s =inlinePlayer.lastSound;                                            
            s.play();
        }        
        play_handle();
        if(audio_activated){                            
              playerLink.trigger('click');              
        }
    });
    
}

function addScriptTag(src){
    var script_tag = document.createElement('script');
    script_tag.setAttribute("type","text/javascript");
    script_tag.setAttribute("src",base_url+'script/'+src);
    if (script_tag.readyState) {
      script_tag.onreadystatechange = function () { // For old versions of IE
          if (this.readyState == 'complete' || this.readyState == 'loaded') {              
              scriptLoadHandler();              
          }
      };
    } 
    else {
      script_tag.onload = scriptLoadHandler;
    }
    // Try to find the head, otherwise default to the documentElement
    (document.getElementsByTagName("head")[0] || document.documentElement).appendChild(script_tag);
}

function showFlySpeaker(e){
    $('#fly_speaker').show();            
}

function hideFlySpeaker(){
    $('#fly_speaker').hide();    
}
var finishedInt;
function play_handle(){  
    var $ = jQuery;
    var index=0;           
    playerLink.click(function(e){                        
         e.stopPropagation();             
         e.preventDefault();                             
         index=0;
         if(readAll_mode){             
             window.clearInterval(finishedInt);
             var cur = $('.last_hovered');
             tagsToReadMouseout(cur,true);                                   
             $('.player_container').css('position','absolute');
             var offset=$('.play_ico').offset();
             $('.player_container').css({'top' : '0px' , 'right' : '30px'});
         }
         else{             
             if(!audio_activated){
                 //$('.slider-frame').trigger('click');
             }             
             tagsToReadMouseover($('.'+span_class+':first'),true);                                   
             $('.player_container').css('position','fixed')
             $('.player_container').css({'right' : '5px' , 'top' : '5px'});
             $('.player_container').show('fast');
             soundFinished=false;                 
             finishedInt=setInterval(function(){
                    if(soundFinished){
                        var cur = $('.last_hovered');                          
                        var getOrder=function(){	
    					  index++;    					  
    					  while(index < orders.length){
    					  	var nxt=$('.'+span_class+'[data-order="'+(orders[index])+'"]');	
	    					  if(nxt.size()>0 && !nxt.is('img'))
	    					  	return orders[index];
	    					index++;  	
    					  }	                    	
	                      return null;  
                        };                                              
                        var order=getOrder();    
                        var nxt=$('.'+span_class+'[data-order="'+(order)+'"]');
                        tagsToReadMouseout(cur,true);
                        if(nxt.size()>0){                            
                            tagsToReadMouseover(nxt,true);            
                            soundFinished=false;                                  
                            
                        }else{
                            $('.player_container').css('position','absolute');
                            var offset=$('.play_ico').offset();
                            $('.player_container').css({'top' : '0px' , 'right' : '30px'});
                            window.clearInterval(finishedInt);
                        }                                        
                    }
             },1000);  
         }
         readAll_mode=!readAll_mode;
         return false;
    });
  
}

//request PhraseHash from tts server  and render the player
function getHash(txt,preferredOnly,callback,params){    
    //removing extra space
    txt = txt.replace(/\s{2,}/g, ' ');            
    if($.trim(txt)=='') return false;
    var txtEncoded = encodeURIComponent(txt);
    var txtAscii = hex2a(txtEncoded);
    var txtEncodedLen =txtEncoded.length;
    var txtLen = txt.length;                      
    var txtAsciiLen = txtAscii.length;
    var asciiMaxLen=700;            
    //console.log('ascii => len : '+txtAsciiLen);                                
    loadingAnim(true,preferredOnly);
    if(txtAsciiLen > asciiMaxLen){ 
        //console.log('long text');
        var ss=guid();                  
        var FragmentLen = (txtLen * asciiMaxLen) / txtAsciiLen;
        var totalFragments = Math.ceil( txtLen / FragmentLen );                
        //console.log('ascii => len : '+txtAsciiLen +' total : '+ totalFragments +' frag : '+ FragmentLen);                                
        for (var i=0; i <totalFragments ; i++){                    
            var from = i*FragmentLen;
            var to = (i+1) * FragmentLen;                                
            var fragment = txt.substring(from,to);                                                      
            var fragmentEncoded = encodeURIComponent(fragment);                                                    
            var sequence =i+1;                    
            var args =  { 'cid' : cid , 'ss' : ss , 'sq' : sequence , 'ct' : totalFragments , 'markup' : fragment , 'preferredOnly' : preferredOnly};                                
            var hex = hex2a(fragmentEncoded);            
            if(totalFragments == sequence){
                //console.log('last sequence sent');
                $.getJSON('http://www.almagu5.com/webreader?callback=?',args,function(response){
                    //console.log('last sequence received');
                    callback(response,params);
                });
            }
            else{
               $.getJSON('http://www.almagu5.com/webreader?callback=?',args);  
            }                    
        }                
    }
    else{
        //console.log('short txt');
        var args =  { 'cid' : cid , 'markup' : txt,'preferredOnly' : preferredOnly};
        $.getJSON('http://www.almagu5.com/webreader?callback=?',args, function (response) {
            callback(response,params);
        });                                
    }
}
// render guild-like code
function S4() {
   return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
}
function guid() {
   return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}
// get ascii code from hex
function hex2a(hex) {
    var str = '';
    for (var i = 0; i < hex.length; i += 2)
        str += String.fromCharCode(parseInt(hex.substr(i, 2), 16));
    return str;
}
// loading animation
function loadingAnim(start,preferredOnly){
   
}
// render the player
function renderPlayer(response,params){    
    //console.log(response);
    // Server is not sending proper content - type , so a hack
    if(typeof response !== 'object') {
      response = $.parseJSON(response);
    }
    // If response status is 0, 
    if(!response.Status) {                              
      playerLink.attr('href',getTTSUrlParam(response.PhraseHash));
      if(soundManagerReady){          
           if(params.hasOwnProperty('hovering') && !hovering) return false;
            inlinePlayer.init();              
            if( params.hasOwnProperty('autoPlay') && params.autoPlay==true){                                         
                var s =inlinePlayer.lastSound;
                s.stop();
                soundManager.stopAll();                          
                s.play();                
            }            
      }else{          
          isSoundWaiting=true;
      }            
    }else{
        window.console && console.log('Webreader : An error has occured, please try again : '+response.Message);                
    }
}
// get the iframe  according to  the given params of the player {size , voice }
function getTTSUrlParam(PhraseHash){      
  var base_mp3Url = 'http://www.almagu5.com/webreader/audio/';        
  var url = base_mp3Url + cid + '_' + PhraseHash + getVoiceRateParam()+ '.mp3';       
  return url;
}

function getVoiceRateParam(){
      var rate = $('.rate_option.settings_on').attr('data-value');
      var voice = $('.voice_option.settings_on').attr('data-value');
      return '_'+rate+'_'+voice;      
  }
//handle user text selection event 
var timer;
var timeout=300;
var selTxt;
function textSelection_handle(){
    var mouseUpHandler;

    if (typeof window.getSelection != "undefined") {
        // Non-IE    
        //console.log(' Non-IE ');
        mouseUpHandler = function(event) {    
            hideSelectionPlayer();
            timer =setTimeout(function(){            
                var sel = window.getSelection();        
                if (sel.rangeCount > 0) {
                    var range = sel.getRangeAt(0);
                    if (range.toString()) {                                  
                        var txt=$.trim(range.toString());                                                    
                        onTextSelection(txt,event);                
                    }
                    clearTimeout(timer);
                    timer = null;
                }
            },timeout);
        };
    } 
    else if (typeof document.selection != "undefined") {
        // IE        
        mouseUpHandler = function(event) {
            hideSelectionPlayer()
            event = event || window.event;        
            //hack for ie 
            var eventCopy = {};
            for (var i in event) {
                eventCopy[i] = event[i];    
            }
            timer =setTimeout(function(){            
                var sel = document.selection;        
                if (sel.type == "Text") {
                    var textRange = sel.createRange();
                    if (textRange.text != "") {                
                        var txt = $.trim(textRange.text)                                    
                        onTextSelection(txt,eventCopy);
                    }
                }
            },timeout)
        };
    }

    document.onmouseup = mouseUpHandler;
};
//on text selection event
function onTextSelection(txt,e){    
    txt= $.trim(txt);
    if(txt=='' || txt==selTxt){
        return false;  
    }else{        
        selTxt=txt;
    } 
    e = $.event.fix(e);        
    getHash(txt,true,renderSelectionPlayer);        
    var d =$('#selection_player');
    var x = e.pageX ;
    var y =e.pageY ;   
    
    d.css('top',(y)+'px');
    d.css('left',(x+30) +'px');
};
// get the small player above the selected text
function renderSelectionPlayer(response){      
    // Server is not sending proper content - type , so a hack
    if(typeof response !== 'object') {
      response = $.parseJSON(response);
    }
    
    // If response status is 0, 
    if(!response.Status) {                              
      var playOnHover = 'false';
      var rate = response.PreferredSpeed;
      var voice = response.PreferredVoice
      var hash =response.PhraseHash;
      var urlParam = hash+'_'+rate+'_'+voice+'&'+playOnHover;      
      var iframe = $('#selection_player').find('iframe');
      iframe.attr({ 'src' : base_url+'smallplayer.html?'+urlParam});              
      //console.log('iframe created');            
    }else{
        window.console && console.log('Webreader : An error has occured, please try again : '+response.Message);        
        hideSelectionPlayer();
    }    
};
//hide the small player above the selected text
function hideSelectionPlayer(){
    var iframe=$('#selection_player').find('iframe');
    if(iframe.size()>0){
        iframe.hide();        
    } 
}
//render the bottom miniature&popup
function render_Popup(){        
    var miniature_switcher =$('<div>').addClass('slider-frame').html('<span class="slider-button">OFF</span>');                                                    
                                    
    var miniature_volume=$('<img>').attr({'src' : base_url+icons['volume'] });
                                        
    var miniature=$('<div>').addClass('reader_miniature');
    
    miniature.append(miniature_volume).append(miniature_switcher);
                                                                                                                          
   
    $('body').append(miniature);
   
		
		if (getCookie('AUDIO_ACTIVATED') == 'true') {
			$('.slider-frame').find('.slider-button').addClass('on').html('On');        
		}
		
		$('.slider-frame').click(function(){
			if (audio_activated) {
				$(this).find('.slider-button').removeClass('on').html('Off');
				setCookie('AUDIO_ACTIVATED','false');
				audio_activated = false;
				
				if(readAll_mode){            
					playerLink.trigger('click');
				}
			}
			else {
				$(this).find('.slider-button').addClass('on').html('On');        
				setCookie('AUDIO_ACTIVATED','true');
				audio_activated = true;
			}
		});
	
   /*
    $('.slider-frame').toggle(function(){
        $(this).find('.slider-button').addClass('on').html('On');        
        setCookie('AUDIO_ACTIVATED','true');
        audio_activated = true;
    }
        ,function(){
        $(this).find('.slider-button').removeClass('on').html('Off');
        setCookie('AUDIO_ACTIVATED','false');        
        if(readAll_mode){            
            playerLink.trigger('click');
        }
        audio_activated = false;        
    });*/
	
                                                          
  /* $.getScript(base_url+'/script/cookies.js',function(){
       var nbShown = $.cookie('player_popup_shown');
       var img = $('<img>');
       var callback;
       if(nbShown==null){
           $.cookie('player_popup_shown', '1' , { path: '/' });      
           img.attr('src',popup_imgs[0]);                                 
           callback = function(t) {
                t.animate({'bottom' : '0px'},1000);                   
           }
       }else{
           nbShown = parseInt(nbShown);
           if(nbShown <3){
               nbShown++;
               $.cookie('player_popup_shown', nbShown,{ path: '/' });                              
               img.attr('src',popup_imgs[nbShown-1]);                      
               callback = function(t) {
                    t.animate({'bottom' : '0px'},1000);                   
                }
           }else{               
               var rnd = Math.floor(Math.random() * 3) + 1
               img.attr('src',popup_imgs[rnd-1]);                      
               callback=function(t){
                    miniature.show();   
               }               
           }           
       }
       popup.append(img);
       img.load(function(){
           callback(popup);
       })
   });*/
                                                          
};

function tagsToReadMouseover(ths,auto){
     if(!audio_activated) return false;     
     var t=$(ths);     
     if( ( readAll_mode && !$(ths).hasClass(span_class) ) || 
          (readAll_mode && $(ths).hasClass(span_class) && !auto )  
       ){              
         return false;
     }
     window.clearTimeout(hoverTimeout);            
     hoverTimeout =window.setTimeout(function(){
        if(typeof inlinePlayer !== 'undefined'){                          
            $('.last_hovered').removeClass('last_hovered');
            t.addClass('reading_content').addClass('last_hovered');            
             t.find('script').remove();
             var txt='';
             t.each(function(){
	         	if(jQuery(this).is('img')){
		        	txt+=' '+jQuery(this).attr('alt')+'.';
		        }else{
			        txt+=' '+jQuery(this).text()+'.'; 
		        }
		             	
	         })
             if(jQuery.trim(txt)=='' ||txt==null || jQuery.trim(txt)=='undefined.'){
	             return false;
             }  
             hovering=true;
             getHash(txt,false,renderPlayer,{'hovering' : hovering , 'autoPlay' : true});     
             if(readAll_mode){
                 $('html, body').animate({
                    scrollTop: $(".last_hovered").offset().top -60
                 }, 1000);
             }else{
                showFlySpeaker();   
             }
         }    
     },200);                  
}

function tagsToReadMouseout(ths,auto){
    if(!audio_activated) return false;    
    var t=$(ths);    
    if(readAll_mode && $(ths).hasClass(span_class) && !auto ||  ( readAll_mode && !$(ths).hasClass(span_class) )  ){                 
         return false;
     }
    if (hoverTimeout) {
        window.clearTimeout(hoverTimeout);            
    }
    hovering=false;
    if(typeof inlinePlayer !== 'undefined'){             
        t.removeClass('reading_content');        
        soundManager.stopAll();     
        hideFlySpeaker();
    }
}

function checkCompatMode(){    
    if(false && document.compatMode!='CSS1Compat' ){            
        var url=base_url+"css/ie_quirks.css";        
        if (document.createStyleSheet)
        {
            document.createStyleSheet(url);
        }
        else
        {
            $('<link rel="stylesheet" type="text/css" href="' + url + '" />').appendTo('head'); 
        }        
        $(window).scroll(function() {
            var top =(parseInt($(window).height()) + $(this).scrollTop())-100;
            $('.reader_miniature').stop().animate({'top' : top + "px"},150);
        });                             
    }
}

function setCookie(name,value,days) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime()+(days*24*60*60*1000));
        var expires = "; expires="+date.toGMTString();
    }
    else var expires = "";
    document.cookie = name+"="+value+expires+"; path=/";
}

function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}

function deleteCookie(name) {
    setCookie(name,"",-1);
}
})();