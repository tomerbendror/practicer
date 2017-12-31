(function($) {
    $.fn.almareader = function(options) {
        return this.each(function() {
			var cookies = {
				set : function (cname, cvalue, exdays) {
					var d = new Date();
					if (exdays) {
						d.setTime(d.getTime() + (exdays*24*60*60*1000));
						var expires = "expires="+d.toUTCString();
					}
					document.cookie = cname + "=" + cvalue + ";path=/;" + expires;
				},
				get : function (cname) {
					var name = cname + "=";
					var ca = document.cookie.split(';');
					for(var i=0; i<ca.length; i++) {
						var c = ca[i];
						while (c.charAt(0)==' ') c = c.substring(1);
						if (c.indexOf(name) != -1) return c.substring(name.length, c.length);
					}
					return "";
				}
			};
			var settings = [
				{
					"name"		:	"reader",
					"title"		:	"הקראת טקסט",
					"values"	:	[
										{"key" : "on", "val" : "פעילה"},
										{"key" : "off", "val" : "כבויה"}
									]
				},
				{
					"name"		:	"reader_voice",
					"title"		:	"קול הקראה",
					"isHidden"	:	true,
					"values"	:	[
										{"key" : "Sivan", "val" : "סיוון"},
										{"key" : "Gilad", "val" : "גלעד"}
									]
				},
				{
					"name"		:	"reader_speed",
					"title"		:	"מהירות הקראה",
					"isHidden"	:	true,
					"values"	:	[
										{"key" : "s", "val" : "איטית"},
										{"key" : "n", "val" : "רגילה"},
										{"key" : "f", "val" : "מהירה"}
									]
				},
				{
					"name"		:	"reader_autoStart",
					"title"		:	"הקראה אוטומטית",
					"isHidden"	:	true,
					"values"	:	[
										{"key" : "on", "val" : "כן"},
										{"key" : "off", "val" : "לא"}
									]
				},
				{
					"name"		:	"reader_onMouseOver",
					"title"		:	"הקראה במעבר עכבר",
					"isHidden"	:	true,
					"values"	:	[
										{"key" : "on", "val" : "כן"},
										{"key" : "off", "val" : "לא"}
									]
				},
				{
					"name"		:	"reader_onFocus",
					"title"		:	"הקראת פוקוס",
					"isHidden"	:	true,
					"values"	:	[
										{"key" : "on", "val" : "כן"},
										{"key" : "off", "val" : "לא"}
									]
				}
			];
			
			settings.forEach(function(element){
				element["values"].forEach(function(element2){
					if (cookies.get(element["name"])) {
						$.fn.almareader.defaults[element["name"]] = cookies.get(element["name"]);
					}
				});
			});
			
			var o = $.extend({}, $.fn.almareader.defaults, options);
			var $this = $(this);
			
			var autoReading = false;
			
			var gearSVG = '<img src="'+o.base_path+'img/settings.png" alt="הגדרות" />';
			
			var controlsHTML = 	'<div class="almareader-widget">'+
				'<div class="almareader-controls">' +
					'<a href="#" class="almareader-title">' +
						'הקראה' +
					'</a>' +
					'<div class="almareader-holder">' +
						'<div class="almareader-tbl">' +
							'<div class="almareader-holder-col">' +
								'<div class="almareader-switch">' +
									'<input id="almareader-status-checkbox" class="almareader-cmn-toggle almareader-cmn-toggle-round" type="checkbox">' +
									'<label for="almareader-status-checkbox"></label>' +
								'</div>' +
							'</div>' +
							'<div class="almareader-holder-col almareader-mafrid">' +
								'<a title="מתג פתיחה וסגירה של הגדרות עלמה רידר" id="almareader-toggle-settings" class="almareader-settings noRead" href="#"></a>' +
							'</div>' +
						'</div>' +
						'<div class="almareader-copyrights">' +
							'<a href="http://almareader.com/" target="_blank"><img src="'+o.base_path+'img/byalmareader.png" alt="הקראה בחסות עלמה-רידר" /></a>' +
						'</div>' +
					'</div>' +
				'</div>' +
			'</div>';
			
			var methods = {
				toggle : function () {
					$(".almareader-widget").toggleClass("open");
					$(".almareader-setting").toggleClass("open");
					$("body").toggleClass("open");
				},
				open : function () {
					$(".almareader-widget").addClass("open");
					$(".almareader-setting").addClass("open");
					$("body").addClass("open");
				},
				close : function () {
					$(".almareader-widget").removeClass("open");
					$(".almareader-setting").removeClass("open");
					$("body").removeClass("open");
				}
			};
			
			var settings_methods = {
				"reader" : function (status){
					o.reader = status;
					
					$(".almareader-setting input[name='reader']").prop("checked", false);
					$(".almareader-setting #reader_" + (status)).prop("checked", true);
					
					if (status == "on") {
						$("fieldset.reader_onMouseOver").show();
						$("fieldset.reader_onFocus").show();
						$("fieldset.reader_voice").show();
						$("fieldset.reader_speed").show();
						$("fieldset.reader_autoStart").show();
						
						$("#almareader-status-checkbox").prop("checked", true);
					}
					else {
						$("fieldset.reader_onMouseOver").hide();
						$("fieldset.reader_onFocus").hide();
						$("fieldset.reader_voice").hide();
						$("fieldset.reader_speed").hide();
						$("fieldset.reader_autoStart").hide();
						
						$("#almareader-status-checkbox").prop("checked", false);
					}
					cookies.set("reader",status,7);
				},
				"reader_onMouseOver" : function (status){
					o.reader_onMouseOver = status;
				},
				"reader_onFocus" : function (status){
					o.reader_onFocus = status;
				},
				"reader_voice" : function (voice){
					o.reader_voice = voice;
				},
				"reader_speed" : function (speed){
					o.reader_speed = speed;
				}
			};
			
			// Create UI
			var optionsHTML = "<div class='almareader-setting'>";
				optionsHTML += "<div class='settings-container'>";
					settings.forEach(function(element){
						isHidden = (element["isHidden"] === true) ? 'style="display: none"' : '';
						optionsHTML += "<fieldset class='"+element["name"]+"' "+isHidden+">";
							optionsHTML += "<legend>";
								optionsHTML += element["title"];
							optionsHTML += "</legend>";
							element["values"].forEach(function(element2){
								isChecked = ($.fn.almareader.defaults[element["name"]] == element2["key"]) ? 'checked="checked"' : '';
								optionsHTML += '<input type="radio" id="'+element["name"]+'_'+element2["key"]+'" name="'+element["name"]+'" value="'+element2["key"]+'" '+isChecked+' />';
								optionsHTML += '<label for="'+element["name"]+'_'+element2["key"]+'">';
								optionsHTML += element2["val"];
								optionsHTML += "</label>";
							});
						optionsHTML += "</fieldset>";
					});
				optionsHTML += "</div>";
				optionsHTML += "<div class='almareader-logo'>";
					optionsHTML += "<a href='http://www.almareader.com/' target='_blank'>";
						optionsHTML += "<img src='"+o.base_path+"img/almareader.png' alt='ההקראה בחסות עלמה רידר' title='ההקראה בחסות עלמה רידר' />";
					optionsHTML += "</a>";
				optionsHTML += "</div>";
			optionsHTML += "</div>";
			
			$this.append(controlsHTML);
			$this.append(optionsHTML);
			
			// Set defaults
			settings.forEach(function(element){
				element["values"].forEach(function(element2){
					name = element["name"];
					val = $.fn.almareader.defaults[element["name"]];
					if	(settings_methods[name]) {
						settings_methods[name](val);
					}
				});
			});
			
			// UI Calls
			$('.settings-container').height($(window).height()-$('.almareader-logo').height());
			$("#almareader-status-checkbox").change(function(e){
				e.preventDefault();
				e.stopPropagation();
				
				var status = ($(this).prop("checked"))?'on':'off';
				
				settings_methods["reader"](status);
			});
			$(".almareader-title").click(function(e){
				e.preventDefault();
				e.stopPropagation();
				
				var status;
				$(".almareader-holder").slideToggle(function(){
					if ($(this).is(":visible")) {
						status = 'on';
					}
					else {
						status = 'off';
					}
					settings_methods["reader"](status);
				});
			});
			$(".almareader-setting").click(function(e){
			   e.stopPropagation();
			});
			$("html").click(function(e){
				methods.close();
			});
			$("#almareader-toggle-settings").click(function(e){
				e.preventDefault();
				e.stopPropagation();
				methods.toggle();
			});
			$(".almareader-setting input").change(function(e){
				e.preventDefault();
				name = $(this).prop("name");
				val = $(this).val();
				if (settings_methods[name]) {
					settings_methods[name](val);
				}
				cookies.set(name,val,7);
			});
			
			// - - -
			
			var autoRead = new Array();
			var autoReading_i = 0;
			
			var autoReadControls = {
				"togglePlay" : function () {
					if (player.paused) {
						player.play();
					}
					else {
						player.pause();
					}
				},
				"play" : function () {
					player.play();
				},
				"pause" : function () {
					player.pause();
				},
				"stop" : function () {
					player.pause();
					autoReading = false;
					$('.autoReading-controls').hide();
					$(".reader-reading").removeClass("reader-reading");
				},
				"next" : function () {
					readElement();
				},
				"prev" : function () {
					if (autoReading_i >= 2) {
						autoReading_i = autoReading_i - 2;
						readElement();
					}
				}
			};
			
			var readElement = function () {
				$(".reader-reading").removeClass("reader-reading");
				if (autoRead[autoReading_i]) {
					txt = getTextToRead(autoRead[autoReading_i]);
					if (txt.length > 2) {
						$(autoRead[autoReading_i]).addClass("reader-reading");
						
						getHash(txt,false,loadAndPlay);
					}
					else {
						autoReadControls["stop"]();
					}
				}
				else {
					autoReadControls["stop"]();
				}
				autoReading_i++;
			};
			
			// - - -
			
			var iOS = ( navigator.userAgent.match(/(iPad|iPhone|iPod)/g) ? true : false );
			
			player = document.createElement("audio");
			
			var on_canplay = function() {
				player.play();
			};
			player.addEventListener("canplaythrough", on_canplay, false);
			
			var on_touch_init = function() {
				player.play();
				document.removeEventListener("touchstart", on_touch_init, false);
			};
			document.addEventListener("touchstart", on_touch_init, false);
			
			var on_end = function() {
				if (autoReading) {
					readElement();
				}
			};
			player.addEventListener("ended", on_end, false);
			
			if (!cookies.get("intrudaction_read")) {
				
				if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
					player.src = o.base_path + "start_mobile.mp3";
				}
				else {
					player.src = o.base_path + "start.mp3";
				}
				
				cookies.set("intrudaction_read","1");
			}
			else {
				player.src = o.base_path + "init.mp3";
			}
			
			var getHash = function(txt,preferredOnly,callback,params){
				
				encoded = Sha1.hash(txt);
				audioFile = "http://www.almagu5.com/webreader.audio/CID_" + encoded + "_" + o.reader_speed + "_" + o.reader_voice + ".mp3";
				//player.src = audioFile;
				
				if (iOS) {
					player.src = audioFile;
					player.play();
					
					var on_error = function() {
						player.removeEventListener('error', on_error, false);
						_getHash(txt,preferredOnly,callback,params);
					};
					player.addEventListener("error", on_error, false);
					
					var on_touch_init = function() {
						player.play();
						document.removeEventListener("touchstart", on_touch_init, false);
					};
					document.addEventListener("touchstart", on_touch_init, false);
				}
				else {
					var test_mp3 = document.createElement("audio");
					test_mp3.src = audioFile;
					
					var on_error = function() {
						test_mp3.removeEventListener('error', on_error, false);
						_getHash(txt,preferredOnly,callback,params);
					};
					test_mp3.addEventListener("error", on_error, false);
					
					var on_success = function() {
						test_mp3.removeEventListener('canplaythrough', on_success, false);
						player.src = audioFile;
						
						
						var on_touch_init = function() {
							player.play();
							document.removeEventListener("touchstart", on_touch_init, false);
						};
						document.addEventListener("touchstart", on_touch_init, false);
				
						
					};
					test_mp3.addEventListener("canplaythrough", on_success, false);
				}
			};
			
			var _getHash = function(txt,preferredOnly,callback,params){
				cid = 'CID';
				referer_param = 'http://binaa.co.il';
				
				var args =  { 'cid' : cid , 'markup' : txt,'preferredOnly' : preferredOnly};
				
				$.getJSON('http://www.almagu5.com/webreader?callback=?',args, function (response) {
					getHash(txt,preferredOnly,callback,params);
				});
			};
			
			var loadAndPlay = function (response,params) {
				audioHash = response.PhraseHash;
				audioFile = "http://www.almagu5.com/webreader.audio/CID_" + audioHash + "_" + o.reader_speed + "_" + o.reader_voice + ".mp3";
				player.src = audioFile;
			};
			
			var getTextToRead = function(element) {
				tag = element.prop('tagName');
				
				//element.html(element.html().replace(">","> "));
				
				if	(element.data("read") !== undefined) {
					return element.data("read");
				}
				if (tag == "A") {
					if	(element.attr("title") !== undefined) {
						return element.attr("title");
					}
					if (element.text().length == 0) {
						if (element.children() !== undefined) {
							return getTextToRead(element.children());
						}
						return "";
					}
					return element.text().trim();
				}
				if (tag == "IMG") {
					return element.attr("alt");
				}
				return element.text().trim();
			};
			
			var timeout, t;
			
			$(o.reader_onMouseOver_tags).on({
				mouseenter : function(e) {
					e.stopPropagation();
					if (!autoReading) {
						if (o.reader == 'on' && o.reader_onMouseOver == 'on') {
							if (!$(this).hasClass("noRead")) {
								var t = $(this);        
								clearTimeout(t.data('timeout'));

								txt = getTextToRead($(this));
								
								if (txt.length > 2) {
									$(this).addClass("reader-reading");
									
									timeout = setTimeout(function() {
										getHash(txt,false,loadAndPlay);
									}, 500);
								}
								
								$(this).data('timeout', timeout);
							}
						}
					}
				},
				mouseleave : function() {
					if (!autoReading) {
						if (o.reader == 'on' && o.reader_onMouseOver == 'on') {
							$(".reader-reading").removeClass("reader-reading");
							player.pause();
							
							clearTimeout($(this).data('timeout'));
						}
					}
				},
				focus : function () {
					if (o.reader == 'on' && o.reader_onFocus == 'on') {
						if (!$(this).hasClass("noRead")) {
							t = $(this);        
							clearTimeout(t.data('timeout'));

							txt = getTextToRead($(this));
							
							if (txt.length > 2) {
								var timeout = setTimeout(function() {
									getHash(txt,false,loadAndPlay);
								}, 500);
							}
							
							$(this).data('timeout', timeout);
						}
					}
				}
			});
			
			$(document).ready(function(){
				if (o.reader == "on") { 
					$(".almareader-holder").show();
				}
				if (o.reader_autoStart == "on" && o.reader == "on") {
					
					var span_class = 'toread';
					var content_class='#ctlContent *,.ListArticle1of2 *';
					var exclude_tag='.readmore a,.ArticlesListDate,.ArticleMainDate,.navigationTab,.navigationTab *,.hidden';
					
					$(content_class).not(exclude_tag).contents().filter(function() { 
						var isText = (this.nodeType == 3) && this.nodeValue.match(/\S/) && !( $(this).is('style') || $(this).parent().is('style') || $(this).parent().is('option') ) && !( $(this).is('script') || $(this).parent().is('script') ) ;         
						return isText; 
					}).wrap('<span class="'+span_class+'"  />');
					$(content_class).not(exclude_tag).contents().filter(function() {       
						var isImg = $(this).is('img');
						return  isImg; 
					}).addClass('img_reader');
					
					$('.'+span_class).each(function(){
						txt = getTextToRead($(this));
						if (txt.length > 0) {
							autoRead.push($(this));
						}
					});
					
					/*
					$(o.reader_autoStartTags).each(function(){
						txt = getTextToRead($(this));
						if (txt) {
							if (txt.length > 0) {
								autoRead.push($(this));
							}
						}
					});*/
					
					
					if (autoRead.length > 0) {
						autoReading = 1;
						
						var controlsHTML = 	'<div class="almareader-autoread-controls autoReading-controls">'+
							'<div class="almareader-autoread-controls-title">' +
								'שליטה על מצב הקראה אוטומטית' +
							'</div>' +
							'<a data-action="play" title="נגן (גם באמצעות מקש P)" class="almareader-btn autoReadControl noRead" href="#">'+
								'<img src="'+o.base_path+'img/play.png" alt="נגן טקסט ברצף" />' +
							'</a>' +
							'<a data-action="pause" title="השהה (גם באמצעות מקש P)" class="almareader-btn autoReadControl noRead" href="#">'+
								'<img src="'+o.base_path+'img/pause.png" alt="השהה הקראה" />' +
							'</a>' +
							'<a data-action="stop" title="בטל הקראה אוטומטית (גם באמצעות מקש S)" class="almareader-btn autoReadControl noRead" href="#">'+
								'<img src="'+o.base_path+'img/stop.png" alt="בטל הקראה ברצף" />' +
							'</a>' +
							'<a data-action="prev" title="עבור לפסקה הקודמת (גם באמצעות המקש חץ למעלה במקלדת)" class="almareader-btn autoReadControl noRead" href="#">'+
								'<img src="'+o.base_path+'img/up.png" alt="פסקה קודמת" />' +
							'</a>' +
							'<a data-action="next" title="עבור לפסקה הבאה (גם באמצעות המקש חץ למטה במקלדת)" class="almareader-btn autoReadControl noRead" href="#">'+
								'<img src="'+o.base_path+'img/down.png" alt="פסקה הבאה" />' +
							'</a>' +
						'</div>';
						$(".almareader-widget").append(controlsHTML);
						
						readElement();
					}
				}
				$(".autoReadControl").click(function(e){
					e.preventDefault();
					autoReadControls[$(this).data("action")]();
				});
				var isInputFocused = false;
				$("input, textarea").focus(function(){
					isInputFocused = true;
				});
				$("input, textarea").blur(function(){
					isInputFocused = false;
				});
				$(document).keydown(function(e) {
					if (!isInputFocused) {
						if (e.which == 74) {	// ח / J
							e.preventDefault();
							
							var status = (o.reader == 'on')?'off':'on';
							
							if (status == "on") {
								$(".almareader-holder").show();
							}
							else {
								$(".almareader-holder").hide();
							}
							
							$(".almareader-setting input[name='reader']").prop("checked", false);
							$(".almareader-setting #reader_" + (status)).prop("checked", true);
							
							settings_methods["reader"](status);
							
							if (status == "on")
							{
								getHash("הנגשה קולית הופעלה",true,loadAndPlay);
							}
							else {
								if (autoReading) {
									autoReadControls["stop"]();
								}
							}
						}
						if (e.which == 80 || e.which == 101) {	// P or 5
							e.preventDefault();
							autoReadControls["togglePlay"]();
						}
						else if (e.which == 83 || e.which == 96) {	// S or 0
							autoReadControls["stop"]();
						}
						else if (e.which == 40 || e.which == 98) {	// down
							autoReadControls["next"]();
						}
						else if (e.which == 38 || e.which == 104) {	// up
							autoReadControls["prev"]();
						}
					}
				});
			});
		});
    };
    $.fn.almareader.defaults = {
		base_path: 'http://almareader.com/almareaderjs/NI/',
		reader: 'off',
		reader_onMouseOver: 'on',
		reader_onMouseOver_tags: 'h1,h2,h3,h4,p,img,a,span,li,.ArticleMainSummary,.GallerySummary_Hp3Cols',
		reader_autoStart: 'on',
		reader_autoStartTags: '#ctlContent *,.ListArticle1of2 *',
		reader_onFocus: 'on',
		reader_speed: 'n',
		reader_voice: 'Sivan'
    };
})(jQuery);


