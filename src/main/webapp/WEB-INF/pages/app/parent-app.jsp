<%@ page import="com.practice.util.MobileUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en" ng-app="parentApp">
<head>
    <base href="/app/parent/"> <%-- angular route html5 mode --%>

    <link href='https://fonts.googleapis.com/css?family=Alef:700,400&subset=hebrew' rel='stylesheet' type='text/css'>

    <meta http-equiv="Content-Type" content="text/html;  charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Practicer</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <% String baseLocation = "../../wrapkit/"; %>

    <!-- fav and touch icons -->
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="<%=baseLocation%>ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="<%=baseLocation%>ico/apple-touch-icon-114-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="72x72" href="<%=baseLocation%>ico/apple-touch-icon-72-precomposed.png">
    <link rel="apple-touch-icon-precomposed" href="<%=baseLocation%>ico/apple-touch-icon-57-precomposed.png">
    <link rel="shortcut icon" href="<%=baseLocation%>ico/favicon.png">
    <link rel="shortcut icon" href="<%=baseLocation%>ico/favicon.ico">


    <style>
        .full-screen-loader {
            position: fixed;
            top: 0;
            bottom: 0;
            right: 0;
            left: 0;
            z-index: 10001;
            background-color: #fff;
        }

        .full-screen-loader i {
            position: fixed;
            top: 50%;
            left: 50%;
            margin-left: -28px;
            margin-top: -28px;
        }
    </style>

</head>

<body style="-webkit-background-size: cover; background: url(/wrapkit/images/patterns/sativa.png) fixed; overflow-x: hidden;">
<div data-ng-controller="menuCtrl">

<div class="full-screen-loader">
    <i class="fa fa-spin fa-spin-1x fa-4x fa-asterisk fa-fw text-primary"></i>
</div>

<main id="wrapper" class="wrapkit-wrapper container">
<header class="header" id="myHeader">
<div class="navbar">
<div class="container-fluid">
<div class="navbar-header navbar-block">
<button type="button" class="navbar-toggle navbar-toggle-alt" data-toggle="collapse" data-target="">
    <span class="fa fa-bars"></span>
</button>

<a class="navbar-brand" href="/"><strong>Practicer</strong> <i class="fa fa-paper-plane"></i> <strong></strong></a>

<ul class="nav navbar-nav pull-right">

<%--<li class="dropdown">--%>
<%--<a class="dropdown-toggle alerts" data-toggle="dropdown" title="7 הודעות שלא נקראו">--%>
    <%--<span class="badge">7</span>--%>
    <%--<i class="fa fa-bell"></i>--%>
<%--</a>--%>

<div class="dropdown-menu dropdown-menu-md stop-propagation">
<div class="dropdown-header text-center"><strong>הודעות</strong></div>
<!-- /.dropdown-header -->
<div class="dropdown-body">
<!-- Nav tabs -->
<ul class="nav nav-tabs nav-tabs-alt nav-justified" role="tablist">
    <li class="active"><a href="#notif-activities" role="tab" data-toggle="tab">Activities
        <small class="text-muted">(3)</small>
    </a></li>
    <li><a href="#notif-messages" role="tab" data-toggle="tab">Messages
        <small class="text-muted">(2)</small>
    </a></li>
    <li><a href="#notif-tasks" role="tab" data-toggle="tab">Tasks
        <small class="text-muted">(2)</small>
    </a></li>
