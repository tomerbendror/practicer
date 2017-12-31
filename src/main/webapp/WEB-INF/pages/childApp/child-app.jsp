<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.practice.util.MobileUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en" ng-app="childApp">
<head>
    <base href="/app/child/"> <%-- angular route html5 mode --%>

    <link rel="stylesheet" href="/angular_material/0.11.0/angular-material.min.css">
    <link rel="stylesheet" href="/css/child-app/child-app.css">
    <link rel="stylesheet" href="/css/child-app/child-app-rtl.css"> <%--todo add in olny for rtl lang--%>

    <% if (MobileUtil.isMobile(request.getHeader("User-Agent"))) { %>
    <link rel="stylesheet" href="/css/child-app/child-app-mobile.css">
    <% } %>

    <c:choose>
        <c:when test="${gender == 'FEMALE'}">
            <link rel="stylesheet" href="/css/child-app/child-female.css">
        </c:when>
    </c:choose>

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

        .loader-div {
            position: fixed;
            top: 50%;
            left: 50%;
            margin-left: -45px;
            margin-top: -50px;
        }
    </style>
</head>

<body layout="row" ng-controller="AppCtrl as appCtrl" nav-direction="rtl" style="direction: rtl">

<div class="full-screen-loader">
    <div layout layout-align="center center" class="loader-div">
        <md-progress-circular md-diameter="60"></md-progress-circular>
    </div>
</div>

<c:if test="${parentViewAsChild}">
    <div id="parent-view-as-child" class="parent-view-as-child">
        <span class="md-headline white">${switchToParentMsg}<a onclick="returnToParentView(${childId})">יש ללחוץ כאן</a></span>
    </div>
</c:if>

<md-sidenav layout="column" class="md-sidenav-right md-whiteframe-z2" md-component-id="left" md-is-locked-open="$mdMedia('gt-md') || lockedOpen">
    <md-toolbar class="md-tall md-hue-2">
        <div layout="column" class="md-toolbar-tools-bottom inset">
            <div layout="column" layout-align="center center" class="md-toolbar-tools-bottom user-avatar-div">
                <md-icon class="user-avatar" md-svg-icon="{{userAvatar}}" style="width:64px;height:64px;"></md-icon>
                <div layout-padding>{{userFullName}}</div>
            </div>
        </div>
        <span flex></span>
    </md-toolbar>
    <md-list>
        <md-list-item ng-repeat="item in menu">
            <a style="width: 100%;" data-ng-click="navigateTo(item.link, true)">
                <md-item-content md-ink-ripple layout="row" layout-align="start center">
                    <div class="inset svg-rotate-180-for-rtl">
                        <ng-md-icon icon="{{item.icon}}"></ng-md-icon>
                    </div>
                    <div class="inset md-title white">{{item.title}}
                    </div>
                </md-item-content>
            </a>
        </md-list-item>
        <md-list-item ng-repeat="item in admin">
            <a>
                <md-item-content md-ink-ripple layout="row" layout-align="start center">
                    <div class="inset">
                        <ng-md-icon icon="{{item.icon}}"></ng-md-icon>
                    </div>
                    <div class="inset">{{item.title}}
                    </div>
                </md-item-content>
            </a>
        </md-list-item>
    </md-list>
</md-sidenav>