/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
/*  SHA-1 implementation in JavaScript | (c) Chris Veness 2002-2013 | www.movable-type.co.uk      */
/*   - see http://csrc.nist.gov/groups/ST/toolkit/secure_hashing.html                             */
/*         http://csrc.nist.gov/groups/ST/toolkit/examples.html                                   */
/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

var Sha1 = {};  // Sha1 namespace

/**
 * Generates SHA-1 hash of string
 *
 * @param {String} msg                String to be hashed
 * @param {Boolean} [utf8encode=true] Encode msg as UTF-8 before generating hash
 * @returns {String}                  Hash of msg as hex character string
 */
Sha1.hash = function(msg, utf8encode) {
  utf8encode =  (typeof utf8encode == 'undefined') ? true : utf8encode;
  
  // convert string to UTF-8, as SHA only deals with byte-streams
  if (utf8encode) msg = Utf8.encode(msg);
  
  // constants [Â§4.2.1]
  var K = [0x5a827999, 0x6ed9eba1, 0x8f1bbcdc, 0xca62c1d6];
  
  // PREPROCESSING 
  
  msg += String.fromCharCode(0x80);  // add trailing '1' bit (+ 0's padding) to string [Â§5.1.1]
  
  // convert string msg into 512-bit/16-integer blocks arrays of ints [Â§5.2.1]
  var l = msg.length/4 + 2;  // length (in 32-bit integers) of msg + â€˜1â€™ + appended length
  var N = Math.ceil(l/16);   // number of 16-integer-blocks required to hold 'l' ints
  var M = new Array(N);
  
  for (var i=0; i<N; i++) {
    M[i] = new Array(16);
    for (var j=0; j<16; j++) {  // encode 4 chars per integer, big-endian encoding
      M[i][j] = (msg.charCodeAt(i*64+j*4)<<24) | (msg.charCodeAt(i*64+j*4+1)<<16) | 
        (msg.charCodeAt(i*64+j*4+2)<<8) | (msg.charCodeAt(i*64+j*4+3));
    } // note running off the end of msg is ok 'cos bitwise ops on NaN return 0
  }
  // add length (in bits) into final pair of 32-bit integers (big-endian) [Â§5.1.1]
  // note: most significant word would be (len-1)*8 >>> 32, but since JS converts
  // bitwise-op args to 32 bits, we need to simulate this by arithmetic operators
  M[N-1][14] = ((msg.length-1)*8) / Math.pow(2, 32); M[N-1][14] = Math.floor(M[N-1][14])
  M[N-1][15] = ((msg.length-1)*8) & 0xffffffff;
  
  // set initial hash value [Â§5.3.1]
  var H0 = 0x67452301;
  var H1 = 0xefcdab89;
  var H2 = 0x98badcfe;
  var H3 = 0x10325476;
  var H4 = 0xc3d2e1f0;
  
  // HASH COMPUTATION [Â§6.1.2]
  
  var W = new Array(80); var a, b, c, d, e;
  for (var i=0; i<N; i++) {
  
    // 1 - prepare message schedule 'W'
    for (var t=0;  t<16; t++) W[t] = M[i][t];
    for (var t=16; t<80; t++) W[t] = Sha1.ROTL(W[t-3] ^ W[t-8] ^ W[t-14] ^ W[t-16], 1);
    
    // 2 - initialise five working variables a, b, c, d, e with previous hash value
    a = H0; b = H1; c = H2; d = H3; e = H4;
    
    // 3 - main loop
    for (var t=0; t<80; t++) {
      var s = Math.floor(t/20); // seq for blocks of 'f' functions and 'K' constants
      var T = (Sha1.ROTL(a,5) + Sha1.f(s,b,c,d) + e + K[s] + W[t]) & 0xffffffff;
      e = d;
      d = c;
      c = Sha1.ROTL(b, 30);
      b = a;
      a = T;
    }
    
    // 4 - compute the new intermediate hash value
    H0 = (H0+a) & 0xffffffff;  // note 'addition modulo 2^32'
    H1 = (H1+b) & 0xffffffff; 
    H2 = (H2+c) & 0xffffffff; 
    H3 = (H3+d) & 0xffffffff; 
    H4 = (H4+e) & 0xffffffff;
  }

  return Sha1.toHexStr(H0) + Sha1.toHexStr(H1) + 
    Sha1.toHexStr(H2) + Sha1.toHexStr(H3) + Sha1.toHexStr(H4);
}