</ul>
<!-- /.nav-tabs -->
<!-- Tab panes -->
<div class="tab-content">
<div class="tab-pane fade in active" id="notif-activities">
    <div class="nice-scroll" style="max-height:354px">
        <ul class="kit-droplists">
            <li class="unread">
                <a href="pages-profile.html">
                    <div class="kit-avatar kit-avatar-32 pull-left">
                        <img src="/wrapkit/images/dummy/profile.jpg" alt="user">
                    </div>
                    <div class="droplists-text">
                        <strong>Bent</strong> just update his project <strong>Wrapkit - Admin template</strong>

                        <div>
                            <small>6 minutes</small>
                        </div>
                    </div>
                    <!-- /.media-body -->
                </a>
            </li>
            <li class="unread">
                <a href="pages-profile.html">
                    <div class="kit-avatar kit-avatar-32 pull-left">
                        <img src="/wrapkit/images/dummy/uifaces16.jpg" alt="user">
                    </div>
                    <div class="droplists-text">
                        <strong>Ofelia Camacho</strong> favorite your Post
                        <div>
                            <small>11 minutes</small>
                        </div>
                    </div>
                    <!-- /.media-body -->
                </a>
            </li>
            <li>
                <a href="pages-profile.html">
                    <div class="kit-avatar kit-avatar-32 pull-left">
                        <img src="/wrapkit/images/dummy/uifaces14.jpg" alt="user">
                    </div>
                    <div class="droplists-text">
                        <strong>Nidia Djuvara</strong> just Upload a revision design <strong>#D7043</strong>

                        <div>
                            <small>42 minutes</small>
                        </div>
                    </div>
                    <!-- /.media-body -->
                </a>
            </li>
            <li>
                <a href="pages-profile.html">
                    <div class="kit-avatar kit-avatar-32 pull-left">
                        <img src="/wrapkit/images/dummy/uifaces15.jpg" alt="user">
                    </div>
                    <div class="droplists-text">
                        <strong>Jake Thomas</strong> just update his profile name from <strong>J. Thomas</strong>

                        <div>
                            <small>1 hour</small>
                        </div>
                    </div>
                    <!-- /.media-body -->
                </a>
            </li>
            <li>
                <a href="pages-profile.html">
                    <div class="kit-avatar kit-avatar-32 pull-left">
                        <img src="/wrapkit/images/dummy/uifaces13.jpg" alt="user">
                    </div>
                    <div class="droplists-text">
                        <strong>Jayden Williams</strong> register as a member
                        <div>
                            <small>1 hour</small>
                        </div>
                    </div>
                    <!-- /.media-body -->
                </a>
            </li>
            <li>
                <a href="pages-profile.html">
                    <div class="kit-avatar kit-avatar-32 pull-left">
                        <img src="/wrapkit/images/dummy/uifaces10.jpg" alt="user">
                    </div>
                    <div class="droplists-text">
                        <strong>Antonie David</strong> comment on your Post
                        <div>
                            <small>1 hours ago</small>
                        </div>
                    </div>
                    <!-- /.media-body -->
                </a>
            </li>
            <li class="unread">
                <a href="pages-profile.html">
                    <div class="kit-avatar kit-avatar-32 pull-left">
                        <img src="/wrapkit/images/dummy/uifaces18.jpg" alt="user">
                    </div>
                    <div class="droplists-text">
                        <strong>Laura Ferguson</strong> is no following You
                        <div>
                            <small>2 hours ago</small>
                        </div>
                    </div>
                    <!-- /.media-body -->
                </a>
            </li>
        </ul>
        <!-- /.kit-droplists -->
    </div>
    <!-- /.nice-scroll -->
</div>
<!-- /.tab-pane -->