<div layout="column" class="relative" layout-fill role="main">
    <md-toolbar ng-show="!showSearchFlag">
        <div class="md-toolbar-tools">
            <md-button ng-click="toggleSidenav('left')" hide-gt-md aria-label="Menu" class="round-btn">
                <ng-md-icon icon="menu"></ng-md-icon>
            </md-button>
            <span class="md-headline white">{{pageTitle}}</span>
            <span flex></span>
            <md-button aria-label="Search" ng-click="showSearch()" class="round-btn">
                <ng-md-icon icon="search"></ng-md-icon>
            </md-button>
            <md-menu>
                <md-button aria-label="Open Settings" ng-click="openMoreMenu($mdOpenMenu, $event)" class="round-btn">
                    <ng-md-icon icon="more_vert"></ng-md-icon>
                </md-button>
                <md-menu-content width="4" class="more-menu">
                    <md-menu-item>
                        <md-button ng-click="openProfileDialog($event)">
                            <ng-md-icon icon="person" md-menu-align-target></ng-md-icon>
                            החשבון שלי
                        </md-button>
                    </md-menu-item>
                    <md-menu-divider></md-menu-divider>
                    <md-menu-item>
                        <md-button ng-click="logout()">
                            <ng-md-icon icon="filter_tilt_shift" md-menu-align-target></ng-md-icon> יציאה</md-button>
                    </md-menu-item>
                </md-menu-content>
            </md-menu>
        </div>
    </md-toolbar>

    <md-toolbar class="md-hue-1" ng-show="showSearchFlag">
        <div class="md-toolbar-tools">
            <md-button ng-click="hideSearch()" aria-label="Back" class="round-btn svg-rotate-180-for-rtl">
                <ng-md-icon icon="arrow_back"></ng-md-icon>
            </md-button>
            <h3 hide-sm flex="10">
                חזור
            </h3>
            <md-input-container md-no-float md-theme="input" flex class="top-search">
                <label>&nbsp;</label>
                <input id="serarchField" ng-keyup="searchSubmit($event)" ng-model="searchPracticeStr" placeholder="חיפוש תרגיל">
            </md-input-container>
            <md-button aria-label="Search" ng-click="hideSearch()" class="round-btn">
                <ng-md-icon icon="search"></ng-md-icon>
            </md-button>
            <md-menu>
                <md-button aria-label="Open Settings" ng-click="openMoreMenu($mdOpenMenu, $event)"  class="round-btn">
                    <ng-md-icon icon="more_vert"></ng-md-icon>
                </md-button>
                <md-menu-content width="4" class="more-menu">
                    <md-menu-item>
                        <md-button ng-click="openProfileDialog($event)">
                            <ng-md-icon icon="person" md-menu-align-target></ng-md-icon>
                            החשבון שלי
                        </md-button>
                    </md-menu-item>
                    <md-menu-divider></md-menu-divider>
                    <md-menu-item>
                        <md-button ng-click="logout()">
                            <ng-md-icon icon="filter_tilt_shift" md-menu-align-target></ng-md-icon> יציאה</md-button>
                    </md-menu-item>
                </md-menu-content>
            </md-menu>
        </div>

    </md-toolbar>
    <md-content flex md-scroll-y id='tomer'>
        <ui-view layout="column" layout-fill>
            <div data-ng-view></div>
        </ui-view>
    </md-content>

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
</div>

<script src="<%=baseLocation%>scripts/jquery.min.js"></script>

<!-- Angular Material Dependencies -->
<script src="/angularjs/1.3.15/angular.min.js"></script>
<script src="/angularjs/1.3.15/angular-animate.min.js"></script>
<script src="/angularjs/1.3.15/angular-aria.min.js"></script>
<script src="/angularjs/1.3.15/angular-route.min.js"></script>
<script src="/angular-cache/dist/angular-cache.min.js"></script>

<script src="/angular_material/0.11.0/angular-material.min.js"></script>

<script src="//cdn.jsdelivr.net/angular-material-icons/0.4.0/angular-material-icons.min.js"></script>

<script type="text/javascript" src="/js/app/child/controllers/childAllPracticesController.js"></script>
<script type="text/javascript" src="/js/app/child/controllers/childRecentPracticesController.js"></script>
<script type="text/javascript" src="/js/app/child/controllers/childPracticeController.js"></script>
<script type="text/javascript" src="/js/app/common-app.js"></script>
<script type="text/javascript" src="/js/app/child/child-app.js"></script>

</body>

</html>