//
// function 'f' [Â§4.1.1]
//
Sha1.f = function(s, x, y, z)  {
  switch (s) {
  case 0: return (x & y) ^ (~x & z);           // Ch()
  case 1: return x ^ y ^ z;                    // Parity()
  case 2: return (x & y) ^ (x & z) ^ (y & z);  // Maj()
  case 3: return x ^ y ^ z;                    // Parity()
  }
}

//
// rotate left (circular left shift) value x by n positions [Â§3.2.5]
//
Sha1.ROTL = function(x, n) {
  return (x<<n) | (x>>>(32-n));
}

//
// hexadecimal representation of a number 
//   (note toString(16) is implementation-dependant, and  
//   in IE returns signed numbers when used on full words)
//
Sha1.toHexStr = function(n) {
  var s="", v;
  for (var i=7; i>=0; i--) { v = (n>>>(i*4)) & 0xf; s += v.toString(16); }
  return s;
}


/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
/*  Utf8 class: encode / decode between multi-byte Unicode characters and UTF-8 multiple          */
/*              single-byte character encoding (c) Chris Veness 2002-2013                         */
/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

var Utf8 = {};  // Utf8 namespace

/**
 * Encode multi-byte Unicode string into utf-8 multiple single-byte characters 
 * (BMP / basic multilingual plane only)
 *
 * Chars in range U+0080 - U+07FF are encoded in 2 chars, U+0800 - U+FFFF in 3 chars
 *
 * @param {String} strUni Unicode string to be encoded as UTF-8
 * @returns {String} encoded string
 */