<div class="tab-pane fade" id="notif-messages">
    <div class="nice-scroll" style="max-height:354px">
        <ul class="kit-droplists">
            <li>
                <a href="pages-email.html">
                    <div class="kit-avatar kit-avatar-32 kit-avatar-square pull-left">
                        <img src="/wrapkit/images/dummy/unknown-profile.jpg" alt="user">
                    </div>
                    <div class="droplists-text">
                        <strong>Wrapbootstrap</strong> Newest Themes & Templates - Et est, sed mattis, donec hac
                        <div>
                            <small>Today, <strong>04:30 AM</strong></small>
                        </div>
                    </div>
                    <!-- /.media-body -->
                </a>
            </li>
            <li>
                <a href="pages-email.html">
                    <div class="kit-avatar kit-avatar-32 kit-avatar-square pull-left">
                        <img src="/wrapkit/images/dummy/uifaces7.jpg" alt="user">
                    </div>
                    <div class="droplists-text">
                        <strong>Eugene Barnett</strong> Service Update: added support for Angular - Sed ultricies nibh,
                        in feugiat sapien
                        <div>
                            <small>Today, <strong>03:11 AM</strong></small>
                        </div>
                    </div>
                    <!-- /.media-body -->
                </a>
            </li>
            <li class="unread">
                <a href="pages-email.html">
                    <div class="kit-avatar kit-avatar-32 kit-avatar-square pull-left">
                        <img src="/wrapkit/images/dummy/uifaces4.jpg" alt="user">
                    </div>
                    <div class="droplists-text">
                        <strong>bent10@stilearning.com</strong> Spread the Word & Earn! - Dapibus nec. Integer sed purus
                        <div>
                            <small>Today, <strong>02:47 AM</strong></small>
                        </div>
                    </div>
                    <!-- /.media-body -->
                </a>
            </li>
            <li>
                <a href="pages-email.html">
                    <div class="kit-avatar kit-avatar-32 kit-avatar-square pull-left">
                        <img src="/wrapkit/images/dummy/uifaces3.jpg" alt="user">
                    </div>
                    <div class="droplists-text">
                        <strong>David Lloyd</strong> Say thanks for your awesome works! - Viverra fermentum ac. Litora
                        mauris elit
                        <div>
                            <small>Yesterday, <strong>11:41 PM</strong></small>
                        </div>
                    </div>
                    <!-- /.media-body -->
                </a>
            </li>
            <li class="unread">
                <a href="pages-email.html">
                    <div class="kit-avatar kit-avatar-32 kit-avatar-square pull-left">
                        <img src="/wrapkit/images/dummy/unknown-profile.jpg" alt="user">
                    </div>
                    <div class="droplists-text">
                        <strong>Dribbble</strong> Design brief from Apple - Quis in nonummy. Ut augue, proident habitant
                        <div>
                            <small>Yesterday, <strong>11:23 PM</strong></small>
                        </div>
                    </div>
                    <!-- /.media-body -->
                </a>
            </li>
            <li>
                <a href="pages-email.html">
                    <div class="kit-avatar kit-avatar-32 kit-avatar-square pull-left">
                        <img src="/wrapkit/images/dummy/uifaces16.jpg" alt="user">
                    </div>
                    <div class="droplists-text">
                        <strong>Sofia Manea</strong> Need help getting started with Wrapkit - Nonummy vitae. Vehicula
                        eget, eleifend arcu
                        <div>
                            <small>Yesterday, <strong>10:04 PM</strong></small>
                        </div>
                    </div>
                    <!-- /.media-body -->
                </a>
            </li>
            <li>
                <a href="pages-email.html">
                    <div class="kit-avatar kit-avatar-32 kit-avatar-square pull-left">
                        <img src="/wrapkit/images/dummy/uifaces20.jpg" alt="user">
                    </div>
                    <div class="droplists-text">
                        <strong>Olivia Gonzales</strong> Updating some of the designs! - Commodo non ac, sem netus
                        adipiscing
                        <div>
                            <small>Yesterday, <strong>08:55 PM</strong></small>
                        </div>
                    </div>
                    <!-- /.media-body -->
                </a>
            </li>
            <li>
                <a href="pages-email.html">
                    <div class="kit-avatar kit-avatar-32 kit-avatar-square pull-left">
                        <img src="/wrapkit/images/dummy/uifaces10.jpg" alt="user">
                    </div>
                    <div class="droplists-text">
                        <strong>Marco Auer</strong> Request custom designs! - Adipiscing pellentesque cum, proin luctus
                        <div>
                            <small>Yesterday, <strong>03:57 PM</strong></small>
                        </div>
                    </div>
                    <!-- /.media-body -->
                </a>
            </li>
        </ul>
        <!-- /.kit-droplists -->
    </div>
</div>
<!-- /.tab-pane -->

<div class="tab-pane fade" id="notif-tasks">
    <div class="nice-scroll" style="max-height:318px">
        <ul class="kit-droplists">
            <li class="droplists-content">
                <p class="pull-right text-muted">
                    <small>60%</small>
                </p>
                <p>
                    <small><a href="#">Uploading...</a></small>
                </p>
                <div class="progress progress-xs">
                    <div class="progress-bar" role="progressbar" aria-valuenow="60" aria-valuemin="0"
                         aria-valuemax="100" style="width: 60%">
                        <span class="sr-only">60% Complete</span>
                    </div>
                </div>
            </li>
            <li class="droplists-content">
                <p class="pull-right text-muted">
                    <small>33%</small>
                </p>
                <p>
                    <small><a href="#">Upgrade Theme</a></small>
                </p>
                <div class="progress progress-xs">
                    <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="33"
                         aria-valuemin="0" aria-valuemax="100" style="width: 33%">
                        <span class="sr-only">33% Complete</span>
                    </div>
                </div>
            </li>
            <li class="droplists-content">
                <p class="pull-right text-muted">
                    <small>87%</small>
                </p>
                <p>
                    <small><a href="#">Webapp Development</a></small>
                </p>
                <div class="progress progress-xs">
                    <div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="87" aria-valuemin="0"
                         aria-valuemax="100" style="width: 87%">
                        <span class="sr-only">87% Complete</span>
                    </div>
                </div>
            </li>
            <li class="droplists-content">
                <p class="pull-right text-muted">
                    <small>54%</small>
                </p>
                <p>
                    <small><a href="#">Backup Data</a></small>
                </p>
                <div class="progress progress-xs">
                    <div class="progress-bar progress-bar-warning" role="progressbar" aria-valuenow="54"
                         aria-valuemin="0" aria-valuemax="100" style="width: 54%">
                        <span class="sr-only">54% Complete</span>
                    </div>
                </div>
            </li>
            <li class="droplists-content">
                <p class="pull-right text-muted">
                    <small>92%</small>
                </p>
                <p>
                    <small><a href="#">Bandwidth Limit</a></small>
                </p>
                <div class="progress progress-xs">
                    <div class="progress-bar progress-bar-danger" role="progressbar" aria-valuenow="92"
                         aria-valuemin="0" aria-valuemax="100" style="width: 92%">
                        <span class="sr-only">92% Complete</span>
                    </div>
                </div>
            </li>
            <li class="droplists-content">
                <p class="pull-right text-muted">
                    <small>24%</small>
                </p>
                <p>
                    <small><a href="#">Clean System</a></small>
                </p>
                <div class="progress progress-xs">
                    <div class="progress-bar" role="progressbar" aria-valuenow="24" aria-valuemin="0"
                         aria-valuemax="100" style="width: 24%">
                        <span class="sr-only">24% Complete</span>
                    </div>
                </div>
            </li>
            <li class="droplists-content unread">
                <p class="pull-right text-muted">
                    <small>100%</small>
                </p>
                <p>
                    <small><a href="#">Done</a> some tasks</small>
                </p>
                <div class="progress progress-xs">
                    <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="100"
                         aria-valuemin="0" aria-valuemax="100" style="width: 100%">
                        <span class="sr-only">100% Complete</span>
                    </div>
                </div>
            </li>
            <li class="droplists-content unread">
                <p class="pull-right text-muted">
                    <small>100%</small>
                </p>
                <p>
                    <small><a href="#">Done</a> another tasks</small>
                </p>
                <div class="progress progress-xs">
                    <div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="100" aria-valuemin="0"
                         aria-valuemax="100" style="width: 100%">
                        <span class="sr-only">100% Complete</span>
                    </div>
                </div>
            </li>
        </ul>
        <!-- /.kit-droplists -->
    </div>
    <!-- /.nice-scroll -->