Utf8.encode = function(strUni) {
  // use regular expressions & String.replace callback function for better efficiency 
  // than procedural approaches
  var strUtf = strUni.replace(
      /[\u0080-\u07ff]/g,  // U+0080 - U+07FF => 2 bytes 110yyyyy, 10zzzzzz
      function(c) { 
        var cc = c.charCodeAt(0);
        return String.fromCharCode(0xc0 | cc>>6, 0x80 | cc&0x3f); }
    );
  strUtf = strUtf.replace(
      /[\u0800-\uffff]/g,  // U+0800 - U+FFFF => 3 bytes 1110xxxx, 10yyyyyy, 10zzzzzz
      function(c) { 
        var cc = c.charCodeAt(0); 
        return String.fromCharCode(0xe0 | cc>>12, 0x80 | cc>>6&0x3F, 0x80 | cc&0x3f); }
    );
  return strUtf;
}

/**
 * Decode utf-8 encoded string back into multi-byte Unicode characters
 *
 * @param {String} strUtf UTF-8 string to be decoded back to Unicode
 * @returns {String} decoded string
 */
Utf8.decode = function(strUtf) {
  // note: decode 3-byte chars first as decoded 2-byte strings could appear to be 3-byte char!
  var strUni = strUtf.replace(
      /[\u00e0-\u00ef][\u0080-\u00bf][\u0080-\u00bf]/g,  // 3-byte chars
      function(c) {  // (note parentheses for precence)
        var cc = ((c.charCodeAt(0)&0x0f)<<12) | ((c.charCodeAt(1)&0x3f)<<6) | ( c.charCodeAt(2)&0x3f); 
        return String.fromCharCode(cc); }
    );
  strUni = strUni.replace(
      /[\u00c0-\u00df][\u0080-\u00bf]/g,                 // 2-byte chars
      function(c) {  // (note parentheses for precence)
        var cc = (c.charCodeAt(0)&0x1f)<<6 | c.charCodeAt(1)&0x3f;
        return String.fromCharCode(cc); }
    );
  return strUni;
}

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

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

$('head').append('<link rel="stylesheet" href="http://almareader.com/almareaderjs/NI/almareader.css" type="text/css" />');


$('body').append('<div class="almareader"></div>');

$(document).ready(function(){
	console.log("almareader loaded");
	$(".almareader").almareader();
});