</div>
<!-- /.tab-pane -->
</div>
<!-- /.tab-content -->
</div>
<!-- /.dropdown-body -->

<div class="dropdown-footer">
    <div class="pull-right">
        <p><a href="#">Mark as read</a> | <a href="#">Settings</a></p>
    </div>
    <p class="text-muted">
        <button rel="tooltip-bottom" title="Refresh" data-context="inverse" class="btn btn-xs btn-default"><i
                class="fa fa-refresh"></i></button>
        Last update 2 minutes ago
    </p>
</div>
<!-- /.dropdown-footer -->
</div>
<!-- /.dropdown-menu -->


<li class="dropdown" id="top-menu-li">
    <a class="dropdown-toggle" data-toggle="dropdown" title="{{parentDisplayName}}">
        <img class="profile-nav" ng-src="{{profileImageUrl}}" alt="profile">
        <span class="user-name">{{parentDisplayName}}</span> <i class="fa fa-angle-down"></i>
    </a>
    <ul class="dropdown-menu">
        <li><a href="/app/parent/profile">החשבון שלי</a></li>
        <li><a target="_blank" href="https://docs.google.com/document/d/1lE_Mp6WaxErB0CNWz2QmK_7v7fr-estOo02TJ8ffjyk/edit?usp=sharing">מדריך למשתמש</a></li>
        <li class="divider"></li>
        <li><a href="/user/logout">יציאה</a></li>
    </ul>
</li>
</ul>
<!-- /navbar-nav -->
</div>
<!--/.navbar-header-->
</div>
<!--/.container-fluid-->
</div>
<!--/.navbar-->

</header>
<!--/.header-->


<aside class="sidebar" id="sidebar-rtl">
    <nav class="sidebar-nav">
        <ul class="nav">
            <li class="nav-item" id="child-btn-li">
                <a id='child-btn' href="/app/parent/childs">
                    <span class="badge num-font">{{childCount}}</span>
                    <i class="nav-item-icon fa fa-child"></i> <span class="nav-item-text">הילדים שלי</span>
                </a>
            </li>

            <li class="nav-item" id="group-btn-li">
                <a id="groups-btn" href="/app/parent/groups">
                    <span class="badge num-font">{{groupsCount}}</span>
                    <i class="nav-item-icon fa fa-group"></i> <span class="nav-item-text">קבוצות</span>
                </a>
            </li>

            <li class="nav-item open" id="practices-li">
                <a href="#" data-toggle="nav-item-child">
                    <span class="caret fa fa-angle-up"></span>
                    <span class="badge num-font">{{practicesCount}}</span>
                    <i class="nav-item-icon fa fa-puzzle-piece"></i>
                    <span class="nav-item-text">תרגילים</span>
                </a>
                <ul class="nav-item-child" style="display: block;">
                    <li class="nav-item" id="all-practices-li">
                        <a id="exercises-btn" href="/app/parent/exercises">
                            <span class="nav-item-text">כל התרגילים</span>
                        </a>
                    </li>

                    <li class="nav-item">
                        <a id="exercises-by-child-btn" href="/app/parent/exercisesByChild">
                            <span class="nav-item-text">תרגילים לפי ילדים</span>
                        </a>
                    </li>

                    <li class="nav-item">
                        <a id="exercises-by-group-btn" href="/app/parent/exercisesByGroups">
                            <span class="nav-item-text">תרגילים לפי קבוצות</span>
                        </a>
                    </li>
                </ul>
            </li>

            <li class="divider"></li>

            <li class="nav-item" id="my-profile-side-menu-li">
                <a id="profile-btn" href="/app/parent/profile">
                    <i class="nav-item-icon fa fa-user"></i> <span class="nav-item-text">החשבון שלי</span>
                </a>
            </li>
        </ul>
        <!--/.nav-->
    </nav>
    <!--/.sidebar-nav-->
</aside>
<!--/.sidebar-->


<section class="content-wrapper" style="background: #FFFFFF;">
    <div class="content" data-ng-view style="padding: 0"></div>
</section>

</main>
<!--/#wrapper-->


<!-- Modal Setups -->
<!-- Modal -->
<div class="modal fade" id="templateSetup" tabindex="-1" role="dialog" aria-labelledby="templateSetupLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content"></div>
    </div>
</div>


<!-- VENDOR -->
<script src="<%=baseLocation%>scripts/jquery.min.js"></script>
<script src="<%=baseLocation%>scripts/bootstrap.min.js"></script>
<script src="<%=baseLocation%>scripts/jquery-ui.min.js"></script>
<!-- END VENDOR -->


<!-- !IMPORTANT DEPENDENCIES -->
<%--<script src="<%=baseLocation%>scripts/jquery.ui.touch-punch.min.js"></script>--%>
<script src="<%=baseLocation%>scripts/jquery.cookie.min.js"></script>
<script src="<%=baseLocation%>scripts/screenfull.min.js"></script>
<script src="<%=baseLocation%>scripts/jquery.autogrowtextarea.min.js"></script>
<script src="<%=baseLocation%>scripts/jquery.nicescroll.min.js"></script>
<script src="<%=baseLocation%>scripts/bootbox.min.js"></script>
<script src="<%=baseLocation%>scripts/toastr.min.js"></script>
<script src="<%=baseLocation%>scripts/components-setup.min.js"></script>
<!-- END !IMPORTANT DEPENDENCIES -->


<!-- WRAPKIT -->
<script src="<%=baseLocation%>scripts/wrapkit-utils.min.js"></script>
<script src="<%=baseLocation%>scripts/wrapkit-layout.min.js"></script>
<script src="<%=baseLocation%>scripts/wrapkit-header.min.js"></script>
<script src="<%=baseLocation%>scripts/wrapkit-sidebar.min.js"></script>
<script src="<%=baseLocation%>scripts/wrapkit-content.min.js"></script>
<script src="<%=baseLocation%>scripts/wrapkit-footer.min.js"></script>
<script src="<%=baseLocation%>scripts/wrapkit-panel.min.js"></script>
<script src="<%=baseLocation%>scripts/wrapkit-setup.min.js"></script>
<!-- END WRAPKIT -->

<script type="text/javascript" src="<%=baseLocation%>scripts/demo/layout-rtl-demo.js"></script>

<script src="<%=baseLocation%>scripts/prettify.min.js"></script>
<script type="text/javascript" src="<%=baseLocation%>scripts/demo/panel-demo.js"></script>

<!-- DEPENDENCIES form -->
<script src="<%=baseLocation%>scripts/moment.min.js"></script>
<script src="<%=baseLocation%>scripts/handlebars.min.js"></script>
<script src="<%=baseLocation%>scripts/typeahead.bundle.min.js"></script>
<script src="<%=baseLocation%>scripts/select2.min.js"></script>

<script src="<%=baseLocation%>scripts/devs/bootstrap-editable.js"></script>
<script src="<%=baseLocation%>scripts/devs/bootstrap-editable-typeaheadjs.js"></script>
<script src="<%=baseLocation%>scripts/bootstrap-editable-address.min.js"></script>
<!-- DEPENDENCIES -->

<script>
    $( document ).ready(function() {
        $('body.wrapkit-sidebar-right .sidebar .sidebar-nav .nav>li').click(function() {
            setActiveMenu(this);

            var menuBtn = $('header.header .navbar .navbar-toggle-alt');
            if (menuBtn.is(":visible")) {
                menuBtn.click();
            }
        });

        // by removing this cookie we allow the feedback widget to be display always
        $.removeCookie('__svid', { path: '/' });

        $( '#wrapper' ).wrapkitLayout( 'setBox' );
        var $myHeader = $("#myHeader");
        $myHeader.wrapkitHeader( "fixedTop" );
        $myHeader.wrapkitHeader( "setSkin", "belpet" );
        var $sidebar = $("#sidebar-rtl");
        $sidebar.wrapkitSidebar( "fixed", true );
        $sidebar.wrapkitSidebar( "setSkin", "belpet" );

        toastr.options = {
            positionClass: 'toast-bottom-left'
        };

        bootbox.defauls = {
            locale: 'he'
        };

        // X-EDITABLE
        //defaults
        $.fn.editable.defaults.inputclass = 'form-control';
        $.fn.editable.defaults.emptytext = 'לא נבחר';

        function setMinHeight() {
            var newHeight = window.innerHeight -51;
            $( ".wrapkit-wrapper" ).css('min-height', newHeight);
            $( ".content-wrapper" ).css('min-height', newHeight);
        }

        $( window ).resize(function() {//.wrapkit-wrapper, .content-wrapper
            setMinHeight();
        });

        setTimeout(function(){
            $('.full-screen-loader').fadeOut();
            setMinHeight();
        }, 900);
    });
</script>


<%-- bring the image before they need to be load --%>
<div style="display: none">
    <img class="practice-profile-panel-img" src="/wrapkit/images/dummy/TRANSLATION.PNG" alt="profile">
    <img class="practice-profile-panel-img" src="/wrapkit/images/dummy/DICTATION.PNG" alt="profile">
    <img class="practice-profile-panel-img" src="/wrapkit/images/dummy/MATH.PNG" alt="profile">
</div>

</div>

<script type="text/javascript">
    (function(w) {
        w['_sv'] = {trackingCode: 'sdgrcqheRbNWIsFctCOMuvUvoXSRbece'};
        var s = document.createElement('script');
        s.src = '//api.survicate.com/assets/survicate.js';
        s.async = true;
        var e = document.getElementsByTagName('script')[0];
        e.parentNode.insertBefore(s, e);
    })(window);
</script><noscript><a href="http://survicate.com">Website Survey</a></noscript>

<c:if test="${envName.equals('production')}">
    <script>
        (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
                    (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
                m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
        })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

        ga('create', 'UA-70823390-1', 'auto');
        ga('send', 'pageview');
    </script>
</c:if>

<% if (!MobileUtil.isMobile(request.getHeader("User-Agent"))) { %>
<script>!function(){var e=document.createElement("script"),t=document.getElementsByTagName("script")[0];e.async=1,e.src="https://inlinemanual.com/embed/player.f8134b46c5d8ed6659d171ecb1d380b9.js",e.charset="UTF-8",t.parentNode.insertBefore(e,t)}();</script>
<% } %>

</body>

<!-- VENDOR -->
<link rel="stylesheet" href="<%=baseLocation%>styles/jqueryui.min.css">
<link rel="stylesheet" href="<%=baseLocation%>styles/bootstrap.min.css">
<!-- END VENDOR -->

<!-- WRAPKIT -->
<link rel="stylesheet" href="<%=baseLocation%>styles/wrapkit.min.css">
<link rel="stylesheet" href="<%=baseLocation%>styles/wrapkit-skins-all.min.css">
<!-- END WRAPKIT -->

<!-- !IMPORTANT DEPENDENCIES -->
<link rel="stylesheet" href="<%=baseLocation%>styles/font-awesome.min.css">
<link rel="stylesheet" href="<%=baseLocation%>styles/angular-ui-switch.min.css">
<link rel="stylesheet" href="<%=baseLocation%>styles/toastr.min.css">
<link rel="stylesheet" href="<%=baseLocation%>styles/prettify.min.css">
<link rel="stylesheet" href="<%=baseLocation%>styles/xeditable.min.css">
<link rel="stylesheet" href="<%=baseLocation%>styles/datatables.min.css">
<link rel="stylesheet" href="<%=baseLocation%>styles/select2.min.css">
<%--<link rel="stylesheet" href="/ngTable/ng-table.css">--%>
<!-- END !IMPORTANT DEPENDENCIES -->

<link rel="stylesheet" href="/ngTagInput/ng-tags-input.css">
<link rel="stylesheet" href="/ngTagInput/ng-tags-input.bootstrap.css">

<link rel="stylesheet" href="/css/practicer-general.css">
<link rel="stylesheet" href="/css/parent-app.css">
<link rel="stylesheet" href="/css/parent-app-rtl.css">    <%--todo only on RTL mode we should include this css file--%>

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
<script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
<![endif]-->

<script src="/angularjs/1.3.15/angular.min.js"></script>
<script src="/angularjs/1.3.15/angular-route.min.js"></script>
<script src="/angularjs/1.3.15/angular-animate.min.js"></script>
<script src="/js/ui-bootstrap-tpls-0.13.0.min.js"></script>
<script src="/js/angular-sortable-view.min.js"></script>
<script src="<%=baseLocation%>scripts/angular-ui-switch.min.js"></script>
<script src="/angular-cache/dist/angular-cache.min.js"></script>

<script type="text/javascript" src="/ngTagInput/ng-tags-input.js"></script>

<script type="text/javascript" src="/js/app/parent/directives/childInfoPanel.js"></script>
<script type="text/javascript" src="/js/app/parent/directives/singleQuestionPanel.js"></script>
<script type="text/javascript" src="/js/app/parent/directives/groupInfoPanel.js"></script>
<script type="text/javascript" src="/js/app/parent/directives/practiceInfoPanel.js"></script>
<script type="text/javascript" src="/js/app/parent/directives/taskInfoPanel.js"></script>
<script type="text/javascript" src="/js/app/parent/controllers/parentChildsCtrl.js"></script>
<script type="text/javascript" src="/js/app/parent/controllers/parentGroupsCtrl.js"></script>
<script type="text/javascript" src="/js/app/parent/controllers/parentExercisesCtrl.js"></script>
<script type="text/javascript" src="/js/app/parent/exercises-by-groups/parentExercisesByGroupCtrl.js"></script>
<script type="text/javascript" src="/js/app/parent/exercises-by-child/parentExercisesByChildCtrl.js"></script>
<script type="text/javascript" src="/js/app/parent/controllers/parentTasksCtrl.js"></script>
<script type="text/javascript" src="/js/app/parent/parent-profile/parentProfileCtrl.js"></script>
<script type="text/javascript" src="/js/app/parent/controllers/singlePractice.js"></script>
<script type="text/javascript" src="/js/app/common-app.js"></script>
<script type="text/javascript" src="/js/app/parent/parent-app.js"></script>
<script type="text/javascript" src="/js/app/parent/edit-question/editQuestionController.js"></script>
<script type="text/javascript" src="/js/app/parent/select-lang-dialog/selectLangDialogController.js"></script>
<script type="text/javascript" src="/js/app/parent/duplicate-practice-dialog/duplicatePracticeDialog.js"></script>
<script type="text/javascript" src="/js/app/parent/guide/guide-create-practice-dialog/guideCreatePracticeDialog.js"></script>
<script type="text/javascript" src="/js/app/parent/copy-questions-to-exist-practice-dialog/copyQuestionsToExistPractice.js"></script>
<script type="text/javascript" src="/js/app/parent/change-child-pw-dialog/changeChildPwDialog.js"></script>
<script type="text/javascript" src="/js/app/parent/parent-data-service.js"></script>
<script type="text/javascript" src="/ngInfiniteScroll/1.1.0/ng-infinite-scroll.min.js"></script>
</html>